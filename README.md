# Trade Engine (Brokerage API)

A **Java 21** + **Spring Boot** backend API for the **brokerage** firms. It manages customer **assets** and **orders**, and supports **order cancellation** plus **admin-only order matching**.

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
│  │  │           │  ├─ port/                       # application ports (hexagonal)
│  │  │           │  │  ├─ in/                      # input ports (use cases)
│  │  │           │  │  └─ out/                     # output ports (driven ports)
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
│  │  │              │  ├─ initialdata/            # e.g. file-based initial data
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
- Java 21+ (recommended: Temurin 21)

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

### List orders (paged)
`GET /api/v1/orders`

Optional query parameters:

| Parameter | Description |
|-----------|-------------|
| `customerId` | Target customer (optional; when omitted, inferred from the authenticated user; admins may supply it to query another customer). |
| `startDate` | Inclusive lower bound for `createDate`, ISO-8601 (`LocalDateTime`). |
| `endDate` | Inclusive upper bound for `createDate`, ISO-8601 (`LocalDateTime`). |
| `status` | Order status filter (`PENDING`, `MATCHED`, `CANCELED`). |
| `side` | Order side filter (`BUY`, `SELL`). |
| `assetName` | Asset symbol filter. |
| `page` | **Zero-based** page index (first page is `1`; defaults to `1` if omitted). |
| `size` | Page size (defaults to `10` if omitted). |

```bash
curl --request GET "http://localhost:8080/api/v1/orders?page=0&size=10" \
  --user "CUST-001:cust123"
```

Optional filters example:

```bash
curl -G "http://localhost:8080/api/v1/orders" \
  --user "CUST-001:cust123" \
  --data-urlencode "status=PENDING" \
  --data-urlencode "side=BUY" \
  --data-urlencode "assetName=testhisse1"
```

**Response** (`PagedResult`: `content` is the current page; `totalPages` and `totalElements` describe the full result set.)

```json
{
  "content": [
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
  ],
  "totalPages": 1,
  "totalElements": 1
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

**Response** (`201 Created`)

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

Optional query parameter `customerId`: same semantics as list orders (admins may target a specific customer).

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

Runs matching asynchronously; returns **`202 Accepted`** with an empty body.

```bash
curl --request POST "http://localhost:8080/api/v1/orders/match" \
  --user "admin:admin123"
```

### List assets (paged)
`GET /api/v1/assets`

Optional query parameters:

| Parameter | Description |
|-----------|-------------|
| `customerId` | Target customer (optional; when omitted, inferred from the authenticated user; admins may supply it). |
| `page` | **One-based** page number (first page is `1`; mapped internally to Spring Data). |
| `size` | Page size (defaults to `10` if omitted). |

```bash
curl --request GET "http://localhost:8080/api/v1/assets?page=1&size=10" \
  --user "CUST-001:cust123"
```

**Response**

```json
{
  "content": [
    {
      "customerId": "CUST-001",
      "assetName": "TRY",
      "size": 10000.00,
      "usableSize": 10000.00
    }
  ],
  "totalPages": 1,
  "totalElements": 1
}
```

---
