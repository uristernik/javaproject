.PHONY: run build-run clean

# Just run the containers
run:
	docker-compose up -d
	@echo "Services are starting..."
	@echo "Wait a few seconds for all services to be ready"
	@echo "Access the application at http://localhost"

# Build the Java applications, build Docker images, and run containers
build-run:
	mvn clean package
	docker-compose up --build -d
	@echo "Services are starting..."
	@echo "Wait a few seconds for all services to be ready"
	@echo "Access the application at http://localhost"

# Stop containers, remove containers, networks, volumes, and images
clean:
	docker-compose down -v
	mvn clean
	@echo "Environment cleaned"
