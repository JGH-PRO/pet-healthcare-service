# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the executable JAR file built by Maven to the /app directory
# Assumes the JAR is in 'target' and named 'petcare-app-*.jar' (wildcard for version)
COPY target/petcare-app-*.jar app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
