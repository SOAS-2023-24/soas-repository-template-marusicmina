FROM openjdk:17
EXPOSE 8200
ADD target/bank-account-0.0.1-SNAPSHOT.jar bank-account.jar
ENTRYPOINT ["java","-jar","/bank-account.jar"]