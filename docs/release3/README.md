
# Release 3

>&#8203;    
>[Home](../../README.md)    
>&#8203;    

## Release notes

* #### [Ai-tools](ai-tools.md)
* #### [Challenges](challenges.md)
* #### [Sustainability](sustainability.md)
* #### [Contribution](contribution.md)
* #### [Further Development](further-development.md)


## Important features of this release

### Project Restructure (#49)
Restructured into backend and client modules to improve separation and scalability.

### React Client Implementation (#50)
Built a new React client for a modern, responsive user experience.

### RESTful JavaFX Client (#51)
Enhanced JavaFX client to communicate over REST, aligning it with the backend.

### Containerized RESTful Backend (#52)
Created a Dockerized backend with REST endpoints, simplifying deployment.

### Fixed Build And Test Stages For CI/CD Gitlab Pipeline To Work With New Project Structure (#53)
Adjusted the GitLab CI/CD pipeline to work with the new project structure.

### React Client CI/CD Pipeline (#54)
Established a GitHub Actions pipeline for the React client, automating build and deployment.

### Comprehensive E2E Tests (#64)
Added end-to-end tests to ensure robust functionality across modules.

### Backend Integration Tests (#65)
Developed integration tests for backend services to catch cross-service issues.

### CORS Configuration (#74)
Implemented CORS settings in the backend to allow client-server interaction.

### Improved Error Handling in React Client (#86)
Enhanced user experience with better error feedback in the React client.

### Added API service in React Client (#59)
Implemented a service to interact with the backend API in the React client.

### Implemented deployment test (#84)
Added a deployment test to ensure the application is working as expected.

### Added E2E Test For JavaFX Client (#92)
Added an end-to-end test for the JavaFX client to ensure robust functionality.

### Jpackaged Application Does Now Support https (#115) 
The JavaFX client now supports HTTPS, improving security for users.

### DevContainer Now Able To Package And Display JavaFX (#117)
The DevContainer now able to package JavaFX
