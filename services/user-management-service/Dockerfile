# Build stage
FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /build

# Copy the parent pom.xml and install it first
COPY pom.xml /build/pom.xml
RUN mvn install -N -DskipTests

# Copy the service's pom.xml and source code
COPY services/user-management-service/pom.xml /build/services/user-management-service/pom.xml
COPY services/user-management-service/src /build/services/user-management-service/src

# Build the service
WORKDIR /build/services/user-management-service
RUN mvn package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
COPY --from=builder /build/services/user-management-service/target/user-management-service.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
