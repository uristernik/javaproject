# New Roots - Farm Fresh Marketplace

New Roots is a microservices-based web application that connects local farmers with consumers, providing a platform for buying and selling fresh, locally-sourced produce. The application is designed to support farmers by giving them a direct channel to market their products while offering consumers access to fresh, seasonal food.

![New Roots Logo](https://via.placeholder.com/800x200?text=New+Roots+Farm+Fresh+Marketplace)

## 1. Product Overview

New Roots is more than just a marketplace; it's a community dedicated to connecting consumers directly with local farmers. Founded to support those whose farms were impacted by challenging circumstances, our platform offers the freshest, seasonal produce while directly supporting the individuals who cultivate it.

### Key Features

- **Product Catalog**: Browse a selection of fresh, locally-sourced produce
- **User Authentication**: Secure login and registration system
- **Shopping Cart**: Add products to cart and checkout
- **Order Management**: Track and manage orders
- **Farmer Portal**: Interface for farmers to add and manage their produce
- **Admin Dashboard**: Manage users, products, and prices
- **Responsive Design**: Works on desktop and mobile devices

### Architecture

New Roots is built using a microservices architecture with the following components:

- **Frontend**: Thymeleaf templates with modern CSS and JavaScript
- **Backend**: Java Spring Boot microservices
- **Database**: PostgreSQL
- **API Gateway**: NGINX for routing and load balancing
- **Authentication**: JWT-based authentication

## 2. Installation Instructions

### Prerequisites

- [Java Development Kit (JDK) 17](https://adoptium.net/)
- [Maven](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com/products/docker-desktop/)
- [Docker Compose](https://docs.docker.com/compose/install/)
- Git

### Setup Steps

1. **Clone the repository**

   ```bash
   git clone https://github.com/yourusername/new-roots.git
   cd new-roots
   ```

2. **Build the application**

   ```bash
   mvn clean package
   ```

3. **Start the application using Docker Compose**

   ```bash
   docker-compose up --build -d
   ```

   Alternatively, you can use the provided Makefile:

   ```bash
   make build-run
   ```

4. **Access the application**

   Open your browser and navigate to:
   ```
   http://localhost
   ```

5. **Default Admin Login**

   ```
   Email: admin@example.com
   Password: password
   ```

### Stopping the Application

To stop the application:

```bash
docker-compose down
```

Or using the Makefile:

```bash
make clean
```

## 3. Functionality Description

New Roots provides a comprehensive set of features for different user types:

### For Consumers

- **Browse Products**: View the catalog of available fresh produce
- **Search and Filter**: Find specific products based on various criteria
- **Shopping Cart**: Add items to cart, adjust quantities, and proceed to checkout
- **Order Tracking**: View order history and track current orders
- **User Profile**: Manage personal information and preferences

### For Farmers

- **Inventory Management**: Add new produce and update existing inventory
- **Stock Management**: Track available quantities of products
- **Order Fulfillment**: View and manage incoming orders
- **Product Analytics**: See which products are selling well

### For Administrators

- **User Management**: Add, edit, or remove users and manage their permissions
- **Price Management**: Set and adjust product prices
- **Order Overview**: View all orders across the platform
- **System Monitoring**: Monitor system health and performance

### Microservices

The application is divided into several microservices, each responsible for specific functionality:

1. **Admin Service (Port 8080)**
   - User management
   - Price management

2. **Inventory Service (Port 8081)**
   - Inventory management
   - Farmer portal

3. **Product Catalog Service (Port 8082)**
   - Product browsing
   - Home page
   - Checkout process

4. **Order Management Service (Port 8084)**
   - Order creation
   - Order tracking
   - Order history

5. **Data Access Service (Port 8085)**
   - Database operations
   - Data retrieval and storage

6. **Auth Service (Port 8086)**
   - User authentication
   - Session management
   - Security

7. **NGINX (Port 80)**
   - API Gateway
   - Load balancing
   - Static file serving

## Acknowledgments

- All the farmers who inspire us with their dedication and resilience
- The open-source community for providing the tools and frameworks that make this project possible