# CI/CD Pipeline for DigLib Project

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

## Purpose and Architecture

This document outlines the Continuous Integration and Continuous Deployment (CI/CD) pipeline for the DigLib project, which consists of three main components:
- Backend Service (Spring Boot)
- JavaFX Desktop Client
- React Web Client

The CI/CD pipeline ensures reliable building, testing, and deployment of all components while maintaining security through proper secret management.

### Deployment Architecture

#### Backend Service
- Deployed to Google Cloud App Engine
- Accessible at `api.diglib.no`
- Uses CI variables for sensitive configuration:
  - Database credentials (DB_USERNAME, DB_PASSWORD)
  - GCP configuration (GCP_BUCKET_NAME, GCP_DATABASE_NAME, GCP_INSTANCE_CONNECTION_NAME)
  - Service account keys (GCP_SERVICE_KEY)
  - JDBC connection details (JDBC_URL)
- Application properties are dynamically configured during deployment using Python script
- Region and project settings managed through CI variables (REGION, PROJECT_ID)

#### Web Frontend
- React application deployed through multi-stage process:
  1. Built in GitLab CI
  2. Deployed to GitHub repository using GITHUB_TOKEN
  3. Automatically published by Cloudflare Pages
- Production site available at `diglib.no`
- Deployment configuration managed through CI variables

### CI Variables

The pipeline uses the following protected variables:
```
DB_PASSWORD
DB_USERNAME
GCP_BUCKET_NAME
GCP_DATABASE_NAME
GCP_INSTANCE_CONNECTION_NAME
GCP_SERVICE_KEY
GITHUB_TOKEN (Personal Access Token for GitHub deployment)
JDBC_URL
PROJECT_ID
REGION
```

These variables are securely stored in GitLab and injected into the pipeline during execution. They are masked in logs and only accessible to authorized users.


## Pipeline Structure

The pipeline is organized into four main stages:
1. Build
2. Test
3. Deploy
4. E2E Test

### Global Configuration

- Base image: `maven:3.9-eclipse-temurin-17`
- Maven configuration: Local repository stored in project directory
- Cache configuration: Maven repository cached between pipeline runs
- Deployment rules: Certain jobs only run on the master branch

## Component-Specific Pipelines

### Backend Pipeline
[Backend pipeline YAML configuration](../../ci/backend.yml) 

#### Build Stage
- Job: `build-backend`
- Actions:
  - Builds the backend application using Maven
  - Skips tests during build
  - Produces JAR artifact
- Artifacts expire in 1 hour

#### Test Stage
1. Unit Tests (`unit-test-backend`)
   - Runs unit tests
   - Runs Checkstyle and Spotbugs
   - Produces JUnit reports
   - Generates JaCoCo coverage reports
   - Coverage reports retained for 30 days

2. Integration Tests (`integration-test-backend`)
   - Runs integration test profile
   - Produces JUnit reports
   - Reports retained for 30 days

#### Deploy Stage
- Job: `deploy-backend`
- Uses custom image: `ahallemberg/itp:deploy-backend`
- Prerequisites: Successful build and tests
- Actions:
  - Authenticates with Google Cloud
  - Updates application properties
  - Deploys to Google App Engine
  - Stores deployment version for potential rollback

#### E2E Test Stage
- Job: `deployment-test-backend`
- Runs deployment tests
- Includes automatic rollback on failure

### JavaFX Client Pipeline
[JavaFX pipeline YAML configuration](../../ci/javafx-client.yml) 

#### Build Stage
- Job: `build-javafx-client`
- Builds all JavaFX modules
- Produces multiple artifacts including JARs

#### Test Stage
1. Unit Tests (`unit-test-javafx-client`)
   - Produces JUnit reports
   - Generates JaCoCo coverage reports
   - Reports retained for 30 days

2. Integration Tests (`integration-test-javafx-client`)
   - Uses TestFX for UI testing
   - Includes retry mechanism on failure

#### Deploy Stage
- Job: `build-javafx-debian-application`
- Uses custom image: `ahallemberg/itp:build-javafx-debian-application`
- Creates Debian package
- Uploads package to GitLab Package Registry

#### E2E Test Stage
- Job: `e2e-test-javafx-client`
- Requires successful backend deployment
- Runs end-to-end tests using TestFX

### React Client Pipeline
[React pipeline YAML configuration](../../ci/react-client.yml) 

#### Build Stage
- Job: `build-react-client`
- Uses Node.js latest image
- Performs clean npm install
- Run ESlint and prettier
- Creates production build
- Artifacts expire in 1 hour

#### Test Stage
- Job: `test-react-client`
- Runs Jest tests with coverage reporting
- Generates JUnit-compatible reports
- Coverage reports retained for 30 days

#### Deploy Stage
- Job: `deploy-react-client`
- Deploys to GitHub repository
- Triggers automatic deployment to production environment

## Docker Images

The pipeline uses several custom Docker images:

1. `javafx-client-base`
   - Base image for JavaFX-related jobs
   - Includes required GUI libraries

2. `build-javafx-debian-application`
   - Extends JavaFX base image
   - Includes Debian packaging tools

3. `deploy-backend`
   - Includes Google Cloud SDK
   - Contains Python for configuration management

This was added at merge request [!91](https://gitlab.stud.idi.ntnu.no/it1901/groups-2024/gr2462/gr2462/-/merge_requests/91) to fix GPG key problems on the NTNU Gitlab Runners for the Ubuntu package repository

## Artifacts and Caching

### Artifacts
- Build artifacts: JARs, Debian packages, React build files
- Test reports: JUnit XML, JaCoCo coverage
- Deployment artifacts: Environment variables, configuration files

### Caching
- Maven repository cached between pipeline runs
- Node modules cached for React client
- Cache keys based on commit SHA and branch

## Pipeline Dependencies

The pipeline maintains proper dependencies between jobs:
- Backend deployment requires successful tests
- E2E tests require successful deployment
- Client builds depend on successful backend deployment for integration testing

## Deployment Rules

- Full pipeline runs on master branch only
- Deployment jobs have additional security constraints
- Failed deployment tests trigger automatic rollback for backend