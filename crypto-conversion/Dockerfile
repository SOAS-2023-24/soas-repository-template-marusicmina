FROM openjdk:17
EXPOSE 8500
ADD target/crypto-conversion-0.0.1-SNAPSHOT.jar crypto-conversion.jar
ENTRYPOINT ["java","-jar","/crypto-conversion.jar"]