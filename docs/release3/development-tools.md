# Development Tools and Setup Guide

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

## Introduction

The DigLib project supports multiple development environment options to accommodate different developer preferences and workflows:

- Local development environment
- Eclipse Che web-based development environment
- VSCode with Development Containers

For team development practices and conventions, refer to our [workhabits documentation](../../docs/release3/workflow.md).

## Local Development Requirements

### Common Requirements
- Git
- Java 17
- Maven 3.8/3.9
- Node.js (version 16.x or higher)
- npm (version 10.8.2 or higher)

### Backend Development
- Google Cloud CLI (for deployment)
- Python 3.x (for configuration scripts)

### JavaFX Client
- JavaFX 17
- Debian packaging tools (for distribution builds)

### React Client
- Node.js and npm

## Remote Development Options

### Eclipse Che Development
The project is configured for Eclipse Che development through the `devfile.yaml`. This provides a consistent development, and has both java17, maven and npm installed. However, eclipse is not configured to show graphical content, e.g. showing the javafx gui with mvn javafx:run would not work.

For configuration details, see [devfile.yaml](../../devfile.yaml).

### VSCode Development Container

#### Prerequisites
- Docker installed and running
  - Download from [https://www.docker.com/products/docker-desktop/](https://www.docker.com/products/docker-desktop/)
  - Ensure Docker daemon is running (Docker Desktop should show "Engine running")
- VSCode Dev Containers extension installed
  - Install from VSCode marketplace: "Dev Containers"
- For macOS users: XQuartz installed for JavaFX GUI support
  - Download from [https://www.xquartz.org/](https://www.xquartz.org/)
  - Log out and log back in after installation
  - Start XQuartz before running JavaFX applications

VSCode users can utilize our development container configuration defined in `devcontainer.json` and `Dockerfile`.

For configuration details, see:
- [devcontainer.json](../../.devcontainer/devcontainer.json)
- [Dockerfile](../../.devcontainer/Dockerfile)


## Version Control and Code Quality

### Continuous Integration
Our CI pipeline plays a crucial role in maintaining code quality:
- Automatically runs on all merge requests
- Executes comprehensive test suites for all components
- Performs static code analysis
- Ensures code quality standards are met before merging
- Prevents merging if tests fail or quality checks don't pass

See our [CI/CD documentation](../../docs/release3/gitlab-ci.md) for detailed information about the pipeline configuration and processes.

### Git Commit Standards
We use conventional commits with automated issue linking. The commit message format is:

```
<type>(#issue): Subject line

[optional body]

[optional Co-authored-by: line]
```

Types include:
- feat: New features
- fix: Bug fixes
- docs: Documentation changes
- style: Formatting changes
- refactor: Code restructuring
- test: Test updates
- chore: Maintenance tasks

### Git Hooks
The project includes automated git hooks for maintaining commit quality:

1. **prepare-commit-msg**: Automatically extracts and includes issue numbers from branch names
2. **commit-msg**: Validates commit message format
3. **commit template**: Provides a standard format for commit messages

Install the hooks using:
```bash
chmod +x .scripts/install-hooks.sh
./.scripts/install-hooks.sh
```

### Issue and Merge Request Templates
We provide standardized templates for:
- Issues: Basic structure for bug reports and feature requests
- Merge Requests: Comprehensive checklist for code changes

These templates ensure consistent documentation and review processes.

## Code Quality Tools

### Java Code Quality
- **Checkstyle**: Enforces coding standards
- **Spotbugs**: Static code analysis
- See [checkstyle.xml](../../checkstyle.xml) and [spotbugs.xml](../../spotbugs.xml) for configurations

### JavaScript/React Code Quality
- **ESLint**: JavaScript linting
- **Prettier**: Code formatting
- See [eslintConfig.eslintrc.js](../../client/diglib-react/eslintConfig.eslintrc.js) and [.prettierrc](../../client/diglib-react/.prettierrc) for configurations