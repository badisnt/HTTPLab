FROM openjdk:21
WORKDIR /app
ADD target/foodAPI-1.0-jar-with-dependencies.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]