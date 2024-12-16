# Google Cloud Platform Setup Guide for DigLib

## Prerequisites
- Google Cloud CLI installed
- Project created in Google Cloud Console
- Billing enabled

## Environment Variables
First, set up your environment variables. Create a `.env` file:

```bash
# Project settings
PROJECT_ID=diglib-439508
REGION=europe-west3

# Database settings
DB_INSTANCE=diglib-db
DB_NAME=diglib
DB_USER=your_user
DB_PASS=your_secure_password
ROOT_PASSWORD=your_secure_root_password

# Storage settings
BUCKET_NAME=diglib-bucket

# Network settings
NETWORK_NAME=diglib-network
SUBNET_NAME=diglib-subnet
CONNECTOR_NAME=diglib-connector
```

Source the variables:
```bash
source .env
```

## Initial Project Setup
```bash
# Set project
gcloud config set project $PROJECT_ID

# Enable required APIs
gcloud services enable \
    appengine.googleapis.com \
    sqladmin.googleapis.com \
    secretmanager.googleapis.com \
    vpcaccess.googleapis.com \
    cloudresourcemanager.googleapis.com \
    storage.googleapis.com
```

## Create App Engine Application
```bash
# Create App Engine app in europe-west3
gcloud app create --region=$REGION
```

## Create Cloud Storage Bucket
```bash
# Create bucket in the same region
gcloud storage buckets create gs://$BUCKET_NAME \
    --location=$REGION \
    --uniform-bucket-level-access
```

## Set up Network Infrastructure
```bash
# Create VPC network
gcloud compute networks create $NETWORK_NAME \
    --subnet-mode=custom

# Create subnet
gcloud compute networks subnets create $SUBNET_NAME \
    --network=$NETWORK_NAME \
    --range=10.0.0.0/28 \
    --region=$REGION

# Create VPC connector
gcloud compute networks vpc-access connectors create $CONNECTOR_NAME \
    --network=$NETWORK_NAME \
    --region=$REGION \
    --range=10.8.0.0/28
```

```bash
# Configure private services access
gcloud compute addresses create google-managed-services-${NETWORK_NAME} \
    --global \
    --purpose=VPC_PEERING \
    --prefix-length=16 \
    --network=${NETWORK_NAME}

# Create the VPC peering connection
gcloud services vpc-peerings connect \
    --service=servicenetworking.googleapis.com \
    --ranges=google-managed-services-${NETWORK_NAME} \
    --network=${NETWORK_NAME} \
    --project=${PROJECT_ID}
```

### Firewall rules
```bash
# Allow internal traffic
gcloud compute firewall-rules create allow-internal \
    --network=${NETWORK_NAME} \
    --allow=tcp,udp,icmp \
    --source-ranges=10.0.0.0/8

# Allow App Engine to Cloud SQL traffic
gcloud compute firewall-rules create allow-appengine \
    --network=${NETWORK_NAME} \
    --allow=tcp:3306 \
    --source-ranges=10.8.0.0/28
```

## Create Cloud SQL Instance
```bash
# Create SQL instance
gcloud sql instances create $DB_INSTANCE \
    --database-version=MYSQL_8_0 \
    --tier=db-f1-micro \
    --region=$REGION \
    --root-password=$ROOT_PASSWORD \
    --storage-type=SSD \
    --availability-type=zonal \
    --network=$NETWORK_NAME \
    --require-ssl

# Create database
gcloud sql databases create $DB_NAME \
    --instance=$DB_INSTANCE

# Create user
gcloud sql users create $DB_USER \
    --instance=$DB_INSTANCE \
    --password=$DB_PASS
```

## Set up Secret Manager
```bash
# Create secrets
echo -n "$DB_USER" | gcloud secrets create diglib-db-user --data-file=-
echo -n "$DB_PASS" | gcloud secrets create diglib-db-pass --data-file=-
```

## Configure IAM Permissions
```bash
# Get the App Engine service account
export APP_ENGINE_SA="$PROJECT_ID@appspot.gserviceaccount.com"

# Grant necessary roles
gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:$APP_ENGINE_SA" \
    --role="roles/cloudsql.client"

gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:$APP_ENGINE_SA" \
    --role="roles/vpcaccess.user"

gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:$APP_ENGINE_SA" \
    --role="roles/secretmanager.secretAccessor"

gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:$APP_ENGINE_SA" \
    --role="roles/storage.objectViewer"

gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:$APP_ENGINE_SA" \
    --role="roles/storage.objectCreator"
```

## Verify Setup
```bash
# Verify App Engine
gcloud app describe

# Verify SQL instance
gcloud sql instances describe $DB_INSTANCE

# Verify VPC connector
gcloud compute networks vpc-access connectors describe $CONNECTOR_NAME \
    --region=$REGION

# Verify bucket
gsutil ls gs://$BUCKET_NAME

# Verify secrets
gcloud secrets list
```

## Usage Notes
- Keep your `.env` file secure and never commit it to version control
- The setup uses minimal resources to keep costs low (e.g., db-f1-micro for SQL)
- All resources are created in the same region (europe-west3) for optimal performance
- The VPC network provides secure communication between App Engine and Cloud SQL
- SSL is required for database connections
- The bucket uses uniform bucket-level access for better security

## Clean Up
If you need to delete everything:
```bash
# Delete App Engine app (requires project deletion)
# Delete SQL instance
gcloud sql instances delete $DB_INSTANCE

# Delete bucket
gsutil rm -r gs://$BUCKET_NAME

# Delete VPC connector
gcloud compute networks vpc-access connectors delete $CONNECTOR_NAME \
    --region=$REGION

# Delete subnet
gcloud compute networks subnets delete $SUBNET_NAME \
    --region=$REGION

# Delete network
gcloud compute networks delete $NETWORK_NAME

# Delete secrets
gcloud secrets delete diglib-db-user
gcloud secrets delete diglib-db-pass
```