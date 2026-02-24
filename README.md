# Loan Management System (LMS) - Microservices Backend

## Services

| Service        | Port | Database         | Description                     |
|----------------|------|------------------|---------------------------------|
| API Gateway    | 8080 | —                | JWT validation, routing         |
| Auth Service   | 8081 | lms_auth_db      | User management, JWT issuance   |
| Customer Svc   | 8082 | lms_customer_db  | Customer accounts               |
| Loan Service   | 8083 | lms_loan_db      | Loan packages, loan management  |
| Payment Svc    | 8084 | lms_payment_db   | Payment collection, receipts    |
| Route Service  | 8085 | lms_route_db     | Route management                |

## Setup

### 1. Configure Database Credentials

Update `spring.datasource.password` in each service's `application.properties`.

### 2. Build & Run Each Service

```bash
# API Gateway
cd api-gateway && mvn spring-boot:run

# Auth Service
cd auth-service && mvn spring-boot:run

# Route Service
cd route-service && mvn spring-boot:run

# Customer Service
cd customer-service && mvn spring-boot:run

# Loan Service
cd loan-service && mvn spring-boot:run

# Payment Service
cd payment-service && mvn spring-boot:run
```

**Start order recommended:** API Gateway → Auth Service → Route Service → Customer Service → Loan Service → Payment Service

---

## API Reference

All requests go through: `http://localhost:8080`

Protected endpoints require:  
`Authorization: Bearer <JWT_TOKEN>`

---

### Authentication Service

| Method | Endpoint                         | Description             | Auth Required |
|--------|----------------------------------|-------------------------|---------------|
| POST | `/api/auth/register`             | Register new employee   | No |
| POST | `/api/auth/login`                | Login, get JWT          | No |
| GET | `/api/users`                     | List all users          | Yes |
| GET | `/api/users/{id}`                | Get user by ID          | Yes |
| GET | `/api/users/nic/{nic}`           | Get user by NIC         | Yes |
| GET | `/api/users/username/{username}` | Get user by username    | Yes |
| GET | `/api/users/username?search=`    | Search user by username | Yes |
| GET | `/api/users/nic?search=`         | Search user by nic      | Yes |
| PUT | `/api/users/{id}`                | Update user permissions | Yes |
| DELETE | `/api/users/{id}`                | Delete user             | Yes |
| POST | `/api/auth/change-password`      | Change password         | Yes |

**Register Request:**
```json
{
  "nic": "199012345678",
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass1!",
  "role": "ROUTE_OFFICER",
  "permissions": ["COLLECT_PAYMENT", "VIEW_CUSTOMER", "VIEW_LOAN"]
}
```

**Login Request:**
```json
{
  "username": "john_doe",
  "password": "SecurePass1!"
}
```

**Change Password Request:**
```json
{
  "currentPassword": "OldPass1!",
  "newPassword": "NewPass1!",
  "confirmNewPassword": "NewPass1!"
}
```

---
### All other services need auth 
### Route Service (`/api/routes`)

| Method | Endpoint                   | Description          |
|--------|----------------------------|----------------------|
| POST | `/api/routes`              | Create route         |
| GET | `/api/routes`              | List all routes      |
| GET | `/api/routes/{routeCode}`  | Get route by code    |
| GET | `/api/routes/code?search=` | Search route by code |
| GET | `/api/routes/name?search=` | Search route by name |
| PUT | `/api/routes/{routeCode}`  | Update route         |
| DELETE | `/api/routes/{routeCode}`  | Delete route         |

**Create Route Request:**
```json
{
  "routeCode": "R001",
  "routeName": "Colombo Central",
  "routeDescription": "Main city area covering Fort and Pettah"
}
```

---

### Customer Service (`/api/customers`)

| Method | Endpoint                              | Description        |
|--------|---------------------------------------|--------------------|
| POST | `/api/customers`                      | Create customer    |
| GET | `/api/customers`                      | List all customers |
| GET | `/api/customers/nic?search=`      | Search by NIC      |
| GET | `/api/customers/username?search=john` | Search by name     |
| GET | `/api/customers/route?search=R001`    | search by route    |
| GET | `/api/customers/status?search=ACTIVE` | search by status   |
| GET | `/api/customers/{id}`                 | Get by ID          |
| GET | `/api/customers/nic/{nic}`            | Get by NIC         |
| PUT | `/api/customers/{id}`                 | Update customer    |
| DELETE | `/api/customers/{id}`                 | Delete customer    |

