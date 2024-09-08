FROM openjdk:17
EXPOSE 8000
ADD target/currency-exchange-0.0.1-SNAPSHOT.jar currency-exchange.jar
ENTRYPOINT ["java","-jar","/currency-exchange.jar"]