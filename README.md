    FluxMart API
FluxMart API is the backend service powering the FluxMart e‑commerce platform. Built with Spring Boot, it provides secure authentication, product management, order handling, and payment integration. Designed for scalability, maintainability, and seamless integration with the React frontend.


## 🛠 Tech Stack

| Layer              | Technology / Tool            | Purpose                                      |
|--------------------|------------------------------|----------------------------------------------|
| Language           | Java 21 (Temurin)            | Core backend language                        |
| Framework          | Spring Boot                  | REST API, dependency injection, app config   |
| Build Tool         | Maven 3.9+                   | Dependency management & build lifecycle      |
| Database           | MySQL 8.0                    | Relational data storage                      |
| Migrations         | Flyway                       | Schema versioning & evolution                |
| Auth & Security    | Spring Security + JWT        | Authentication, authorization, role control  |
| Payments           | M-pesa+Stripe + Webhooks     | Checkout flow & payment confirmation         |
| Containerization   | Docker + Docker Compose      | Environment consistency & orchestration      |
| Deployment         | Railway / Cloud Provider     | Hosting & CI/CD integration                  |
| Config Management  | application.yml profiles     | Dev, Docker, and Prod environment configs    |
| Analytics          | Custom Reports (Spring Data) | Product trends, margins, stock insights      |


📦 Features
- 🔐 Authentication & Authorization
- JWT‑based login/signup
- Role‑based access control (admin vs user)
- 🛒 Product Management
- CRUD operations for products, categories, and stock
- Search, filter, and sort endpoints
- 📦 Order Handling
- Cart management
- Checkout flow with Stripe integration
- Webhook support for payment confirmation
- 📊 Admin Analytics
- Fast/slow‑moving product reports
- Profit margin and stock level insights

📂 Project Structure
fluxmartApi/
├── src/main/java/com/fluxmartApi/
│   ├── auth/             # JWT config, filters, services
│   ├── product/          # Product entities, controllers, services
│   ├── order/            # Order/cart handling
│   ├── config/           # DB, CORS, security configs
│   └── FluxmartApiApp.java # Main Spring Boot entrypoint
│
├── src/main/resources/
│   ├── application.yml   # Config (profiles: dev, docker, prod)
│   └── db/migration/     # Flyway migration scripts
│
├── Dockerfile            # Multi-stage build for API
└── pom.xml               # Maven dependencies



🛠️ Getting Started
Prerequisites
- Java 21 (Temurin recommended)
- Maven 3.9+
- Docker & Docker Compose
- MySQL 8.0 (if running locally without Docker)
Local Development




