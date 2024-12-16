# JavaFX client

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

The JavaFX client is a desktop application that provides a graphical user interface for interacting with the DigLib application. It allows users to upload, download, view, and delete books. The client is built using JavaFX, and it communicates with the backend server using REST API calls.

## Modules

* [FXUI](fxui/readme.md)
* [Model](model/readme.md)
* [Aggregate](aggregate/readme.md)


## Features

- **User Interface**: Provides a graphical interface for interacting with the DigLib application.
- **Book Management**: Allows users to upload, download, view, and delete books through the UI.
- **Error Handling**: Displays error messages and handles user actions gracefully.


## Launching the JavaFX application

### Dependencies

- Java 17
- Maven 3.8/3.9
- JavaFX 17

### Compile and run the JavaFX client

From root

```bash

  cd client/diglib-javafx

  mvn clean install 

  mvn javafx:run -pl fxui

```

### Run integration tests

JavaFX tests are running headless by default, which means that the ui will not be displayed. By modifying the pom.xml they can be run in non-headless mode as well [here](#run-withouth-headless-mode)


From root

```bash

  cd client/diglib-javafx

  mvn clean install

  mvn verify -P integration-tests

```

### Run end-to-end tests

End-to-end tests are running headless by default, which means that the ui will not be displayed. By modifying the pom.xml they can be run in non-headless mode as well [here](#run-withouth-headless-mode)

From root

```bash

  cd client/diglib-javafx

  mvn clean install

  mvn verify -P e2e-tests

```

### Run withouth headless mode

From root go to client/diglib-javafx/fxui/pom.xml

Change the following lines to false

Before

```xml

  <testfx.headless.mac>true</testfx.headless.mac>
  <testfx.headless.linux>true</testfx.headless.linux>
  <testfx.headless.windows>true</testfx.headless.windows>

```
After

```xml

  <testfx.headless.mac>false</testfx.headless.mac>
  <testfx.headless.linux>false</testfx.headless.linux>
  <testfx.headless.windows>false</testfx.headless.windows>

```

For more information about how the tests work press [here](../../docs/release3/tests.md)

### JavaFX Client Packaging
The JavaFX client can be packaged into a native executable using jpackage. This creates an OS-specific installer or application bundle.

#### Supported Platforms
- Linux (Debian-based distributions)
- macOS
- Windows (through WSL or VSCode DevContainer)

#### Creating the Package
From the project root:
```bash
cd client/diglib-javafx
mvn clean install -P package
```

The packaged application will be created in:
```
client/diglib-javafx/fxui/target/dist/diglib_VERSION_ARCH.EXTENSION
```

#### Platform-Specific Notes
- **Linux**: Creates a .deb package for Debian-based distributions
  - Install the package:
    ```bash
    sudo apt-get install client/diglib-javafx/fxui/target/dist/diglib_VERSION_ARCH.deb
    ```
  - Launch the application:
    ```bash
    /opt/diglib/bin/DigLib
    ```
- **macOS**: Creates a .dmg installer
- **Windows**: Direct Windows packaging is not supported, but you can:
  - Use Windows Subsystem for Linux (WSL) to create and run the package
  - Use the VSCode DevContainer to create and run the package

The packaged application includes all necessary dependencies and can be distributed as a standalone application.

