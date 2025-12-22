# Integration Tests - Recipe App

## Test Database Setup

### 1. Create the test database

```bash
# Execute the SQL script
psql -U postgres -f setup-test-database.sql

# Or manually:
sudo -i -u postgres
psql
CREATE DATABASE recipeTestDB;
\q
exit
```

### 2. Configure application-test.properties

Edit the `src/main/resources/application-test.properties` file with your PostgreSQL credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/recipeTestDB
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
```

## Test Structure

```
src/test/java/com/recipeapp/
├── BaseIntegrationTest.java           # Base class for all tests
├── controller/
│   ├── AuthControllerIntegrationTest.java
│   └── RecipeControllerIntegrationTest.java
```

## Running the Tests

### All tests
```bash
mvn test
```

### Specific tests
```bash
# Authentication tests only
mvn test -Dtest=AuthControllerIntegrationTest

# Recipe tests only
mvn test -Dtest=RecipeControllerIntegrationTest
```

### With Maven Wrapper
```bash
./mvnw test
```

## Test Coverage

### AuthController (7 tests)
- ✅ Register new user
- ✅ Register with duplicate email
- ✅ Login with valid credentials
- ✅ Login with invalid password
- ✅ Login with non-existent user
- ✅ Password reset
- ✅ Password reset for non-existent user

### RecipeController (15 tests)
- ✅ Get all public recipes
- ✅ Get all recipes (authenticated)
- ✅ Get all recipes (unauthenticated)
- ✅ Get recipe by ID
- ✅ Get non-existent recipe
- ✅ Create recipe (authenticated)
- ✅ Create recipe (unauthenticated)
- ✅ Update recipe (owner)
- ✅ Update recipe (non-owner)
- ✅ Delete recipe (owner)
- ✅ Delete recipe (non-owner)
- ✅ Search by name
- ✅ Search by name (no results)
- ✅ Search by ingredient
- ✅ Search by type
- ✅ Get my recipes
- ✅ Get my recipes (user with no recipes)

## Test Features

### BaseIntegrationTest
- Automatic database cleanup between tests
- Test users creation (USER and ADMIN)
- Utility methods for JSON and authentication

### Automatic Cleanup
The database is **automatically cleaned** before each test thanks to:
- `@Transactional`: Automatic rollback after each test
- `spring.jpa.hibernate.ddl-auto=create-drop`: Schema recreation on each run
- `setUp()` method: Explicit data cleanup

## Debugging

### View SQL queries
SQL queries are logged to the console via: