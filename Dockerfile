FROM openjdk:8-jdk-alpine
ARG JAR_FILE=build/libs/gift_certificates-1.0-SNAPSHOT.jar
ARG JTA_CONFIG=jta.properties
COPY ${JAR_FILE} certificate-service.jar
ENTRYPOINT ["java", "-jar", "certificate-service.jar"]