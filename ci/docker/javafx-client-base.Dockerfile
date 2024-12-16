FROM maven:3.9-eclipse-temurin-17

RUN apt-get update -y
RUN apt-get install -y libpangoft2-1.0-0
RUN apt-get install -y libfreetype6
RUN apt-get install -y fonts-liberation