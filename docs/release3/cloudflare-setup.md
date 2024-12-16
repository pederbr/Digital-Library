# Cloudflare and Domain Setup Guide for DigLib

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
This guide documents the Cloudflare configuration and domain setup for DigLib's production environment. The frontend application is automatically deployed through Cloudflare Pages

## Domain Configuration

### Domain Registration
- Domain provider: domene.shop
- Registered domain: diglib.no
- Primary usage: Production environment for DigLib application

### DNS Configuration

#### Frontend (diglib.no)
```plaintext
Type: CNAME
Name: diglib.no
Target: diglib-react-deployment.pages.dev
Proxy status: Proxied
```

#### API (api.diglib.no)
```plaintext
Type: CNAME
Name: api.diglib.no
Target: diglib-439508.ey.r.appspot.com
Proxy status: Proxied
```

## Cloudflare Setup

### Pages Configuration
   - Connected to: `ahallemberg/diglib-react-deployment`
   - Copies build react application from repo
   - Automatic deployments enabled on push events

### Security Configuration

#### SSL/TLS Settings
- Mode: Full (strict)
- Always Use HTTPS: Enabled
- Automatic HTTPS Rewrites: Enabled

#### DNSSEC Configuration
- Status: Enabled
- Protects against DNS spoofing and cache poisoning attacks

#### Zero Trust Access
1. Authentication Requirements:
   - Email domain restriction: `@ntnu.no`
   - Single Sign-On (SSO) enabled
   - Session duration: 24 hours

2. Access Policies:
   ```plaintext
   Rule: Require NTNU Authentication
   Action: Allow
   Selector: email.endsWith("@ntnu.no")
   ```

## Deployment Flow
1. Frontend Deployment
   - CI/CD pipeline builds React application (documented in CI/CD guide)
   - Built artifacts pushed to GitHub deployment repository
   - Cloudflare Pages automatically deploys from GitHub

2. Backend Integration
   - Spring Boot API deployed to Google Cloud App Engine (documented in CI/CD guide)
   - Cloudflare proxies requests to api.diglib.no