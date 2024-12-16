# React client

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

## Overview


The React client is a web application that provides user friendly interface for interacting with the DigLib application from any modern web browser. The user can view, upload, download and delete books. The client is built using React, and communicates with the backend server using REST API calls. The application is hosted on Cloudflare.


## Modules
 * [Components](src/components/readme.md)
 * [Services](src/services/readme.md)


### Features

- **Responsive Design**: Ensures compatibility with various devices and screen sizes.
- **Authentication**: Secure login and registration for users.
- **User Interface**: Provides a user-friendly interface for interacting with the DigLib application.
- **Pagination**: Efficiently handles large collections of books with pagination.
- **Book Management**: Allows users to upload, download, view, and delete books through the UI.
- **Error Handling**: Displays error messages and handles user actions gracefully.



## Launching the React application

### Dependencies

We use npm as our package manager. Make sure you have npm installed on your machine.  
You can install npm by installing Node.js from [here](https://nodejs.org/en/download/).

- **Node.js**: Version 16.x or higher (recommended to ensure compatibility with the latest npm versions).
- **npm**: Version `10.8.2` or higher.

To check your npm version, run:
```bash
npm -v
```



### Visit our website

[Digital Library](https://diglib.no)

- Log in with ntnu email (@ntnu.no or @stud.ntnu.no)
- Fill in the code from your email


### Run the React client on localhost

From root go to client/diglib-react/package.json
add the following line to the root section of the package.json file

```json

  "proxy": "https://api.diglib.no"`

```
From root go to client/diglib-react/src/config/api.js

change the following line

```javascript

  const API_URL = 'https://api.diglib.no';

```
To
    
```javascript

const API_URL = '';
    
```

Then from root

```bash

  cd client/diglib-react

  npm install

  npm run build

  npm start

```


### Run unit and integration tests

#### Without coverage

From root

```bash

  cd client/diglib-react

  npm install

  npm test -- --watchAll=false

```

#### With coverage

From root

```bash

  cd client/diglib-react

  npm install

  npm test -- --coverage --watchAll=false

```

For more information about how the tests work press [here](../../docs/release3/tests.md)


### For deployment

From root

```bash

  cd client/diglib-react

  npm install

  npm run build

```

### Run static code analysis
These commands will run automatically when you use npm run build.

From root

```bash

  cd client/diglib-react

  npm install

  npm run lint && npm run format:check

```

