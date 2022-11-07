FROM openjdk:17-oracle
ADD target/ms-crypto-0.0.1-SNAPSHOT.jar ms-crypto.jar
ENTRYPOINT ["java","-jar","ms-crypto.jar"]