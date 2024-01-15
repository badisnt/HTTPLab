FROM openjdk:21
WORKDIR /app
COPY target/foodAPI-1.0-jar-with-dependencies.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]