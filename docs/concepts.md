# Core Concepts in Delivery Tracking System

## 📦 Objects (Key Entities)

- **User**: Represents system users with roles (ADMIN, CUSTOMER, AGENT).
- **Order**: Delivery order entity with status, address, and assigned agent/customer.
- **Location**: Tracks real-time location (latitude/longitude) of delivery agents.
- **JwtTokenProvider**: Manages token generation and validation.
- **RouteOptimizationService**: Optimizes delivery paths using nearest-neighbor algorithm.
- **EmailService**: Sends notifications for order updates.
- **JwtAuthenticationFilter**: Ensures endpoints are secured and role-accessed.

---

## 🧭 Context (Functional Role of Each Object)

- **User Registration & Login**:
  - Via `/api/auth/register` and `/api/auth/login` endpoints.
  - Generates JWT tokens for secure access.

- **Order Management**:
  - Admin creates/updates orders.
  - Customers view their history.
  - Agents handle assigned deliveries.

- **Location Tracking**:
  - Agents update GPS coordinates every 30 seconds via `/api/agent/location`.

- **Email Notifications**:
  - Triggered on order status changes.

- **Route Optimization**:
  - Agents get optimal delivery sequence from `/api/agent/optimize-route/{id}`.

---

## 📄 Information (Data & Logic)

### User
- Fields: `username`, `password`, `email`, `role`
- Roles: `ADMIN`, `CUSTOMER`, `AGENT`

### Order
- Fields: `status`, `customerId`, `deliveryAgentId`, `deliveryAddress`

### Location
- Fields: `latitude`, `longitude`, `timestamp`, `deliveryAgentId`

### JWT Token
- Header: `Authorization: Bearer <token>`
- Expiration: `86400000 ms` (1 day)

### Route Optimization
- Algorithm: Nearest-neighbor
- Performance: ~20% efficiency gain in sample runs

### Testing Strategy
- **JUnit/Mockito**: Service and controller-level testing
- **Postman**: End-to-end API flow testing

---

## 🔗 Placement
- Recommended location: `docs/concepts.md`
