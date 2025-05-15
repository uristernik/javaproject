.PHONY: run build-run clean help build-admin build-inventory build-product build-order build-data build-auth

# Display help information
help:
	@echo "Available commands:"
	@echo "  make run                 - Start all containers without rebuilding"
	@echo "  make build-run           - Build all services and start containers"
	@echo "  make clean               - Stop and remove all containers, networks, volumes"
	@echo "  make build-admin         - Rebuild and restart only the admin-service"
	@echo "  make build-inventory     - Rebuild and restart only the inventory-service"
	@echo "  make build-product       - Rebuild and restart only the product-catalog-service"
	@echo "  make build-order         - Rebuild and restart only the order-management-service"
	@echo "  make build-data          - Rebuild and restart only the data-access-service"
	@echo "  make build-auth          - Rebuild and restart only the auth-service"

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

# Build and restart only the admin-service
build-admin:
	mvn -pl services/admin-service clean package
	docker-compose up -d --build admin-service
	@echo "Admin service rebuilt and restarted"
	@echo "Access the application at http://localhost"

# Build and restart only the inventory-service
build-inventory:
	mvn -pl services/inventory-service clean package
	docker-compose up -d --build inventory-service
	@echo "Inventory service rebuilt and restarted"
	@echo "Access the application at http://localhost"

# Build and restart only the product-catalog-service
build-product:
	mvn -pl services/product-catalog-service clean package
	docker-compose up -d --build product-catalog-service
	@echo "Product catalog service rebuilt and restarted"
	@echo "Access the application at http://localhost"

# Build and restart only the order-management-service
build-order:
	mvn -pl services/order-management-service clean package
	docker-compose up -d --build order-management-service
	@echo "Order management service rebuilt and restarted"
	@echo "Access the application at http://localhost"

# Build and restart only the data-access-service
build-data:
	mvn -pl services/data-access-service clean package
	docker-compose up -d --build data-access-service
	@echo "Data access service rebuilt and restarted"
	@echo "Access the application at http://localhost"

# Build and restart only the auth-service
build-auth:
	mvn -pl services/auth-service clean package
	docker-compose up -d --build auth-service
	@echo "Auth service rebuilt and restarted"
	@echo "Access the application at http://localhost"
