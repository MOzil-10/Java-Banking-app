FROM openjdk:17-oracle
COPY target/*.jar banking-app-0.0.1-SNAPSHOT.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "banking-app-0.0.1-SNAPSHOT.jar"]