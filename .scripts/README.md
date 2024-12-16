# Scripts Used

>&#8203;    
>[Home](../README.md)    
>&#8203;    

### Setup script

This setup script installs custom Git hooks and a commit message template for this repository. It:

1. Locates the Git repository's root directory.
2. Copies prepare-commit-msg and commit-msg hooks from the hooks directory to the Git hooks directory, making them executable.
3. Copies a .gitmessage template from the templates directory and configures Git to use it as the default commit message template.

Run this script to standardize commit message formats and enable custom hook behaviors in the repository.
