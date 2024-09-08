FROM openjdk:17
EXPOSE 8100
ADD target/trade-service-0.0.1-SNAPSHOT.jar trade-service.jar
ENTRYPOINT ["java","-jar","/trade-service.jar"]