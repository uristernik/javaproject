# Build stage
FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /build

# Copy the parent pom.xml and install it first
COPY pom.xml /build/pom.xml
RUN mvn install -N -DskipTests

# Copy the service's pom.xml and source code
COPY services/gateway-service/pom.xml /build/services/gateway-service/pom.xml
COPY services/gateway-service/src /build/services/gateway-service/src

# Build the service
WORKDIR /build/services/gateway-service
RUN mvn package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
COPY --from=builder /build/services/gateway-service/target/gateway-service.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]