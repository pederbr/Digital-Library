# Contribution

>&#8203;    
>[Go back](./README.md)    
>&#8203;  


## Sindre

For the first release, my primary focus was on coding the backend, specifically working on the persistence layer. I also contributed to the frontend, particularly by helping implement methods to display data in a user-friendly format. Together with Peder, I shared responsibility for writing tests. I focused primarily on unit tests, as well as an integration test using FXRobot in JavaFX.

In the second release my primary resonsibility was testing our code. Creating unit tests for our javafx client with a high test coverage. Aswell as including additional integration tests using FXRobot to tests if the different components worked properly together.

In the final release, my focus shifted to migrating the client side of the project to React. Ask structured the new project layout, while I took responsibility for implementing all React-related code. I learned and applied JavaScript on the frontend to bring the React client in line with the functionality of our previous JavaFX application. Alongside Jørgen, I developed methods to enable React to send API calls to our database. Together, we also addressed challenges related to CORS policy when working with localhost. Finally, I wrote unit and integration tests for the React client, as well as documentation to support its use and development.

## Ask

In the first release, I took responsibility for establishing the development environment for the team and implementing containerization of the application. This foundational work ensured consistent development experiences across the team and simplified deployment processes.

For the second release, I led the restructuring effort to transform the project into a multi-modular architecture, improving code organization and maintainability. I also contributed to the persistent storage implementation and established our CI pipeline, setting up automated testing and deployment workflows.

In the final release, I continued my DevOps focus by enhancing our development infrastructure. I restructured the project to accommodate both the JavaFX and React clients, configured the CI pipeline for multiple deployment targets, and successfully implemented cloud deployment solutions - setting up the backend on Google Cloud and the React client on Cloudflare Pages. Working collaboratively with Peder and Jørgen, I contributed to developing and refining the backend implementation. Additionally, I managed the Maven configuration for both the JavaFX and backend modules, ensuring smooth build processes and dependency management. Throughout the project, I maintained a strong DevOps perspective, focusing on automation, deployment efficiency, and maintaining a robust development infrastructure.

## Peder 

Throughout this project, I took primary responsibility for the JavaFX client, developing most of the frontend code and writing tests for it. I also ensured that the JavaFX client followed the Model-View-Controller principles. Additionally, I facilitated fair task distribution within the team, ensuring that workload was balanced and that team members could leverage their strengths effectively.

During the first and second release I designed the application layout and implemented the logic in the controller class, making sure the application functioned as intended. I also focused on modularization to simplify the separation of logic, and I contributed to setting up static code analysis tools.During the third release, I created the initial draft of the backend application, which was further developed by team members including Ask and Jørgen. Additionally, I worked on enabling communication between the JavaFX client and the REST API.


## Jørgen

I joined the project later due to military service during the initial phase. My early contributions included documenting our second release and developing the core logic for calculating key information about the book. In the third release, I collaborated with Peder on building the REST server, then implemented logging and worked with Sindre to connect the API to our React app. To enable seamless communication between the React frontend and the REST server, I addressed CORS-related restrictions to allow the React frontend to communicate smoothly with the REST server. I developed unit and integration tests to ensure reliability and performance in our REST server.


Throughout the project, I have focused on guiding the team toward effective solutions, keeping architectural integrity and best practices front and center. Additionally, I worked closely with Ask to ensure our DevOps pipelines ran smoothly, contributing to a stable and efficient development process.