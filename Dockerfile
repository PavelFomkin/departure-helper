# Step 1: Build the application using a Gradle image
FROM gradle:7.6-jdk11 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy your Gradle wrapper and build files
COPY gradle/wrapper /app/gradle/wrapper
COPY build.gradle /app/
COPY settings.gradle /app/

# Copy the source code
COPY src /app/src

# Run Gradle build to produce a JAR (skip tests)
RUN gradle clean build -x test

# Step 2: Create the final image to run the app
FROM openjdk:11-jre-slim

# Set the working directory inside the container for the app
WORKDIR /

# Copy the JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port the application will run on (default: 8080)
EXPOSE 8081

# Command to run the Spring Boot app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
