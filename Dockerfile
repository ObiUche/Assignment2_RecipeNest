# Use Gracle with Java 17 to build the Spring Boot jar
FROM gradle:8.14-jdk17 AS builder
WORKDIR /app

# Copy everything into the container
COPY . .

# Build the jar without running tests during image build
RUN gradle bootJar --no-daemon

# Use a smaller Java 17 runtime image for running the app
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]