# JaCoCo Module
 Currently not functioning


## Overview

 This is the JaCoCo module, it is an integral part of our Maven project, designed to measure and report code coverage. By integrating JaCoCo, we ensure that our tests adequately cover our codebase, providing valuable insights into untested parts of our application. The module is only used for generating code coverage reports and does not contain any source code.

## Features

- **Code Coverage Reports**: Generates detailed code coverage reports in various formats (HTML, XML, CSV).
- **Integration with Maven**: Seamlessly integrates with our Maven build lifecycle.
- **Thresholds**: Allows us to set coverage thresholds to enforce minimum coverage levels.

## Reading output

To read the generated code coverage reports, navigate to the `target/site/jacoco-aggregate` directory. Open the file `index.html` in your browser.

### [JaCoCo Aggregate report](/aggregate/target/site/jacoco-aggregate/index.html)

## Test Coverage

We chose to exclude the UI-module from the test coverage report, as it is not feasible to test the UI using JaCoCo. The UI-module is tested using testFX, which is a separate testing framework. We estimate that the UI-module has a test coverage of around 65%.
