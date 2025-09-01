FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/Bank_REST-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]