**Create Customer Request:**
```json
{
  "nic": "199512345678",
  "customerName": "Kamal Perera",
  "phoneNumber": "0771234567",
  "address": "123 Main Street, Colombo",
  "routeCode": "R001",
  "email": "kamal@example.com",
  "gender": "Male",
  "secondaryPhoneNumber": "0112345678"
}
```

---

### Loan Service (`/api/loans`, `/api/loan-packages`)

**Loan Packages:**

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/loan-packages` | Create package |
| GET | `/api/loan-packages` | List packages |
| GET | `/api/loan-packages/{packageCode}` | Get package |
| PUT | `/api/loan-packages/{packageCode}` | Update package |
| DELETE | `/api/loan-packages/{packageCode}` | Delete package |

**Create Loan Package:**
```json
{
  "packageCode": "LP001",
  "packageName": "Short-Term Micro Loan",
  "timePeriod": 30,
  "interest": 10.00
}
```

**Loans:**

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/loans` | Issue loan |
| GET | `/api/loans` | List loans |
| GET | `/api/loans?status=OPEN` | Filter by status |
| GET | `/api/loans?routeCode=R001` | Filter by route |
| GET | `/api/loans?nic=199512345678` | Filter by customer |
| GET | `/api/loans/{loanNumber}` | Get loan details |
| PUT | `/api/loans/{id}/state` | Update loan status |
| POST | `/api/loans/close` | Close loan & create sub-loan |

**Issue Loan Request:**
```json
{
  "customerNic": "199512345678",
  "startDate": "2026-02-20",
  "packageCode": "LP001",
  "amount": 50000.00
}
```

**Loan Statuses:** `OPEN`, `ARREARS`, `COMPLETED`, `CLOSED`

**Close & Sub-Loan Request:**
```json
{
  "loanNumber": "LN1234567890",
  "createSubLoan": true,
  "subLoanAmount": 15000.00,
  "subLoanStartDate": "2026-02-20",
  "subLoanPackageCode": "LP001"
}
```

---

### Payment Service (`/api/payments`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/payments/collect` | Collect rental payment |
| GET | `/api/payments/loan/{loanNumber}` | Payment history for loan |
| GET | `/api/payments/customer/{nic}` | All payments by customer |
| GET | `/api/payments/route/{routeCode}/collections?date=2026-02-20` | Route collection summary |

**Collect Payment Request:**
```json
{
  "customerNic": "199512345678",
  "loanNumber": "LN1234567890",
  "paidAmount": 5000.00,
  "remark": "Regular monthly payment"
}
```

---

## Permissions Reference

| Permission | Description |
|------------|-------------|
| `CREATE_CUSTOMER` | Register new customers |
| `VIEW_CUSTOMER` | View customer profiles |
| `UPDATE_CUSTOMER` | Modify customer data |
| `DELETE_CUSTOMER` | Remove customer accounts |
| `ISSUE_LOAN` | Approve and issue loans |
| `VIEW_LOAN` | View loan details |
| `REMOVE_LOAN` | Delete loan records |
| `UPDATE_LOAN_STATE` | Change loan status |
| `COLLECT_PAYMENT` | Record rental payments |
| `VIEW_PAYMENT` | View payment history |
| `MANAGE_ROUTE` | Create/update/delete routes |
| `MANAGE_LOAN_PACKAGE` | Manage loan packages |
| `USER_MANAGEMENT` | Manage system users |

---

## Security

- JWT tokens expire in 24 hours (configurable via `jwt.expiration`)
- API Gateway validates all JWT tokens before routing
- User roles: `ADMIN`, `ROUTE_OFFICER`
- Fine-grained permissions control specific actions
- User info forwarded downstream as HTTP headers: `X-User-Name`, `X-User-Role`, `X-User-Permissions`
