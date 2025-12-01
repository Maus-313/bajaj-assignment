package com.bajaj.qualifier.service;

import org.springframework.stereotype.Component;

@Component
public class SqlQueryProvider {

    private static final String FINAL_QUERY = """
            WITH filtered_payments AS (
                SELECT p.emp_id,
                       SUM(p.amount) AS total_salary
                FROM payments p
                WHERE EXTRACT(DAY FROM p.payment_time) <> 1
                GROUP BY p.emp_id
            ), ranked AS (
                SELECT d.department_name,
                       fp.total_salary,
                       CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
                       DATE_PART('year', AGE(CURRENT_DATE, e.dob)) AS age,
                       ROW_NUMBER() OVER (PARTITION BY d.department_id ORDER BY fp.total_salary DESC) AS rn
                FROM filtered_payments fp
                JOIN employee e ON e.emp_id = fp.emp_id
                JOIN department d ON d.department_id = e.department
            )
            SELECT department_name,
                   total_salary AS salary,
                   employee_name,
                   age
            FROM ranked
            WHERE rn = 1
            ORDER BY department_name;
            """;

    public String provide() {
        return FINAL_QUERY.strip();
    }
}
