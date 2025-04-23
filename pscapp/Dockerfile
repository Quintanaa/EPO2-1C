FROM maven:3.8.8-openjdk-17

WORKDIR /pscapp
COPY ../.. .
RUN mvn clean install

CMD mvn spring-boot:run