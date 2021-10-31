--auto-increment from 1
create table employee (id SERIAL NOT NULL, department VARCHAR(255) NOT NULL, name VARCHAR(255) NOT NULL, PRIMARY KEY (id));

--if custom auto-increment start value
-- create sequence employee_id_seq start 10001;
-- create table employee (id INT NOT NULL DEFAULT nextval('employee_id_seq'), department VARCHAR(255) NOT NULL, name VARCHAR(255) NOT NULL, PRIMARY KEY (id));
-- select seqstart from pg_sequence; -- 10001
