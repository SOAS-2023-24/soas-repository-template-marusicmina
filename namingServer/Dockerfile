FROM openjdk:17
EXPOSE 8761
ADD target/naming-server-1.0.0.jar naming-server.jar
ENTRYPOINT ["java","-jar","/naming-server.jar"]