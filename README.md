# Digital Library

>&#8203;    
>[Home](./README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
[About](./docs/README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Development-Tools](./docs/release3/development-tools.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[API](./docs/release3/api-calls.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Workhabits](./docs/release3/workflow.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Tests](./docs/release3/tests.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[JavaFX](./client/diglib-javafx/README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[React](./client/diglib-react/README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Backend](./backend/README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[CI/CD](./docs/release3/gitlab-ci.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Google-Cloud](./docs/release3/gcloud-setup.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Cloudflare](./docs/release3/cloudflare-setup.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Use-cases](./docs/release3/usercase.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;     
>&#8203; 

## Overview of DigLib

This repository belongs to Group 62 and houses the Digital Library project. The project is an application designed to give user an easy way to read books and distribute own books to others.

 [Open our project in Eclipse Che](https://che.stud.ntnu.no/#https://gitlab.stud.idi.ntnu.no/it1901/groups-2024/gr2462/gr2462?new)

The application is split into three main parts:

1. **JavaFX Client**:
    - A desktop application that allows users to manage the library locally.
    - For more information, visit the [JavaFX Client Documentation](./client/diglib-javafx/README.md).

2. **React Client**:
    - A web application with the same functionality as the JavaFX client.
    - Deployed with CloudFlare and accessible at [diglib.no](https://diglib.no).
    - For more information, visit the [React Client Documentation](./client/diglib-react/README.md).
    - For deployment details, visit the [Cloudflare Setup Documentation](./docs/release3/cloudflare-setup.md).

3. **REST Server**:
    - A REST server built with Spring Boot and hosted on Google Cloud Platform.
    - For information about the server in general, vist the [REST Server Documentation](./backend/README.md)
    - For information about the API endpoints, visit the [API-endpoints Documentation](./docs/release3/api-calls.md)
    - For deployment details, visit [Google Cloud Platform setup Documentation](./docs/release3/gcloud-setup.md).
  
The project is being built, tested and deployed using GitLab CI/CD. For more information, visit the [CI/CD Documentation](./docs/release3/gitlab-ci.md).

Read more by navigating to the different parts of the application in the navigation bar above. If you want to run the applications yourself you should navigate to the specific part of the project you want to run. You should also read about our [Development tools](./docs/release3/development-tools.md) if you want to run in a development container.

The project is part of the course IT1901 - Informatics Project I at the Norwegian University of Science and Technology (NTNU).

### Notes to the user

We have experienced delays when our application is first accessed. This applies both to the JavaFX client and the React client. If this happens to you, please be patient and wait for the application to load.

### Contributors

- [Peder Brennum](https://gitlab.stud.idi.ntnu.no/pedebr)
- [Ask Hallem-Berg](https://gitlab.stud.idi.ntnu.no/askha)
- [Sindre Swan Moland](https://gitlab.stud.idi.ntnu.no/sindrsmo)
- [Jørgen Hauan Larsen](https://gitlab.stud.idi.ntnu.no/jorgehla)

### Release notes

- [v1.0](/docs/release1/README.md)
- [v2.0](/docs/release2/README.md)
- [v3.0](/docs/release3/README.md)
