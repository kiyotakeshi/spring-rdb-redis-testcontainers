-- this file for Testcontainers postgres

-- auto-increment from 1
-- create table employee (id SERIAL NOT NULL, department VARCHAR(255) NOT NULL, name VARCHAR(255) NOT NULL, PRIMARY KEY (id));

-- if custom auto-increment start value
create sequence employee_id_seq start 101;
create table employee (id INT NOT NULL DEFAULT nextval('employee_id_seq'), department VARCHAR(255) NOT NULL, name VARCHAR(255) NOT NULL, PRIMARY KEY (id));
insert into employee (id, name, department) values (1, 'taro', 'sales');
insert into employee (id, name, department) values (2, 'jiro', 'human resources');
