FROM openjdk:17-jdk-alpine
WORKDIR ./app
ENTRYPOINT ["java","-jar","app_java.jar"]