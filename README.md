# Bajaj Finserv Health Qualifier – Java Submission

A Spring Boot console application that generates a SQL solution for the qualifier task and submits it via API.

## Prerequisites
- JDK 17+
- Internet access

## How to Run

1. **Build the project:**
   ```bash
   ./mvnw clean package
   ```

2. **Run the application:**
   ```bash
   java -jar target/bfh-qualifier-java-0.0.1-SNAPSHOT.jar
   ```

The application will automatically:
- Generate a webhook and JWT token
- Compute the SQL query
- Save the query to `target/generated-sql-solution.sql`
- Submit the solution to the webhook with JWT authentication

## Project Structure
- `src/main/java/com/bajaj/qualifier/` - Main application code
- `FINAL_QUERY.sql` - The SQL solution
- `pom.xml` - Maven configuration
