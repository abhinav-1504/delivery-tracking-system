# Delivery Tracking System Documentation

## Table of Contents

## Table of Contents
- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Database Schema](#database-schema)
- [API Endpoints](#api-endpoints)
- [Performance Metrics](#performance-metrics)
- [Route Optimization Impact](#route-optimization-impact)
- [Reasoning for 20% Efficiency Improvement](#reasoning-for-20-efficiency-improvement)
- [Testing](#testing)
   - [Unit and Integration Tests](#unit-and-integration-tests)
   - [API Testing with Postman](#api-testing-with-postman)
- [Deployment Instructions](#deployment-instructions)
- [Troubleshooting](#troubleshooting)
- [Lessons Learned](#lessons-learned)
- [Future Improvements](#future-improvements)


## Project Overview

The Delivery Tracking System is a Spring Boot application designed to streamline delivery operations, built as a learning project by a fresher backend developer to apply Java, Spring Boot, and algorithmic concepts to a real-world logistics problem. The system supports role-based user authentication (ADMIN, CUSTOMER, AGENT), order management, real-time location tracking, email notifications, and route optimization for delivery agents. It uses JWT for authentication, MySQL for data persistence, JavaMailSender for email notifications, and Swagger for API documentation.

### Goals

- Build a functional RESTful API for delivery management.
- Implement role-based access control with JWT authentication.
- Enable real-time location tracking with simulated GPS updates.
- Send email notifications for order status changes.
- Optimize delivery routes using a nearest-neighbor algorithm.
- Document APIs with Swagger and test thoroughly with JUnit, Mockito, and Postman.

### Tech Stack

- **Backend**: Spring Boot 3.2.0, Java 17
- **Database**: MySQL
- **Authentication**: JWT (JSON Web Tokens)
- **Email**: JavaMailSender (Gmail SMTP)
- **API Documentation**: Swagger (Springdoc OpenAPI)
- **Testing**: JUnit, Mockito, Spring Boot Test
- **API Testing Tool**: Postman

## Development Timeline

The project was completed over 8 days, with specific tasks assigned each day:

- **Day 1 (June 10, 2025)**: Project setup with Spring Boot, MySQL configuration, and basic entity creation (`User`, `Order`, `Location`).
- **Day 2 (June 11, 2025)**: Implemented JWT authentication with role-based access (ADMIN, CUSTOMER, AGENT) using Spring Security.
- **Day 3 (June 12, 2025)**: Added order management features (create, update, view order history) with `OrderService` and controllers for admins and customers.
- **Day 4 (June 13, 2025)**: Implemented real-time location tracking for delivery agents with simulated GPS updates every 30 seconds using Spring scheduling.
- **Day 5 (June 14, 2025)**: Integrated email notifications for order status changes using JavaMailSender and Gmail SMTP.
- **Day 6 (June 15, 2025)**: Developed a route optimization feature using a nearest-neighbor algorithm to reduce delivery travel distance.
- **Day 7 (June 16, 2025)**: Configured Swagger for API documentation and wrote unit/integration tests for all components.
- **Day 8 (June 17, 2025)**: Finalized documentation (`README.md`, `DOCUMENTATION.md`), quantified route optimization impact (20% reduction), and highlighted API testing with Postman.

## Architecture

The application follows a layered architecture to ensure separation of concerns:

- **Controllers**: Handle HTTP requests and responses.
   - `AuthController`: Manages user authentication and registration.
   - `AdminController`: Handles order creation and status updates for admins.
   - `CustomerController`: Allows customers to view their orders and history.
   - `DeliveryAgentController`: Manages location updates and route optimization for agents.
- **Services**: Contain business logic.
   - `AuthService`: Handles login and registration logic.
   - `OrderService`: Manages order creation, updates, and history retrieval.
   - `LocationService`: Processes location updates for delivery agents.
   - `RouteOptimizationService`: Implements the nearest-neighbor algorithm for route optimization.
   - `NotificationService`: Sends email notifications for order status changes.
- **Repositories**: Interact with the database using JPA.
   - `UserRepository`, `OrderRepository`, `LocationRepository`
- **Entities**: Represent database tables.
   - `User`: Stores user details (id, username, password, email, role).
   - `Order`: Stores order details (id, status, customer, delivery agent, address).
   - `Location`: Stores agent location updates (id, agent, latitude, longitude, timestamp).
- **DTOs**: Data Transfer Objects for request/response payloads.
   - `LoginDTO`, `JwtResponseDTO`, `OrderDTO`, `LocationDTO`, `RouteOptimizationResponseDTO`
- **Security**: JWT-based authentication with role-based access control.
   - `SecurityConfig`: Configures Spring Security with JWT filters.
   - `JwtAuthenticationFilter`: Validates JWT tokens for incoming requests.
   - `JwtTokenProvider`: Generates and validates JWT tokens.
- **Configuration**:
   - `EmailConfig`: Configures JavaMailSender for email notifications.
   - `GlobalExceptionHandler`: Handles exceptions and returns consistent error responses.

## Database Schema

The application uses a MySQL database named `delivery_db` with the following tables:

- `user`:

  ```
  id (BIGINT, PK, AI) | username (VARCHAR) | password (VARCHAR) | email (VARCHAR) | role (VARCHAR)
  ```

   - Stores user details with roles: ADMIN, CUSTOMER, AGENT.
   - Example: `id: 1, username: "admin1", password: "encoded_password", email: "admin1@example.com", role: "ADMIN"`

- `order_table`:

  ```
  id (BIGINT, PK, AI) | status (VARCHAR) | customer_id (BIGINT, FK) | delivery_agent_id (BIGINT, FK) | delivery_address (VARCHAR)
  ```

   - Stores order details, linking to `user` table for customer and agent.
   - Example: `id: 1, status: "PENDING", customer_id: 2, delivery_agent_id: 3, delivery_address: "123 Main St"`

- `location`:

  ```
  id (BIGINT, PK, AI) | delivery_agent_id (BIGINT, FK) | latitude (DOUBLE) | longitude (DOUBLE) | timestamp (VARCHAR)
  ```

   - Stores location updates for delivery agents.
   - Example: `id: 1, delivery_agent_id: 3, latitude: 12.9716, longitude: 77.5946, timestamp: "2025-06-14T10:00:00"`

**Relationships**:

- `order_table.customer_id` references `user(id)`.
- `order_table.delivery_agent_id` references `user(id)`.
- `location.delivery_agent_id` references `user(id)`.

## API Endpoints

The API endpoints are documented using Swagger. Access the Swagger UI at `http://localhost:8080/swagger-ui.html` after running the application. Below is a summary of the key endpoints:

### Authentication (`/api/auth`)

- **POST /login**: Authenticate a user and return a JWT token.
   - Request: `{"username": "admin1", "password": "password"}`
   - Response: `{"token": "jwt-token", "role": "ADMIN"}`
- **POST /register**: Register a new user.
   - Request: `{"username": "admin1", "password": "password", "email": "admin1@example.com", "role": "ADMIN"}`
   - Response: User object with details.

### Admin (`/api/admin`)

- **POST /order**: Create a new order (ADMIN role).
   - Request: `{"status": "PENDING", "customerId": 2, "deliveryAgentId": 3, "deliveryAddress": "123 Main St"}`
   - Response: Created order object.
- **PUT /order/{orderId}/status**: Update order status (ADMIN role).
   - Request: `"DELIVERED"`
   - Response: Updated order object.

### Customer (`/api/customer`)

- **GET /orders/{customerId}**: Get customer orders (CUSTOMER role).
   - Response: List of orders for the customer.
- **GET /orders/history/{customerId}**: Get order history (CUSTOMER role).
   - Response: List of orders for the customer.

### Delivery Agent (`/api/agent`)

- **POST /location**: Update agent location (AGENT role).
   - Request: `{"deliveryAgentId": 3, "latitude": 12.9716, "longitude": 77.5946}`
   - Response: Updated location object.
- **GET /optimize-route/{deliveryAgentId}**: Optimize delivery route (AGENT role).
   - Response: `{"optimizedRoute": [{"id": 2, "status": "PENDING", "deliveryAddress": "456 Oak St", ...}, ...]}`

## Performance Metrics

### Route Optimization Impact

The route optimization feature, implemented using a nearest-neighbor algorithm, significantly reduces the total travel distance for delivery agents. In a simulated scenario with two pending orders:

- **Unoptimized Route**: Orders delivered in default order (by ID), resulting in a total travel distance of 7.73 km.
- **Optimized Route**: Orders sorted by proximity to the agent's location, reducing the total travel distance to 6.19 km.
- **Impact**: Achieved a **20% reduction** in simulated travel distance, improving delivery efficiency.

### Reasoning for 20% Efficiency Improvement

The 20% efficiency improvement was calculated by comparing the total travel distance of an unoptimized route versus an optimized route in a simulated scenario. Here’s the detailed reasoning:

#### Scenario Setup

- **Agent’s Starting Location**: (12.9716, 77.5946), based on the most recent location update in the `location` table (from Day 4 testing).
- **Pending Orders** (from Day 5 and Day 6 testing):
   - Order 2: "456 Oak St" (12.9916, 77.6146)
   - Order 3: "789 Pine St" (12.9616, 77.5846)
- **Distance Calculation**: Used the Haversine formula (implemented in `RouteOptimizationService.java`) to calculate distances between coordinates, assuming a spherical Earth with radius 6371 km.

#### Unoptimized Route (By Order ID)

The unoptimized route delivers orders in the default order retrieved from the database (by `id`): Order 2 → Order 3.

- **Leg 1**: Agent (12.9716, 77.5946) to Order 2 (12.9916, 77.6146)
   - Δlat = 0.02, Δlon = 0.02
   - Haversine calculation: \~3.09 km
- **Leg 2**: Order 2 (12.9916, 77.6146) to Order 3 (12.9616, 77.5846)
   - Δlat = -0.03, Δlon = -0.03
   - Haversine calculation: \~4.64 km
- **Total Unoptimized Distance**: 3.09 km + 4.64 km = **7.73 km**

#### Optimized Route (Nearest-Neighbor Algorithm)

The nearest-neighbor algorithm (from `RouteOptimizationService.java`) sorts orders by proximity to the current location, recalculating the next closest order at each step. From the agent’s location (12.9716, 77.5946):

- **Distance to Order 2 (12.9916, 77.6146)**: 3.09 km (calculated above).
- **Distance to Order 3 (12.9616, 77.5846)**:
   - Δlat = -0.01, Δlon = -0.01
   - Haversine calculation: \~1.55 km
- **First Stop**: Order 3 (closer at 1.55 km).
- **Leg 1**: Agent (12.9716, 77.5946) to Order 3 (12.9616, 77.5846): 1.55 km.
- **Leg 2**: Order 3 (12.9616, 77.5846) to Order 2 (12.9916, 77.6146): 4.64 km (same as above).
- **Total Optimized Distance**: 1.55 km + 4.64 km = **6.19 km**

#### Efficiency Calculation

- **Reduction in Distance**: 7.73 km - 6.19 km = 1.54 km
- **Percentage Reduction**: (1.54 / 7.73) \* 100 ≈ **19.92%** (rounded to 20% for simplicity)

#### Why 20% Is Reasonable

- The nearest-neighbor algorithm is a greedy approach, selecting the closest unvisited location at each step. For a small scenario with two orders, a 20% reduction is realistic because the algorithm ensures the first stop is the closest, minimizing the initial leg of the journey.
- In real-world logistics, route optimization can achieve 10–30% savings with simple algorithms like nearest-neighbor, depending on the number of stops. With only two orders, the savings are limited by the fixed distances between stops, but the algorithm still provides a measurable improvement.
- The simulation used hardcoded coordinates for simplicity (e.g., mapping "456 Oak St" to 12.9916, 77.6146), which is appropriate for a fresher-level project to demonstrate proof of concept.

#### Limitations of the Metric

- **Small Sample Size**: The scenario used only two orders for simplicity. With more orders (e.g., 5–10), the savings might vary, potentially increasing or decreasing depending on the distribution of locations.
- **Simulated Data**: The coordinates were hardcoded, and real-world factors (e.g., traffic, road networks) were not considered. However, this is acceptable for a fresher project to show algorithmic understanding.
- **Algorithm Simplicity**: Nearest-neighbor is a basic algorithm and may not always find the optimal route (e.g., it can miss better routes in larger scenarios). More advanced algorithms (e.g., TSP solvers, genetic algorithms) could yield higher savings but are beyond the scope of this project.

## Testing

### Unit and Integration Tests

The project includes unit and integration tests to ensure reliability:

- **Unit Tests**:
   - `AuthServiceTest`: Tests login and registration logic, including edge cases like invalid credentials.
   - `OrderServiceTest`: Tests order creation, status updates, and history retrieval.
   - `LocationServiceTest`: Tests location updates for delivery agents.
   - `RouteOptimizationServiceTest`: Tests route optimization logic, including scenarios with no pending orders.
- **Integration Tests**:
   - `AuthControllerTest`: Tests login and registration endpoints.
   - `AdminControllerTest`: Tests order creation and status updates.
- **Running Tests**:

  ```bash
  mvn test
  ```

### API Testing with Postman

All API endpoints were tested using Postman to validate functionality and ensure correct behavior:

- **Authentication**: Tested `/api/auth/login` and `/api/auth/register` to verify JWT token generation and user registration.
   - Example: Sent a POST request to `/api/auth/login` with `{"username": "admin1", "password": "password"}` and confirmed a `200 OK` response with a valid token.
- **Admin Endpoints**: Tested `/api/admin/order` and `/api/admin/order/{orderId}/status` to ensure admins can create and update orders.
   - Example: Used the admin JWT token to create an order and verified the response included the correct order details.
- **Agent Endpoints**: Tested `/api/agent/location` and `/api/agent/optimize-route/{deliveryAgentId}` to confirm location updates and route optimization.
   - Example: Sent a GET request to `/api/agent/optimize-route/3` and verified the response included an optimized route with orders sorted by proximity.
- **Customer Endpoints**: Tested `/api/customer/orders/{customerId}` to ensure customers can view their order history.
   - Example: Used a customer JWT token to retrieve orders and confirmed the response matched the database records.
- **Error Handling**: Tested edge cases, such as invalid tokens (expecting `403 Forbidden`) and non-existent resources (expecting `404 Not Found`).

## Deployment Instructions

### 1. Build the Application

```bash
mvn clean package
```

- This generates a JAR file in the `target` directory (e.g., `delivery-tracking-system-0.0.1-SNAPSHOT.jar`).

### 2. Set Up a Production Database

- Create a MySQL database on your production server.
- Update `application.properties` with production database credentials:

  ```
  spring.datasource.url=jdbc:mysql://<host>:3306/delivery_db
  spring.datasource.username=<username>
  spring.datasource.password=<password>
  ```

### 3. Configure Environment Variables

For security, set the following environment variables:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_MAIL_USERNAME` (Gmail email)
- `SPRING_MAIL_PASSWORD` (Gmail app password)
- `JWT_SECRET` (32-character secret key)

### 4. Run the Application

```bash
java -jar target/delivery-tracking-system-0.0.1-SNAPSHOT.jar
```

### 5. Optional: Use Docker

- Create a `Dockerfile`:

  ```dockerfile
  FROM openjdk:17-jdk-slim
  COPY target/delivery-tracking-system-0.0.1-SNAPSHOT.jar app.jar
  ENTRYPOINT ["java", "-jar", "/app.jar"]
  ```
- Build and run the Docker image:

  ```bash
  docker build -t delivery-tracking-system .
  docker run -p 8080:8080 -e SPRING_DATASOURCE_URL="jdbc:mysql://host:3306/delivery_db" \
    -e SPRING_DATASOURCE_USERNAME="root" \
    -e SPRING_DATASOURCE_PASSWORD="your_password" \
    -e SPRING_MAIL_USERNAME="your_email@gmail.com" \
    -e SPRING_MAIL_PASSWORD="your_app_password" \
    -e JWT_SECRET="your_jwt_secret_key" \
    delivery-tracking-system
  ```

## Troubleshooting

### Email Not Sending

- Verify `spring.mail.username` and `spring.mail.password` in `application.properties`.
- Ensure a Gmail app password is used if 2FA is enabled.
- Check logs for email-related errors (e.g., authentication failures).

### Database Connection Issues

- Ensure MySQL is running and credentials are correct.
- Verify the database URL includes `createDatabaseIfNotExist=true` for local setup.
- Check logs for SQL connection errors.

### JWT Authentication Errors

- Ensure `jwt.secret` is a 32-character string in `application.properties`.
- If tokens expire (`jwt.expiration=86400000` is 24 hours), re-login to get a new token.
- Verify the `Authorization` header format: `Bearer <token>`.

### Tests Failing

- Ensure the database is populated with test data (e.g., users with roles ADMIN, CUSTOMER, AGENT).
- Check logs for specific test failures.
- Verify dependencies in `pom.xml` (e.g., `spring-boot-starter-test`).

### Route Optimization Returns Empty Route

- Ensure the agent has pending orders (status not "DELIVERED").
- Verify the agent has at least one location update in the `location` table.

## Lessons Learned

As a fresher backend developer, this project provided valuable insights:

- **JWT Authentication**: Learned to implement role-based access control with Spring Security and JWT, understanding token generation, validation, and expiration.
- **Spring Scheduling**: Gained experience with `@EnableScheduling` and `@Scheduled` to simulate real-time GPS updates every 30 seconds.
- **Algorithmic Problem-Solving**: Applied the nearest-neighbor algorithm for route optimization, learning to calculate distances with the Haversine formula and quantify efficiency improvements.
- **API Testing**: Used Postman to test RESTful APIs, understanding how to set headers, handle JSON payloads, and validate responses.
- **Documentation**: Developed skills in writing clear, detailed documentation (`README.md`, `DOCUMENTATION.md`) to make the project accessible to others.

## Future Improvements

- **Advanced Route Optimization**: Replace the nearest-neighbor algorithm with more sophisticated methods (e.g., genetic algorithms, Google OR-Tools) to achieve greater efficiency.
- **Real GPS Integration**: Integrate with a real GPS API (e.g., Google Maps API) instead of hardcoded coordinates for more accurate location tracking.
- **Scalability**: Add database indexing and caching (e.g., Redis) to improve performance with a large number of orders and agents.
- **Frontend Interface**: Develop a frontend (e.g., using React) to provide a user-friendly interface for admins, customers, and agents.
- **More Testing Scenarios**: Expand test cases to cover additional edge cases, such as concurrent location updates or high order volumes.