# Google Cloud Platform Setup Guide for DigLib

>&#8203;    
>[Home](../../README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
[About](../../docs/README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Development-Tools](../../docs/release3/development-tools.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[API](../../docs/release3/api-calls.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Workhabits](../../docs/release3/workflow.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Tests](../../docs/release3/tests.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[JavaFX](../../client/diglib-javafx/README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[React](../../client/diglib-react/README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Backend](../../backend/README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[CI/CD](../../docs/release3/gitlab-ci.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Google-Cloud](../../docs/release3/gcloud-setup.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Cloudflare](../../docs/release3/cloudflare-setup.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Use-cases](../../docs/release3/usercase.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;    
>&#8203;    

## Prerequisites
- Google Cloud Console account with billing enabled
- Spring Boot application built with Java 17
- GitLab CI/CD pipeline access

## Project Components
The project consists of the following Google Cloud Platform components:
- App Engine (Java 17 runtime)
- Cloud SQL (MySQL 8.0)
- Cloud Storage bucket
- Required service APIs

## Initial Setup in Google Cloud Console

### 1. Project Creation
1. Navigate to the Google Cloud Console
2. Create a new project named "diglib-439508"
3. Select the region "europe-west3"

### 2. Enable Required APIs
Enable the following APIs through the Google Cloud Console:
- App Engine API
- Cloud SQL Admin API
- Cloud Storage API
- Secret Manager API

### 3. Cloud SQL Setup
1. Create a MySQL 8.0 instance named "diglib-db"
2. Configure the following:
   - Region: europe-west3
   - Instance ID: diglib-db
   - Database version: MySQL 8.0
   - Create database user with secure password
   - Note down the instance connection name

### 4. Cloud Storage Setup
1. Create a new bucket
2. Configure:
   - Name: diglib-bucket
   - Region: europe-west3
   - Uniform bucket-level access: Enabled

## Application Configuration

### 1. App Engine Configuration
Create `app.yaml` in your project root:

```yaml
runtime: java17
instance_class: F2
beta_settings:
  cloud_sql_instances: "diglib-439508:europe-west3:diglib-db"
entrypoint: java -Xmx512m -jar diglib-0.0.1-SNAPSHOT.jar
```

### 2. Application Properties
The application uses a template `application.properties` file with environment variable placeholders:

```properties
spring.datasource.url=${JDBC_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.cloud.gcp.sql.instance-connection-name=${GCP_INSTANCE_CONNECTION_NAME}
spring.cloud.gcp.sql.database-name=${GCP_DATABASE_NAME}
spring.cloud.gcp.storage.bucket-name=${GCP_BUCKET_NAME}
spring.cloud.gcp.project-id=${PROJECT_ID}
```

### 3. GitLab CI Variables
Configure the following variables in GitLab CI/CD Settings:
- `JDBC_URL`: JDBC connection URL for Cloud SQL
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password
- `GCP_INSTANCE_CONNECTION_NAME`: Cloud SQL instance connection name
- `GCP_DATABASE_NAME`: Database name
- `GCP_BUCKET_NAME`: Cloud Storage bucket name
- `PROJECT_ID`: GCP project ID

## Deployment

The project includes a Python script that handles sensitive property replacement. This script:
1. Creates a backup of the original properties file
2. Replaces sensitive values with environment variable placeholders
3. Updates the properties file with actual values from environment variables during deployment

To see more about the deployment in the Gitlab CI Pipeline, see [CI/CD documentation](../../docs/release3/gitlab-ci.md) 