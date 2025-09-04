# ---- Stage 1: Build the application ----
FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /src

# Copy the source code
COPY . .

# Download dependencies
RUN mvn dependency:go-offline

# Package the application
RUN mvn clean package -DskipTests

# ---- Stage 2: Run the application ----
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /src/target/*.jar app.jar

# Expose port 8080 (default Spring Boot port)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
