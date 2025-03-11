# Build stage
FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /build

# Copy the entire project structure
COPY pom.xml .
COPY services services/

# Build from the root directory
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
COPY --from=builder /build/services/inventory-service/target/inventory-service.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
