# Trade Engine (Brokerage API)

A **Java 21** + **Spring Boot** backend API for the **brokerage** case study. It manages customer **assets** and **orders**, and supports **order cancellation** plus **admin-only order matching**.

**Tech stack**
- **Java**: 21
- **Spring Boot**: 3.5.x
- **Build**: Gradle (`build.gradle.kts`)
- **DB**: H2 (in-memory)
- **API Docs (Swagger UI)**: springdoc-openapi
- **Auth**: HTTP Basic Authentication

---

## Project Structure

**Base package:** `com.brokerage.tradeengine`

```text
tradeengine
├─ build.gradle.kts
├─ settings.gradle.kts
├─ gradlew
├─ gradlew.bat
├─ gradle/
├─ Dockerfile
├─ .dockerignore
├─ src
│  ├─ main
│  │  ├─ java
│  │  │  └─ com
│  │  │     └─ brokerage
│  │  │        └─ tradeengine
│  │  │           ├─ TradeengineApplication.java
│  │  │           ├─ application/                 # use cases, DTOs, input ports, app services
│  │  │           │  ├─ dto/
│  │  │           │  │  ├─ mapper/
│  │  │           │  │  ├─ request/
│  │  │           │  │  └─ response/
│  │  │           │  ├─ exception/
│  │  │           │  ├─ port/                       # inbound ports; provider interfaces
│  │  │           │  │  └─ in/
│  │  │           │  ├─ service/
│  │  │           │  └─ usecase/
│  │  │           ├─ domain/                        # aggregates, domain services, repository ports
│  │  │           │  ├─ constant/
│  │  │           │  ├─ exception/
│  │  │           │  ├─ model/
│  │  │           │  ├─ repository/
│  │  │           │  └─ service/
│  │  │           └─ infrastructure/              # Spring adapters, configuration
│  │  │              ├─ adapter/
│  │  │              │  ├─ config/                 # e.g. file-based initial data
│  │  │              │  ├─ persistence/            # JPA entities, Spring Data, adapters
│  │  │              │  │  ├─ entity/
│  │  │              │  │  ├─ mapper/
│  │  │              │  │  ├─ repository/
│  │  │              │  │  └─ specification/
│  │  │              │  ├─ rest/                   # REST controllers, exception handling
│  │  │              │  └─ security/                 # auth integration
│  │  │              └─ config/
│  │  └─ resources
│  │     ├─ application.yml
│  │     ├─ initial-data.json
│  │     ├─ messages.properties
│  │     └─ logback-spring.xml
│  └─ test
│     ├─ java
│     │  └─ com
│     │     └─ brokerage
│     │        └─ tradeengine
│     │           ├─ TradeengineApplicationTests.java
│     │           ├─ integration/
│     │           ├─ application/
│     │           │  └─ usecase/
│     │           └─ domain/
│     │              ├─ model/
│     │              └─ service/
│     └─ resources
│        └─ application-test.properties

```

---

## Build & Run

### Requirements
- Java 21+

### Run with Gradle

```bash
./gradlew clean test
./gradlew bootRun
```

By default the application listens on `http://localhost:8080`.

### Run with Docker

```bash
docker build -t tradeengine .
docker run -d -p 8080:8080 --name tradeengine tradeengine
```

---

## Configuration

- **Server port**: `8080`
- **H2 console**: `http://localhost:8080/h2-console`
  - JDBC URL (default): `jdbc:h2:mem:tradeenginedb`

---

## API Docs (Swagger UI)

Swagger UI:
- `http://localhost:8080/swagger-ui/index.html`

OpenAPI JSON:
- `http://localhost:8080/v3/api-docs`

---

## Authentication

All endpoints require **HTTP Basic Authentication**.

Seed credentials (examples):
- **Customer**: `CUST-001` / `cust123`
- **Customer**: `CUST-002` / `cust123`
- **Admin**: `admin` / `admin123`

---

## API

Base path: `/api/v1`

### Get orders
`GET /api/v1/orders`

```bash
curl --request GET "http://localhost:8080/api/v1/orders" \
  --user "CUST-001:cust123"
```

**Response**

```json
{
  "orders": [
    {
      "orderId": 1,
      "customerId": "CUST-001",
      "assetName": "testhisse1",
      "orderSide": "BUY",
      "size": 20.00,
      "price": 10.00,
      "status": "PENDING",
      "createDate": "2026-05-10T18:53:01.65803"
    }
  ]
}
```

### Create order
`POST /api/v1/orders`

```bash
curl --request POST "http://localhost:8080/api/v1/orders" \
  --user "CUST-001:cust123" \
  --header "Content-Type: application/json" \
  --data-raw '{
    "assetName": "testhisse1",
    "side": "BUY",
    "size": 20,
    "price": 10.00
  }'
```

**Response**

```json
{
  "customerId": "CUST-001",
  "assetName": "testhisse1",
  "orderSide": "BUY",
  "size": 20.00,
  "price": 10.00,
  "tryUsableSize": 9800.00,
  "status": "PENDING",
  "createDate": "2026-05-10T18:53:01.658030453"
}
```

### Cancel order
`DELETE /api/v1/orders/{orderId}`

```bash
curl --request DELETE "http://localhost:8080/api/v1/orders/1" \
  --user "CUST-001:cust123"
```

**Response**

```json
{
  "orderId": 1,
  "customerId": "CUST-001",
  "assetName": "testhisse1",
  "orderSide": "BUY",
  "size": 20.00,
  "price": 10.00,
  "status": "CANCELED",
  "createDate": "2026-05-10T18:53:01.65803"
}
```

### Match orders (admin)
`POST /api/v1/orders/match`

```bash
curl --request POST "http://localhost:8080/api/v1/orders/match" \
  --user "admin:admin123"
```

### Get assets
`GET /api/v1/assets`

```bash
curl --request GET "http://localhost:8080/api/v1/assets" \
  --user "CUST-001:cust123"
```

**Response**

```json
[
  {
    "customerId": "CUST-001",
    "assetName": "TRY",
    "size": 10000.00,
    "usableSize": 10000.00
  }
]
```

---