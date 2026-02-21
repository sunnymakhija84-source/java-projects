# Simple runtime-only image; requires 'mvn -DskipTests package' before building the image
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copy the built jar from the module's target directory
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
