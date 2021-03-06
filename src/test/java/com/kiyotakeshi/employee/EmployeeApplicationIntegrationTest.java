package com.kiyotakeshi.employee;

import com.kiyotakeshi.employee.entity.Employee;
import com.kiyotakeshi.employee.entity.EmployeeRequest;
import com.kiyotakeshi.employee.repository.EmployeeRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@ActiveProfiles("it")
class EmployeeApplicationIntegrationTest {

    private static final String KEY = "employee";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Container
    private static final GenericContainer REDIS =
            new GenericContainer(DockerImageName.parse("redis:6.2.6-alpine")).withExposedPorts(6379);

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(DockerImageName.parse("postgres:11.10-alpine"))
            .withUsername("testcontainers")
            .withPassword("passw0rd!")
            .withDatabaseName("testcontainers");

    @DynamicPropertySource
    static void setupProperties(DynamicPropertyRegistry registry) {
        REDIS.start();
        registry.add("spring.redis.host", REDIS::getContainerIpAddress);
        registry.add("spring.redis.port", REDIS::getFirstMappedPort);

        POSTGRES.start();
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    private String getTestBaseUrl() {
        return "http://localhost:" + port + "/employees";
    }

    private ResultSet performQuery(DataSource dataSource, String sql) throws SQLException {
        Statement statement = dataSource.getConnection().createStatement();
        statement.execute(sql);
        ResultSet resultSet = statement.getResultSet();
        resultSet.next();
        return resultSet;
    }

    @BeforeAll
    static void beforeAll() {
    }

    @BeforeEach
    void setUp() {
        redisTemplate.delete(KEY);
    }

    // @see https://stackoverflow.com/questions/59372048/testcontainers-hikari-and-failed-to-validate-connection-org-postgresql-jdbc-pgc
    @Test
    void testPostgresConnection() throws SQLException {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(POSTGRES.getJdbcUrl());
        hikariConfig.setUsername(POSTGRES.getUsername());
        hikariConfig.setPassword(POSTGRES.getPassword());
        hikariConfig.setDriverClassName(POSTGRES.getDriverClassName());
        var dataSource = new HikariDataSource(hikariConfig);

        ResultSet resultSet = performQuery(dataSource, "select 'hello'");
        assertEquals("hello", resultSet.getString(1));
        resultSet.close();

        // verify loading from test data
        ResultSet testData = performQuery(dataSource, "select * from employee");
        assertEquals("test-taro", testData.getString("name"));
        assertEquals("sales", testData.getString("department"));
        testData.next();
        assertEquals("test-jiro", testData.getString("name"));
        assertEquals("human resources", testData.getString("department"));
        testData.close();
    }

    @Test
    void getEmployees() {
        ResponseEntity<String> response = this.restTemplate.getForEntity(getTestBaseUrl(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        String expected = """
                [{"id":1,"name":"test-taro","department":"sales"},{"id":2,"name":"test-jiro","department":"human resources"}]""";
        assertEquals(expected, response.getBody());

        ResponseEntity<Employee[]> result = this.restTemplate.getForEntity(getTestBaseUrl(), Employee[].class);
        assertEquals(2, result.getBody().length);
        assertEquals("test-taro", result.getBody()[0].getName());

//        // if check cache @Cacheable at findEmployees()
//        List<Employee> cachedEmployees = new ArrayList<>();
//        Set keys = this.redisTemplate.keys("employee*");
//        keys.forEach(k -> {
//            ((ArrayList<Employee>) this.redisTemplate.opsForValue().get(k))
//                    .forEach(
//                            e -> cachedEmployees.add(e)
//                    );
//        });
//        assertEquals("test-taro", cachedEmployees.get(0).getName());
//        assertEquals("sales", cachedEmployees.get(0).getDepartment());
//        assertEquals("test-jiro", cachedEmployees.get(1).getName());
//        assertEquals("human resources", cachedEmployees.get(1).getDepartment());
    }

    @Test
    void getEmployee() {
        int requestId = 1;
        ResponseEntity<Employee> response = this.restTemplate.getForEntity(getTestBaseUrl() + "/{id}", Employee.class, requestId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(requestId, response.getBody().getId());
        assertEquals("test-taro", response.getBody().getName());
        assertEquals("sales", response.getBody().getDepartment());

        // check redis cache
        var cachedEmployee = (Employee) this.redisTemplate.opsForValue().get("employee::" + requestId);
        assertEquals(requestId, cachedEmployee.getId());
        assertEquals("test-taro", cachedEmployee.getName());
        assertEquals("sales", cachedEmployee.getDepartment());
    }

    @Test
    void addEmployee() {
        var request = new EmployeeRequest("kendrick", "general affairs");
        ResponseEntity<Employee> response = this.restTemplate.postForEntity(getTestBaseUrl(), request, Employee.class);
        assertEquals("kendrick", response.getBody().getName());

        // check postgres data
        Employee found = employeeRepository.findByName(request.getName()).orElseThrow();

        // @see src/test/resources/db/schema-postgresql.sql
        assertEquals(101, found.getId());
        assertEquals("general affairs", found.getDepartment());
    }

    @Test
    @DirtiesContext
    void updateEmployee() {
        int requestId = 1;
        var request = new EmployeeRequest("name-update", "vice president");

        // this.restTemplate.put(getTestBaseUrl() + "/{id}", request, 1);
        // @see https://github.com/spring-projects/spring-boot/issues/12009#issuecomment-365051611
        ResponseEntity<Employee> response = this.restTemplate.exchange(
                getTestBaseUrl() + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<EmployeeRequest>(request),
                Employee.class, requestId);

        assertEquals(request.getName(), response.getBody().getName());

        // check postgres
        var fromDB = employeeRepository.findById(requestId).orElseThrow();
        assertNotNull(fromDB);
        assertEquals(request.getDepartment(), fromDB.getDepartment());

        // check redis cache
        var cachedEmployee = (Employee) this.redisTemplate.opsForValue().get("employee::" + requestId);
        assertEquals(request.getName(), cachedEmployee.getName());
        assertEquals(request.getDepartment(), cachedEmployee.getDepartment());
    }

    @Test
    @DirtiesContext
    void deleteEmployee() {
        int requestId = 1;

        ResponseEntity<Employee> response = this.restTemplate.getForEntity(getTestBaseUrl() + "/{id}", Employee.class, requestId);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        // check redis cache exist
        var cachedEmployee = (Employee) this.redisTemplate.opsForValue().get("employee::" + requestId);
        assertEquals(requestId, cachedEmployee.getId());

        ResponseEntity<String> result = this.restTemplate.exchange(
                getTestBaseUrl() + "/{id}",
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                String.class, requestId);

        assertEquals(HttpStatus.OK, result.getStatusCode());

        // check postgres
        assertTrue(employeeRepository.findById(requestId).isEmpty());

        // check redis cache evict
        assertNull(this.redisTemplate.opsForValue().get("employee::" + requestId));
    }
}
