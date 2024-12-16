FROM maven:3.9-eclipse-temurin-17

RUN apt-get update
RUN apt-get install -y apt-transport-https ca-certificates gnupg curl 
RUN curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | gpg --dearmor -o /usr/share/keyrings/cloud.google.gpg
RUN echo "deb [signed-by=/usr/share/keyrings/cloud.google.gpg] https://packages.cloud.google.com/apt cloud-sdk main" | tee -a /etc/apt/sources.list.d/google-cloud-sdk.list
RUN apt-get update && apt-get install -y google-cloud-cli
RUN apt-get update -qq && apt-get install -qq -y python3 > /dev/null 2>&1