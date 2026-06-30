# TrustPay — Digital Banking Microservices Platform
## Live Demo
API Gateway: http://13.48.24.109:9090
(Note: instance may be stopped to control AWS costs — contact me to spin it up for a live demo)

A full-stack digital banking system built with Spring Boot microservices, demonstrating JWT-based authentication, service discovery, API gateway routing, event-driven communication with Kafka, Redis caching, and containerized deployment with Docker.

---

## Architecture

```
                          ┌─────────────────┐
                          │   API Gateway    │  :9090
                          │  (JWT validation)│
                          └────────┬─────────┘
                                   │
                  ┌────────────────┼────────────────┐
                  │                │                 │
          ┌───────▼──────┐ ┌──────▼───────┐ ┌───────▼────────┐
          │ Auth Service │ │   Account    │ │  Notification   │
          │    :8081     │ │   Service    │ │    Service      │
          │              │ │    :8082     │ │     :8083       │
          └───────┬──────┘ └──────┬───────┘ └────────┬─────────┘
                  │                │                  │
                  └────────────────┼──────────────────┘
                                   │
                         ┌─────────▼──────────┐
                         │   Eureka Server     │  :8761
                         │ (service discovery) │
                         └──────────────────────┘

  Infrastructure: MySQL · Redis · Kafka + Zookeeper
```

All services register with Eureka. The API Gateway is the single public entry point — it validates JWT tokens and routes requests to the appropriate downstream service using Spring Cloud LoadBalancer (client-side load balancing).

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Security | Spring Security, JWT (jjwt) |
| Persistence | Spring Data JPA, MySQL 8 |
| Caching | Redis (balance cache, token blacklist) |
| Messaging | Apache Kafka |
| Service Discovery | Netflix Eureka |
| API Gateway | Spring Cloud Gateway (WebFlux) |
| Inter-service calls | OpenFeign |
| Email | Spring Mail + Brevo SMTP |
| Containerization | Docker, Docker Compose |

---

## Services

### Eureka Server (`:8761`)
Service registry. Every other service registers here on startup and discovers peers through it.

### Auth Service (`:8081`)
- User registration and login with BCrypt password hashing
- JWT token generation and validation
- Token blacklist on logout (stored in Redis with TTL matching token expiry)
- Transaction PIN management (set/verify, BCrypt-hashed)
- Internal endpoints for other services to resolve user details by ID or email

### Account Service (`:8082`)
- Account creation, balance inquiry (Redis-cached), deposit, withdraw
- Peer-to-peer transfers — by account number or by recipient email
- Transaction PIN verification required before any transfer
- Full transaction history per account
- Soft-delete (close/reopen) accounts instead of hard deletion
- Publishes transaction events to Kafka for async notification delivery

### Notification Service (`:8083`)
- Kafka consumer for account-created, deposit, withdrawal, and transfer events
- Sends transactional emails via Brevo SMTP
- Fully decoupled from Account Service — notification failures never block a transaction

### API Gateway (`:9090`)
- Single public entry point for all client requests
- Custom JWT validation `GlobalFilter` — no Spring Security dependency, manual filter chain
- Redis-backed rate limiting (per-IP for public routes, per-user for authenticated routes)
- Routes to all backend services via Eureka-based service discovery (`lb://` URIs)

---

## Key Features

- **JWT authentication** validated once at the Gateway — downstream services trust forwarded `X-User-Id` headers instead of re-validating tokens
- **Redis caching** on account balance reads with cache eviction on every write (deposit/withdraw/transfer)
- **Kafka-driven async notifications** — deposits, withdrawals, transfers, and account creation all trigger non-blocking email delivery
- **Transfer by email** — send money using a recipient's email address instead of requiring their account number
- **Transaction PIN** — a second authentication factor required for any money movement, independent of the JWT
- **Atomic transfers** — `@Transactional` ensures sender debit and receiver credit either both succeed or both roll back
- **Soft account closure** — accounts are marked `CLOSED` rather than deleted, preserving transaction history

---

## Running Locally

### Prerequisites
- Java 21
- Maven
- Docker & Docker Compose
- MySQL 8 (or use the Dockerized version below)

### Option 1 — Docker Compose (recommended)

```bash
# Clone the repo
git clone https://github.com/Karthik-a39/trustpay.git
cd trustpay

# Create a .env file (see .env.example)
cp .env.example .env
# Fill in JWT_SECRET, BREVO_SMTP_USERNAME, BREVO_SMTP_KEY, NOTIFICATION_FROM_EMAIL

# Start the full stack
docker compose up -d

# Watch logs
docker compose logs -f
```

Eureka dashboard: `http://localhost:8761`
API entry point: `http://localhost:9090`

### Option 2 — Run services individually

```bash
# Start infrastructure first
docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root mysql:8
docker run -d -p 6379:6379 redis:7-alpine
# Kafka + Zookeeper — see docker-compose.yml for full config

# Then run each service in order:
cd eureka-server && mvn spring-boot:run
cd auth-service && mvn spring-boot:run
cd account-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
```

---

## API Overview

All requests go through the Gateway at `http://localhost:9090`.

| Method | Endpoint | Description | Auth required |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | No |
| POST | `/api/auth/login` | Login, returns JWT | No |
| POST | `/api/auth/logout` | Blacklist current token | Yes |
| POST | `/api/account` | Create a bank account | Yes |
| GET | `/api/account/my-accounts` | List accounts for current user | Yes |
| GET | `/api/account/{accountNumber}/balance` | Check balance (cached) | Yes |
| POST | `/api/account/{accountNumber}/deposit` | Deposit funds | Yes |
| POST | `/api/account/{accountNumber}/withdraw` | Withdraw funds | Yes |
| POST | `/api/account/transfer` | Transfer by account number | Yes + PIN |
| POST | `/api/account/transfer-via-email` | Transfer by recipient email | Yes + PIN |
| GET | `/api/account/{accountNumber}/transactions` | Transaction history | Yes |
| PUT | `/api/account/{accountNumber}/close` | Close (soft-delete) account | Yes |

A full Postman collection is included at `/postman/TrustPay.postman_collection.json`.

---

## Project Structure

```
trustpay/
├── eureka-server/
├── auth-service/
├── account-service/
├── notification-service/
├── api-gateway/
├── docker-compose.yml
├── .env.example
└── README.md
```

Each service is a standalone Maven project with its own `Dockerfile`, following a multi-stage build pattern (Maven build stage → lean JRE runtime stage) to keep production images small.

---

## What This Project Demonstrates

- Microservices architecture with service discovery and API gateway routing
- Stateless JWT authentication validated centrally at the edge
- Synchronous inter-service communication (OpenFeign) vs asynchronous event-driven communication (Kafka)
- Caching strategy with Redis, including cache invalidation on writes
- Multi-factor authorization for sensitive operations (JWT + transaction PIN)
- Containerization with multi-stage Docker builds and Docker Compose orchestration
- Defensive design patterns: idempotent operations, ownership checks, soft deletes

---

## License

This project was built as a portfolio/learning project and is available under the MIT License.
