FROM openjdk:17
EXPOSE 8770
ADD target/users-service-0.0.1-SNAPSHOT.jar users-service.jar
ENTRYPOINT ["java","-jar","/users-service.jar"]