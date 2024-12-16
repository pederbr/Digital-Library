#!/usr/bin/env python3
import os
import re

def replace_sensitive_values(input_file, output_file) -> None:
    """
    Replace sensitive values in application.properties with placeholders
    and create a new file with the replacements
    """
    # Define the properties that should be replaced with environment variables
    sensitive_props = {
        'spring.datasource.url': '${JDBC_URL}',
        'spring.datasource.username': '${DB_USERNAME}',
        'spring.datasource.password': '${DB_PASSWORD}',
        'spring.cloud.gcp.sql.instance-connection-name': '${GCP_INSTANCE_CONNECTION_NAME}',
        'spring.cloud.gcp.sql.database-name': '${GCP_DATABASE_NAME}',
        'spring.cloud.gcp.storage.bucket-name': '${GCP_BUCKET_NAME}',
        'spring.cloud.gcp.project-id': '${PROJECT_ID}'
    }

    with open(input_file, 'r') as f:
        properties = f.read()

    for prop, placeholder in sensitive_props.items():
        pattern = f'^{prop}=.*$'
        replacement = f'{prop}={placeholder}'
        properties = re.sub(pattern, replacement, properties, flags=re.MULTILINE)

    with open(output_file, 'w') as f:
        f.write(properties)

def update_properties_with_env(properties_file):
    """
    Update properties file by replacing placeholders with environment variables
    """
    with open(properties_file, 'r') as f:
        properties = f.read()

    env_mapping = {
        '${JDBC_URL}': os.getenv('JDBC_URL'),
        '${DB_USERNAME}': os.getenv('DB_USERNAME'),
        '${DB_PASSWORD}': os.getenv('DB_PASSWORD'),
        '${GCP_INSTANCE_CONNECTION_NAME}': os.getenv('GCP_INSTANCE_CONNECTION_NAME'),
        '${GCP_DATABASE_NAME}': os.getenv('GCP_DATABASE_NAME'),
        '${GCP_BUCKET_NAME}': os.getenv('GCP_BUCKET_NAME'),
        '${PROJECT_ID}': os.getenv('PROJECT_ID')
    }

    for placeholder, env_value in env_mapping.items():
        if env_value is not None:
            properties = properties.replace(placeholder, env_value)
        else:
            print(f"Warning: Environment variable for {placeholder} not found")

    with open(properties_file, 'w') as f:
        f.write(properties)

if __name__ == "__main__":
    import sys
    
    if len(sys.argv) != 2:
        print("Usage: python script.py <properties_file>")
        sys.exit(1)
    
    properties_file = sys.argv[1]
    
   
    backup_file = properties_file + '.bak'
    with open(properties_file, 'r') as src, open(backup_file, 'w') as dst:
        dst.write(src.read())
    
    replace_sensitive_values(backup_file, properties_file)
    update_properties_with_env(properties_file)