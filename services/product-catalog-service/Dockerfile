# Build stage
FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /build

# Copy the parent pom.xml and install it first
COPY pom.xml /build/pom.xml
RUN mvn install -N -DskipTests

# Copy the service's pom.xml and source code
COPY services/product-catalog-service/pom.xml /build/services/product-catalog-service/pom.xml
COPY services/product-catalog-service/src /build/services/product-catalog-service/src

# Build the service
WORKDIR /build/services/product-catalog-service
RUN mvn package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
COPY --from=builder /build/services/product-catalog-service/target/product-catalog-service.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
