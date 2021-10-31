-- this file for Testcontainers postgres
create table employee (id SERIAL NOT NULL, department VARCHAR(255) NOT NULL, name VARCHAR(255) NOT NULL, PRIMARY KEY (id));
insert into employee (id, name, department) values (1, 'taro', 'sales');
insert into employee (id, name, department) values (2, 'jiro', 'human resources');
