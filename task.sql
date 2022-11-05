/* задачи 2.3 */

/* Уникальный номер сотрудника, его ФИО и стаж работы – для всех сотрудников компании */
select id, name, CURRENT_DATE - start_date from employee;
/* Уникальный номер сотрудника, его ФИО и стаж работы – только первых 3-х сотрудников (самые 'старички' в компании) */
select id, name, CURRENT_DATE - start_date from employee order by start_date limit 3;
/* Уникальный номер сотрудников - водителей */
select id from employee where position = 'driver';
/* Выведите номера сотрудников, которые хотя бы за 1 квартал получили оценку D или E */
select distinct (employee_id) from grade where grade.grade in ('D', 'E') order by employee_id;
/* Выведите самую высокую зарплату в компании */
select max(salary) from employee;
/* Выведите название самого крупного отдела */
select name from department order by number_of_employees desc limit 1;
/* Выведите номера сотрудников от самых опытных до вновь прибывших. Можно считать самыми опытним в компании тех,
   кто работает дольше всех. Тогда запрос простой: */
select id from employee order by start_date;
/* Если учитывать уровень сотрудника: */
select id from employee
join unnest(array ['lead', 'senior', 'middle', 'jun']) with ordinality t(level) using (level)
order by (t.level, start_date);

select DATE_PART('day', now() - start_date)

/* задачи 2.4 */
/* Попробуйте вывести не просто самую высокую зарплату во всей команде, а вывести именно фамилию сотрудника с самой высокой зарплатой */
select name from employee order by salary desc limit 1;
/* Попробуйте вывести фамилии сотрудников в алфавитном порядке */
select name from employee order by name;
/* Рассчитайте средний стаж для каждого уровня сотрудников */
select level, avg(CURRENT_DATE - start_date) from employee group by level;
/* Выведите фамилию сотрудника и название отдела, в котором он работает */
select d.name, e.name from employee e inner join department d on e.department_id = d.id
/* Выведите название отдела и фамилию сотрудника с самой высокой зарплатой в данном отделе и саму зарплату также. */
select e.name "employee name", d.name "department name", e.salary
from employee e
    inner join department d on e.department_id = d.id
where (department_id, salary) in (
    select department_id, max(salary) from employee group by (department_id));