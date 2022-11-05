create table employee
(
    id            integer not null
        constraint employee_pk
            primary key,
    name          varchar,
    start_date    timestamp with time zone,
    position      varchar,
    level         varchar,
    salary        numeric,
    department_id integer,
    has_access    boolean
);

alter table employee
    owner to postgres;

create index employee_id_index
    on employee (id);

create table department
(
    id                  integer not null
        constraint department_pk
            primary key,
    name                varchar,
    head_of_department  varchar,
    number_of_employees integer
);

alter table department
    owner to postgres;

create table grade
(
    employee_id integer
        constraint bonus_employee_id_fk
            references employee
            on update cascade on delete cascade,
    year        integer,
    quarter     integer,
    grade       char
);

alter table grade
    owner to postgres;

