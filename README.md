# New Roots - Farm Fresh Marketplace

New Roots is a microservices-based web application that connects local farmers with consumers, providing a platform for buying and selling fresh, locally-sourced produce. The application is designed to support farmers by giving them a direct channel to market their products while offering consumers access to fresh, seasonal food.

![New Roots Logo](https://cdn-icons-png.flaticon.com/512/187/187039.png)

## Table of Contents

1. [Product Overview](#1-product-overview)
2. [Installation Instructions](#2-installation-instructions)
3. [Architecture and Technical Details](#3-architecture-and-technical-details)
4. [Functionality Description](#4-functionality-description)
5. [Development Workflow](#5-development-workflow)
6. [Inter-Service Communication](#6-inter-service-communication)
7. [User Experience](#7-user-experience)
8. [Future Development Roadmap](#8-future-development-roadmap)
9. [Troubleshooting](#9-troubleshooting)
10. [Acknowledgments](#10-acknowledgments)

## 1. Product Overview

New Roots is more than just a marketplace; it's a community dedicated to connecting consumers directly with local farmers. Founded to support those whose farms were impacted by challenging circumstances, our platform offers the freshest, seasonal produce while directly supporting the individuals who cultivate it.

### Key Features

- **Product Catalog**: Browse a selection of fresh, locally-sourced produce
- **User Authentication**: Secure login and registration system
- **Shopping Cart**: Add products to cart and checkout
- **Order Management**: Track and manage orders
- **Farmer Portal**: Interface for farmers to add and manage their produce
- **Admin Dashboard**: Manage users, products, and prices

## 2. Installation Instructions

### Prerequisites

- [Java Development Kit (JDK) 17](https://adoptium.net/)
- [Maven](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com/products/docker-desktop/)
- [Docker Compose](https://docs.docker.com/compose/install/)

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

### Full Rebuild

To completely rebuild the application (useful after making changes):

```bash
make clean
make build-run
```

## 3. Architecture and Technical Details

### Architecture Overview

New Roots is built using a microservices architecture with the following components:

- **Frontend**: Static HTML, CSS
- **Backend**: Java Spring Boot microservices
- **Database**: PostgreSQL
- **API Gateway**: NGINX for routing and load balancing
- **Authentication**: Session-based authentication with cookies

### Technology Stack

- **Frontend**:
  - HTML5, CSS3
  - Modern responsive design
  - Font Awesome icons
  - Google Fonts (Poppins, Montserrat)

- **Backend**:
  - Java 17
  - Spring Boot 3.1.0
  - Spring Security
  - Spring Web
  - Thymeleaf (for server-side template rendering)

- **Database**:
  - PostgreSQL 15
  - JDBC

- **DevOps**:
  - Docker
  - Docker Compose
  - Makefile for common operations
  - NGINX for reverse proxy

### Directory Structure

```
├── docker-compose.yml       # Docker Compose configuration
├── Makefile                 # Makefile for common operations
├── nginx/                   # NGINX configuration and static files
│   ├── Dockerfile           # NGINX Docker image configuration
│   ├── nginx.conf           # NGINX server configuration
│   └── static/              # Static resources
├── services/                # Microservices
│   ├── admin-service/       # Admin service
│   ├── auth-service/        # Authentication service
│   ├── data-access-service/ # Data access service
│   ├── inventory-service/   # Inventory service
│   ├── order-management-service/ # Order management service
│   └── product-catalog-service/  # Product catalog service
└── README.md                # Project documentation
```

### Microservices

The application is divided into several microservices, each responsible for specific functionality:

1. **Admin Service**
   - User management
   - Price management
   - Admin dashboard

2. **Inventory Service**
   - Inventory management
   - Farmer portal
   - Stock tracking

3. **Product Catalog Service**
   - Product browsing
   - Home page
   - Checkout process

4. **Order Management Service**
   - Order creation
   - Order tracking
   - Order history
   - User-specific order IDs

5. **Data Access Service**
   - Centralized database operations
   - Data retrieval and storage
   - Database schema management

6. **Auth Service**
   - User authentication
   - Session management
   - Security
   - User registration

7. **NGINX**
   - API Gateway
   - Load balancing
   - Authentication proxy

### Database Schema

The application uses a PostgreSQL database with the following main tables:

- **users**: Stores user information (id, email, password, name, type)
- **products**: Stores product information (id, name, description, price, image)
- **inventory**: Tracks product inventory (product_id, quantity, farmer_id)
- **orders**: Stores order information (id, user_id, status, date)
- **order_items**: Links orders to products (order_id, product_id, quantity, price)
- **farmers**: Stores farmer information (id, user_id, farm_name, location)

## 4. Functionality Description

New Roots provides a comprehensive set of features for different user types:

### For Consumers

- **Browse Products**: View the catalog of available fresh produce
- **Shopping Cart**: Add items to cart, adjust quantities, and proceed to checkout
- **Order Tracking**: View order history and track current orders
- **User Profile**: Manage personal information and preferences

### For Farmers

- **Inventory Management**: Add new produce and update existing inventory
- **Stock Management**: Track available quantities of products

### For Administrators

- **User Management**: Add, edit, or remove users and manage their permissions
- **Price Management**: Set and adjust product prices
- **Order Overview**: View all orders across the platform

### Authentication and Authorization

- **User Registration**: New users can create accounts
- **User Login**: Secure authentication with email and password
- **Role-Based Access Control**: Different permissions for regular users, farmers, and administrators
- **Session Management**: Secure session handling with cookies

## 5. Inter-Service Communication

### Communication Patterns

The microservices in New Roots communicate with each other using the following patterns:

1. **REST API Calls**: Services communicate with each other via HTTP REST APIs
2. **API Gateway**: NGINX routes requests to the appropriate service
3. **Shared Database**: The data-access-service provides a centralized point for database operations

### Network Configuration

All services are connected via a Docker network defined in the docker-compose.yml file. Services can communicate with each other using their service names as hostnames:

- `auth-service`: Auth Service
- `data-access-service`: Data Access Service
- `product-catalog-service`: Product Catalog Service
- `order-management-service`: Order Management Service
- `inventory-service`: Inventory Service
- `admin-service`: Admin Service
- `nginx`: NGINX
- `database`: PostgreSQL Database

## 6. User Experience

### Navigation

The application features a consistent navigation bar across all pages with links to:

- **Home**: Landing page with featured products
- **Catalog**: Browse all available products
- **Orders**: View order history and track current orders
- **Inventory**: (For farmers) Manage product inventory
- **Users**: (For admins) Manage users
- **Prices**: (For admins) Manage product prices

### User Flows

#### Registration and Login

1. User navigates to the registration page
2. User fills out the registration form with personal information
3. System validates the information and creates a new account
4. User is redirected to the login page
5. User enters credentials and logs in
6. User is redirected to the home page

#### Shopping

1. User browses the product catalog
2. User adds products to the shopping cart
3. User proceeds to checkout
4. User confirms the order
5. System processes the order and displays a confirmation

#### Order Management

1. User navigates to the orders page
2. User views order history
3. User can track the status of the order

## 7. Future Development Roadmap

### Short-Term Improvements

1. **Session Management with Redis**
   - Implement Redis for session storage
   - Enable session persistence across service restarts
   - Improve session expiration handling

2. **Enhanced Security**
   - Implement HTTPS with Let's Encrypt
   - Add CSRF protection
   - Implement rate limiting

3. **UI/UX Enhancements**
   - Add product images
   - Improve mobile responsiveness
   - Add dark mode support

### Medium-Term Goals

1. **Kubernetes Deployment**
   - Containerize all services for Kubernetes
   - Create Kubernetes manifests (Deployments, Services, ConfigMaps)
   - Implement Horizontal Pod Autoscaling
   - Set up Ingress controllers

2. **Monitoring and Observability**
   - Implement Prometheus for metrics collection
   - Set up Grafana dashboards
   - Add distributed tracing with Jaeger
   - Implement centralized logging with ELK stack

3. **Feature Enhancements**
   - Add product reviews and ratings
   - Implement a recommendation system
   - Add social sharing functionality
   - Implement a notification system
