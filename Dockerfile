FROM openjdk:11.0.10-jdk
ARG JAR_FILE=target/spring-boot-atm-api-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]