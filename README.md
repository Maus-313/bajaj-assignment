# Bajaj Finserv Health Qualifier – Java Submission

## Overview
This Spring Boot (3.2.5) console application automates the entire qualifier workflow:

1. Generates a dedicated webhook + JWT token from the Bajaj Finserv Health hiring API.
2. Computes the requested SQL solution for Question 1 (odd registration number) entirely in code.
3. Persists the final SQL query to `target/generated-sql-solution.sql` for audit/reference.
4. Posts the solution (`finalQuery`) to the returned webhook URL, authenticating the call with the received JWT.

The flow runs automatically at application startup, so no controller endpoints are exposed per the assignment requirements.

## Data Problem & Final SQL
The task asks for the highest salaried employee **per department**, ignoring any payments made on the 1st day of a month. The query also needs the concatenated employee name and their age.

```sql
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
```

This query is embedded in `SqlQueryProvider` and mirrored inside [`FINAL_QUERY.sql`](FINAL_QUERY.sql).

## Key Components
- [`QualifierWorkflow`](src/main/java/com/bajaj/qualifier/service/QualifierWorkflow.java) orchestrates the startup flow using `RestTemplate`.
- [`SqlQueryProvider`](src/main/java/com/bajaj/qualifier/service/SqlQueryProvider.java) encapsulates the final SQL answer.
- [`SolutionStorageService`](src/main/java/com/bajaj/qualifier/service/SolutionStorageService.java) writes the SQL to `target/generated-sql-solution.sql` every time the app runs.
- [`RestClientConfig`](src/main/java/com/bajaj/qualifier/config/RestClientConfig.java) exposes a singleton `RestTemplate` bean.

## Prerequisites
- JDK 17+
- Internet access (required for Maven downloads and the assignment APIs)

No system-wide Maven installation is necessary because the project ships with the Maven Wrapper.

## Build
```bash
./mvnw clean package
```
The runnable Spring Boot JAR will be produced at `target/bfh-qualifier-java-0.0.1-SNAPSHOT.jar`.

## Run
```bash
java -jar target/bfh-qualifier-java-0.0.1-SNAPSHOT.jar
```
Running the jar will:
1. Call `https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA` with your personal details.
2. Persist the SQL query under `target/generated-sql-solution.sql`.
3. Submit `{"finalQuery": "..."}` to the webhook URL using the returned JWT via the `Authorization: Bearer <token>` header.

Logs include success/failure messages for both HTTP calls.

## Submission Checklist
When creating the GitHub submission:
1. Commit the entire repository **including**:
   - `target/bfh-qualifier-java-0.0.1-SNAPSHOT.jar`
   - `FINAL_QUERY.sql`
   - Wrapper files (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/*`)
2. Push to a public GitHub repo and capture:
   - Repository URL (e.g., `https://github.com/your-username/bfh-qualifier-java.git`).
   - Raw download link for the jar (e.g., `https://raw.githubusercontent.com/your-username/bfh-qualifier-java/main/target/bfh-qualifier-java-0.0.1-SNAPSHOT.jar`).
3. Fill in the Microsoft Form with both the repo link and the raw JAR link.

## Notes
- The application is headless (`spring.main.web-application-type=none`) so it exits immediately after submission.
- Errors (network failures, invalid payloads) are logged but do not crash the JVM abruptly; they simply stop the workflow.
- Adjust the personal details inside [`GenerateWebhookRequest`](src/main/java/com/bajaj/qualifier/dto/GenerateWebhookRequest.java) if needed before building.
