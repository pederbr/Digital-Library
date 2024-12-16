# Backend

>&#8203;    
>[Home](../README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
[About](../docs/README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Development-Tools](../docs/release3/development-tools.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[API](../docs/release3/api-calls.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Workhabits](../docs/release3/workflow.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Tests](../docs/release3/tests.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[JavaFX](../client/diglib-javafx/README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[React](../client/diglib-react/README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Backend](../backend/README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[CI/CD](../docs/release3/gitlab-ci.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Google-Cloud](../docs/release3/gcloud-setup.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Cloudflare](../docs/release3/cloudflare-setup.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Use-cases](../docs/release3/usercase.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;     
>&#8203; 

## Backend Overview

### REST Server

The backend is implemented as a RESTful server using Spring Boot. It provides endpoints for managing books, including adding, deleting, and fetching book details and content. The server handles requests and responses in JSON format and supports pagination and sorting for book listings.

### Deployment to Google Cloud

The backend is deployed to Google Cloud Platform (GCP) using Google Cloud Storage for storing book contents and Google Cloud SQL for the database. The deployment process involves setting up the necessary GCP services and configuring the application to use these services.

### Database

The backend uses Google Cloud SQL as its database, with Hibernate ORM for database interactions. The database schema is automatically generated based on the JPA entity classes. The `BookRepository` interface extends Spring Data's `PagingAndSortingRepository` and `CrudRepository` to provide CRUD operations and pagination support.

### Logging

Logging is implemented using SLF4J with Logback as the underlying logging framework. Logs are generated for various operations, including adding, deleting, and fetching books, as well as handling errors. The logs provide detailed information about the application's behavior and are useful for debugging and monitoring.

### Environment Variables Injection

A Python script (`replace.py`) is used to inject environment variables from GitLab CI/CD into the backend's configuration. The script replaces sensitive values in the `application.properties` file with placeholders and then updates these placeholders with actual environment variables. This ensures that sensitive information, such as database credentials and GCP configuration, is securely managed and injected at runtime.

## Running backend

Since the backend is deployed to Google Cloud Platform (GCP), we do not provide a feature for running the backend locally. Additionally, hosting the server locally would be problematic because the environment variables are only available in the GitLab repository. These variables are sensitive keys that should not be tracked by Git or exposed to other users. We have concluded that it is more appropriate to maintain high quality on our deployed server rather than adding functionality for running the server locally. Much of DigLib's functionality relies on the ability for everyone to add and read books from an external server.

## Running tests

### Dependencies

- Java 17
- Maven 3.8/3.9

To run the tests for the backend, follow these steps:

1. **Navigate to the backend directory from the root (gr2462)**:

    ```sh
    cd backend
    ```

2. **Run Unit Tests**:

    ```sh
    mvn test
    ```

3. **Run Integration Tests**:

    ```sh
    mvn verify -P integration-tests
    ```

4. **Run Deployment Tests**:

    ```sh
    mvn verify -P deployment-tests
    ```

For more information about how the tests work press [here](../docs/release3/tests.md)
