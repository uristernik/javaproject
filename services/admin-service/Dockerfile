# Build stage
FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /build

# Copy the parent pom.xml and install it first
COPY pom.xml /build/pom.xml
RUN mvn install -N -DskipTests

# Copy the service's pom.xml and source code
COPY services/admin-service/pom.xml /build/services/admin-service/pom.xml
COPY services/admin-service/src /build/services/admin-service/src

# Build the service
WORKDIR /build/services/admin-service
RUN mvn package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
COPY --from=builder /build/services/admin-service/target/admin-service.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
