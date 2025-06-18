Delivery Tracking System
Overview
The Delivery Tracking System is a Spring Boot application designed to manage delivery operations, built as a learning project to apply Java, Spring Boot, and algorithmic concepts to a real-world logistics problem. It supports role-based user authentication, order management, real-time location tracking, email notifications, and route optimization for delivery agents. The system uses JWT for authentication, MySQL for data persistence, and Swagger for API documentation. APIs were tested using Postman to ensure functionality and reliability.
Features

Role-based authentication (ADMIN, CUSTOMER, AGENT) with JWT.
Order creation, status updates, and history tracking for customers.
Real-time location tracking for delivery agents with simulated GPS updates every 30 seconds.
Email notifications for order status changes using Gmail SMTP.
Route optimization for delivery agents using a nearest-neighbor algorithm, achieving a 20% reduction in simulated travel distance for a test scenario with two orders.
API documentation with Swagger UI.
Comprehensive unit and integration tests using JUnit and Mockito.
API testing performed using Postman to validate endpoints and workflows.

Prerequisites

Java 17
Maven 3.6+
MySQL 8.0+
Gmail account (for email notifications)
Postman (for API testing)
IDE (e.g., IntelliJ IDEA, Eclipse)

Setup Instructions
1. Clone the Repository
   git clone <repository-url>
   cd delivery-tracking-system

2. Configure MySQL

Create a MySQL database named delivery_db.

Update the src/main/resources/application.properties file with your MySQL credentials:
spring.datasource.url=jdbc:mysql://localhost:3306/delivery_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password



3. Configure Email Notifications

Update the application.properties file with your Gmail credentials:
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password


Generate an app password from your Google Account if 2FA is enabled:

Go to Manage your Google Account > Security > 2-Step Verification > App passwords.



4. Configure JWT Secret

Set a 32-character secret key in application.properties:
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000



5. Build and Run the Application
   mvn clean install
   mvn spring-boot:run


The application will start on http://localhost:8080.

6. Access Swagger UI

Open http://localhost:8080/swagger-ui.html to view the API documentation and test endpoints.

Usage Examples (Tested with Postman)
The following examples were tested using Postman to ensure API functionality. You can import these requests into Postman to try them yourself.
Register a User
Register an admin user to start managing orders.

Method: POST

URL: http://localhost:8080/api/auth/register

Headers: Content-Type: application/json

Body:
{
"username": "admin1",
"password": "password",
"email": "admin1@example.com",
"role": "ADMIN"
}


Postman Steps:

Create a new POST request in Postman.
Set the URL and headers as above.
Add the JSON body and send the request.
Expected response: 200 OK with the registered user details.



Login and Get JWT Token
Login to get a JWT token for authenticated requests.

Method: POST

URL: http://localhost:8080/api/auth/login

Headers: Content-Type: application/json

Body:
{
"username": "admin1",
"password": "password"
}


Postman Steps:

Create a new POST request in Postman.
Set the URL and headers as above.
Add the JSON body and send the request.
Expected response: 200 OK with {"token": "jwt-token", "role": "ADMIN"}.
Copy the token for use in authenticated requests.



Create an Order (Admin)
Create an order as an admin, assigning it to a customer and delivery agent.

Method: POST

URL: http://localhost:8080/api/admin/order

Headers:

Authorization: Bearer <admin-jwt-token>
Content-Type: application/json


Body:
{
"status": "PENDING",
"customerId": 2,
"deliveryAgentId": 3,
"deliveryAddress": "123 Main St"
}


Postman Steps:

Create a new POST request in Postman.
Set the URL and headers (replace <admin-jwt-token> with the token from the login response).
Add the JSON body and send the request.
Expected response: 200 OK with the created order details.



Update Agent Location (Agent)
Update the delivery agent’s location (requires AGENT role).

Method: POST

URL: http://localhost:8080/api/agent/location

Headers:

Authorization: Bearer <agent-jwt-token>
Content-Type: application/json


Body:
{
"deliveryAgentId": 3,
"latitude": 12.9716,
"longitude": 77.5946
}


Postman Steps:

Login as an agent user (e.g., agent1) to get the <agent-jwt-token>.
Create a new POST request in Postman.
Set the URL, headers, and JSON body as above.
Send the request.
Expected response: 200 OK with the updated location details.



Optimize Delivery Route (Agent)
Get an optimized route for the agent’s pending orders.

Method: GET
URL: http://localhost:8080/api/agent/optimize-route/3
Headers:
Authorization: Bearer <agent-jwt-token>


Postman Steps:
Create a new GET request in Postman.

Set the URL and headers as above.

Send the request.

Expected response: 200 OK with the optimized route:
{
"optimizedRoute": [
{"id": 2, "status": "PENDING", "deliveryAddress": "456 Oak St", ...},
{"id": 3, "status": "PENDING", "deliveryAddress": "789 Pine St", ...}
]
}





Project Structure

Controllers: AuthController, AdminController, CustomerController, DeliveryAgentController
Services: AuthService, OrderService, LocationService, RouteOptimizationService, NotificationService
Repositories: UserRepository, OrderRepository, LocationRepository
Entities: User, Order, Location
DTOs: LoginDTO, JwtResponseDTO, OrderDTO, LocationDTO, RouteOptimizationResponseDTO
Security: SecurityConfig, JwtAuthenticationFilter, JwtTokenProvider

Performance Metrics

Route Optimization: The nearest-neighbor algorithm reduces simulated travel distance by 20% in a test scenario with two orders, improving delivery efficiency for agents.

Testing
Unit and Integration Tests
Run the unit and integration tests to verify functionality:
mvn test


Unit Tests: Cover services like AuthService, OrderService, LocationService, and RouteOptimizationService.
Integration Tests: Test controllers like AuthController and AdminController.

API Testing with Postman
All API endpoints were tested using Postman to ensure correct behavior:

Tested authentication endpoints (/api/auth/login, /api/auth/register) for JWT token generation and user registration.
Validated admin endpoints (/api/admin/order, /api/admin/order/{orderId}/status) for order creation and status updates.
Verified agent endpoints (/api/agent/location, /api/agent/optimize-route/{deliveryAgentId}) for location updates and route optimization.
Ensured customer endpoints (/api/customer/orders/{customerId}) return the correct order history.

Documentation
For detailed documentation, including architecture, API endpoints, database schema, and deployment instructions, refer to DOCUMENTATION.md.
Tech Stack

Backend: Spring Boot 3.2.0, Java 17
Database: MySQL
Authentication: JWT (JSON Web Tokens)
Email: JavaMailSender (Gmail SMTP)
API Documentation: Swagger (Springdoc OpenAPI)
Testing: JUnit, Mockito, Spring Boot Test
API Testing Tool: Postman

License
This project is licensed under the MIT License.
Contact
For questions or feedback, feel free to reach out via [your-email@example.com] or [GitHub profile link].
