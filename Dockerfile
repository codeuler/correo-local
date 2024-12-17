FROM openjdk:17-jdk-alpine
WORKDIR ./app
ENTRYPOINT ["java","-jar","codemail-0.0.1-SNAPSHOT.jar"]