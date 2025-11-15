# 🛒 OnlineShop – Full REST API Backend for E-Commerce

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Framework-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36)
![Status](https://img.shields.io/badge/Project-Training_Project-yellow)
![REST API](https://img.shields.io/badge/API-REST%20Full%20Backend-purple)

---

# 📌 Overview

**OnlineShop** is a **full REST API backend** for an e-commerce system.  
This is a **training project**, frontend-independent, and usable by any client (Web, Mobile, Desktop) over HTTP.

It implements product catalog, categories, orders, user roles, security, and full CRUD operations.  
HTML documentation is available at the **root endpoint** of the running application.

---

# ✨ Key Features

- 🌐 Product catalog & categories management
- 🛒 Shopping cart & checkout process
- 📜 Order history and management
- 🖼 Product images upload and display
- 🔒 Authentication & role-based authorization
- 🛡 Role management (Admin, Manager, User)
- 🧩 Fully decoupled REST API architecture
- 📖 Interactive API documentation via Swagger UI
- ✅ Comprehensive unit and integration tests

---

# 🧰 Technology Stack

### Backend

- Java 17
- Spring Boot, Spring Web, Spring Security
- Spring Data JPA, Hibernate
- Maven

### Database

- PostgreSQL

### Infrastructure

- Docker & Docker Compose
- Deployment options include Heroku

---

# 🏗 Architecture Diagram

### General Structure


```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   REST API      │◄───│   Business       │◄───│   Data Access   │
│   Controllers   │    │   Services       │    │   Layer (JPA)   │
└─────────────────┘    └──────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   HTTP          │    │   Business       │    │   PostgreSQL    │
│   Clients       │    │   Logic & DTOs   │    │   Database      │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```


---

# 🗄 Database Diagram (Mermaid)

```mermaid
erDiagram
    USERS {
        integer user_id PK
        string username
        string email
        string phone_number
        string hash_password
        string role
        string status
    }

    CART {
        integer cart_id PK
        integer user_id FK
    }

    CART_ITEMS {
        integer cart_item_id PK
        integer cart_id FK
        integer product_id FK
        int quantity
    }

    PRODUCTS {
        integer product_id PK
        string product_name
        string product_description
        decimal product_price
        decimal product_discount_price
        string image
        integer category_id FK
    }

    CATEGORIES {
        integer category_id PK
        string category_name
        string image
    }

    ORDERS {
        integer order_id PK
        integer user_id FK
        datetime created_at
        datetime updated_at
        string delivery_address
        string delivery_method
        string contact_phone
        string status
    }

    ORDER_ITEMS {
        integer order_item_id PK
        integer order_id FK
        integer product_id FK
        int quantity
        decimal price_at_purchase
    }

    FAVOURITES {
        integer favourite_id PK
        integer user_id FK
        integer product_id FK
    }

    USERS ||--o{ CART : owns
    USERS ||--o{ ORDERS : places
    USERS ||--o{ FAVOURITES : has
    CART ||--o{ CART_ITEMS : contains
    PRODUCTS ||--o{ CART_ITEMS : appears_in
    PRODUCTS ||--o{ ORDER_ITEMS : appears_in
    CATEGORIES ||--o{ PRODUCTS : includes
    ORDERS ||--o{ ORDER_ITEMS : includes

```

---

# 📦 Entities & DTOs Overview

### User

- **Entity**: `User` – id, username, email, phoneNumber, hashPassword, role, status, cart, orders, favourites
- **DTOs**:
    - `UserRequestDto` – registration data
    - `UserResponseDto` – returned user info
    - `UserUpdateRequestDto` – update user data

### Cart & CartItem

- **Entity**: `Cart`, `CartItem`
- **DTOs**: `CartResponseDto`, `CartItemRequestDto`, `CartItemResponseDto`, `CartItemUpdateDto`

### Product & Category

- **Entities**: `Product`, `Category`
- **DTOs**: `ProductRequestDto`, `ProductResponseDto`, `ProductResponseForUserDto`, `ProductUpdateDto`, `CategoryRequestDto`, `CategoryResponseDto`, `CategoryUpdateDto`

### Order & OrderItem

- **Entities**: `Order`, `OrderItem`
- **DTOs**: `OrderRequestDto`, `OrderResponseDto`, `OrderStatusResponseDto`, `OrderItemRequestDto`, `OrderItemResponseDto`, `OrderItemUpdateDto`

### Favourite

- **Entity**: `Favourite`
- **DTO**: `FavouriteResponseDto`

### Statistic

- **DTOs**: `ProductStatisticResponseDto`, `ProfitStatisticRequestDto`, `ProfitStatisticsResponseDto`, `GroupByPeriod`

---

# 🛠 Services Overview

### UserService

- Registration, update, delete, renew user
- Email confirmation & password encoding
- Get current user / user by email

### CartService

- Get full cart, clear cart, transfer cart to order
- Save cart

### CartItemService

- Add, remove, update items in cart
- Get all items of current cart

### ProductService

- CRUD operations for products
- Set discount, get products by criteria, get top discounted

### CategoryService

- CRUD operations for categories
- Get category by ID or name

### OrderService

- Save order, update delivery, cancel order, confirm payment
- Get order by ID, orders by user
- Update order status, get order status DTO

### OrderItemService

- Add, delete, update items in order
- Get current open order

### FavouriteService

- Add, delete, get favourites

### StatisticService

- Top sold, cancelled, pending products
- Profit statistics by period

### ConfirmationCodeService

- Generate, save, send confirmation codes
- Confirm code, delete by user

---

# 📘 API Documentation

- **Swagger UI**: `http://localhost:8080/swagger-ui/`
- **HTML Docs**: `http://localhost:8080/` or `https://api.onlineshop.name/`

---

# ⚙ **Installation**

### Clone repository
```bash
git clone https://github.com/YuriyDolgikh/onlineshop.git
cd onlineshop

```

---

# ▶ **Local Run**
1. Install Java 17 & Maven  
2. Configure PostgreSQL in `application.properties`  
3. Run from IDE or:

```bash
mvn spring-boot:run
```

---

# ▶ **Run with Maven**
```bash
mvn clean install
mvn spring-boot:run
```

---

# 🐳 **Run with Docker**
```bash
docker-compose up --build
```

---

# 🔧 **Configuration**
File:
```
src/main/resources/application.properties
```

Example:
```properties
server.port=8080

spring.datasource.url=jdbc:postgresql://localhost:5432/onlineshop
spring.datasource.username=your_user
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
```

---

# 🧪 **Testing**
Run all tests:
```bash
mvn test
```

---

# ☁ **Deployment**
Previously deployed to Heroku:
```
https://api.onlineshop.name
```

Suitable for:
- VPS (Docker)
- Railway / Render / Fly.io
- Kubernetes

---

# 📂 **Project Structure**

```
onlineshop/
 ├── src/
 │   ├── main/
 │   │   ├── java/
 │   │   ├── resources/
 │   │   │   ├── application.properties
 │   │   │   ├── static/ (HTML documentation)
 │   │   │   └── templates/
 │   ├── test/
 ├── docker-compose.yml
 ├── Dockerfile
 ├── pom.xml
 └── README.md
```

---

# 🎉 Summary

A complete **full-stack-ready REST backend** for an e-commerce system with:

- Clean architecture
- Strong security
- Extensible design
- Fully documented API
- DTO & Entity clarity
- Service overview  
