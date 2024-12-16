# Challenges we faced during development

>&#8203;    
>[Go back](./README.md)    
>&#8203;  

## CORS policy issues

We encountered challenges with CORS (Cross-Origin Resource Sharing) policies while developing our digital library application, specifically with API requests between domains. Browsers impose strict restrictions to prevent requests to different domains, and our server’s CORS policy blocked API calls from external domains, impacting our app's functionality.

Our initial solution involved adding a proxy configuration in our React app. This setup directed requests through a local proxy, aligning the domain with the server when hosted on localhost. While this approach enabled API calls during local development, it failed to work on the deployed site. We initially assumed it would seamlessly transition to production, but this was not the case.

To address the issue in deployment, we modified our REST server configuration to permit requests specifically from the deployed URL `diglib.no`. This change allowed our application to access the server while maintaining control over authorized origins.

This experience has reinforced the importance of understanding CORS and configuring API access securely in web applications. Without proper CORS setup, servers will deny requests from other domains by default. We also learned the significance of balancing security and accessibility, as proper CORS management is essential for enabling cross-domain functionality while protecting server resources.

## File handling in release 2

During release 2, we encountered a significant issue with file pathing due to differences in how Maven handles project execution. Specifically, running the program from the fxui folder rather than the source folder disrupted file path references that were set up with the source folder in mind. Much of the project’s pathing logic assumed that files and resources would be accessed from the source folder, so running from the fxui folder caused these references to fail. 

This complication arose because persistence was a separate module, which prevented us from running JavaFX directly from the fxui folders. Attempting to do so complicated pathing. Initially, we tried to run the project from the root directory, but this approach failed because Maven required JavaFX to be executed from the same module as the application.

Our next attempt involved injecting the correct paths from the JavaFX controller to the persistence module, which ultimately allowed the project to function. However, this solution was temporary. In release 3, we resolved the issue by re-architecting the backend as a REST API, allowing it to operate independently from the frontend. This change simplified the pathing requirements and eliminated the file pathing conflicts.
