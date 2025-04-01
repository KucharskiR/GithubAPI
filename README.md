# GitHub Repository API - Recruitment Task

This project is a simple REST API that retrieves a list of public repositories for a given GitHub user, excluding forks. For each repository, the API provides the repository name, owner login, and information about each branch (name and SHA of the last commit).

## Table of Contents
- [Requirements](#requirements)
- [Project Structure](#project-structure)
- [Implementation Details](#implementation-details)
- [How to Run](#how-to-run)
- [API Documentation](#api-documentation)
- [Testing](#testing)

## Requirements

- Java 21
- Maven
- Spring Boot 3.2.x

## Project Structure

```
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── githubapi
│   │   │               ├── GithubApiApplication.java
│   │   │               ├── controller
│   │   │               │   └── GithubController.java
│   │   │               ├── exception
│   │   │               │   ├── ErrorResponse.java
│   │   │               │   ├── GithubApiException.java
│   │   │               │   └── GlobalExceptionHandler.java
│   │   │               ├── model
│   │   │               │   ├── Branch.java
│   │   │               │   ├── BranchDetails.java
│   │   │               │   ├── Commit.java
│   │   │               │   ├── GithubRepository.java
│   │   │               │   └── RepositoryResponse.java
│   │   │               └── service
│   │   │                   └── GithubService.java
│   │   └── resources
│   │       └── application.properties
│   └── test
│       └── java
│           └── com
│               └── example
│                   └── githubapi
│                       ├── controller
│                       │   └── GithubControllerTest.java
│                       └── service
│                           └── GithubServiceTest.java
├── pom.xml
└── README.md
```

## Implementation Details

### Main Components

1. **Controller Layer**: Handles HTTP requests and responses
2. **Service Layer**: Contains business logic and communicates with GitHub API
3. **Model Classes**: Define data structures for both GitHub API and our API responses
4. **Exception Handling**: Global exception handler for proper error responses

### Technologies Used

- Spring Boot 3.2.x
- Spring Web (for REST endpoints)
- RestTemplate (for HTTP communication with GitHub API)
- Lombok (to reduce boilerplate code)
- JUnit 5 with Spring Test (for integration testing)

## How to Run

1. Ensure you have Java 21 installed:
   ```
   java --version
   ```

2. Clone the repository:
   ```
   git clone https://github.com/yourusername/github-api-task.git
   cd github-api-task
   ```

3. Build the project with Maven:
   ```
   ./mvnw clean package
   ```

4. Run the application:
   ```
   ./mvnw spring-boot:run
   ```
   
   Alternatively, you can run the JAR file:
   ```
   java -jar target/github-api-0.0.1-SNAPSHOT.jar
   ```

5. The application will start on port 8080 by default.

## API Documentation

### Endpoint

```
GET /api/repos/{username}
```

Where `{username}` is the GitHub username for which you want to retrieve repositories.

### Success Response

- **Status Code**: 200 OK
- **Body**: Array of repository objects

```json
[
  {
    "name": "repository-name",
    "owner": "owner-login",
    "branches": [
      {
        "name": "branch-name",
        "lastCommitSha": "commit-sha"
      }
    ]
  }
]
```

### Error Response (User Not Found)

- **Status Code**: 404 Not Found
- **Body**: Error object

```json
{
  "status": 404,
  "message": "Użytkownik GitHub nie istnieje."
}
```

## Testing

Integration tests cover the following scenarios:
- Retrieving repositories for an existing user (happy path)
- Error handling for a non-existent user (404 response)
- Proper filtering of fork repositories

To run tests:
```
./mvnw test
```