# Book Rental Management REST API

## Overview
This is a Spring Boot-based REST API designed for managing book rentals, user accounts, and book inventory. It supports user and admin roles with distinct capabilities and integrates with the Google Books API to fetch book details.

## Key Features
- **User Account Management**: Register, log in, view/update profile, change password, and deactivate account.
- **JWT-Based Authentication**: Secure stateless authentication using Spring Security.
- **Book Management**: Admins can add, update, and remove books, with details fetched from the Google Books API.
- **Rental Management**: Users can rent and return books. Admins can view and manage all rental records.
- **Custom Exception Handling**: Uses `@ControllerAdvice` for global exception handling.
- **Multiple Profiles**: Development (H2 database) and production (MySQL database) environments.
- **OpenAPI/Swagger Integration**: Automatically generated API documentation for ease of use.

## Endpoints

### Authentication
- `POST /api/auth/register` – Create a new user account.
- `POST /api/auth/login` – Log in with JWT-based authentication.

---

### User Profile Management
- `GET /api/user/profile` – View the current user's profile.
- `PATCH /api/user/profile` – Update user profile details (e.g., name, email, username).

---

### Book Management

#### Public Endpoints
- `GET /api/books` – List all books with pagination and optional filters.  
  **Example Request**:
   ```
   GET /api/books?title=harry&author=rowling&ratingGreaterThan=4&page=0&size=5&orderBy=title&dir=asc
   ```  
  **Response**:  
  A paginated list of books filtered by the title "harry", author "rowling", and a minimum rating of 4.

- `GET /api/books/{id}` – View details of a specific book.

#### Admin Endpoints
- `POST /api/admin/books` – Add new books by author name, title, or ISBN using Google Books API.  
  **Example Request**:
   ```json
   {
     "searchGroups": [
       { "author": "J.K. Rowling", "title": "Harry Potter" }
     ],
     "copies": 5
   }
   ```
  **Response**:  
  List of saved book IDs.

- `PATCH /api/admin/books/{id}` – Update details of a specific book.
- `DELETE /api/admin/books/{id}` – Remove a book.

---

### Rental Management

#### User Endpoints
- `GET /api/rentals` – View all rentals for the currently logged-in user with optional filters.  
  **Example Request**:
   ```
   GET /api/rentals?status=ACTIVE&dueDateBefore=2024-12-01&page=0&size=10
   ```  
  **Response**:  
  A paginated list of active rentals due before `2024-12-01`.

- `GET /api/rentals/{id}` – View details of a specific rental.
- `POST /api/rentals` – Create a new rental for multiple books with duration provided in days.  
  **Example Request**:
   ```json
   {
     "bookIds": [1, 2, 3],
     "rentalDuration": 60
   }
   ```

- `PATCH /api/rentals/{id}/return` – Return rented books.

#### Admin Endpoints
- `GET /api/admin/rentals` – View all rentals with filters like status, dates, and book title.  
  **Example Request**:
   ```
   GET /api/admin/rentals?status=COMPLETED&issueDateAfter=2024-01-01&page=0&size=5
   ```

---

### Admin User Management
- `GET /api/admin/users` – List all users with filtering options (e.g., name, role, rented book).  
  **Example Request**:
   ```
   GET /api/admin/users?role=USER&rentalsGreaterThan=3&page=0&size=10&orderBy=name&dir=desc
   ```
- `GET /api/admin/users/{id}` – View details of a specific user.
- `PATCH /api/admin/users/{id}` – Promote a user to admin.

---

## Request DTO Examples

### Book Filter (for GET `/api/books`)
```json
{
  "title": "harry",
  "author": "rowling",
  "isbn": "978-3-16-148410-0",
  "available": true,
  "categories": ["Fantasy"],
  "ratingGreaterThan": 4.5,
  "page": 0,
  "size": 10,
  "orderBy": "title",
  "dir": "asc"
}
```

### Rental Filter (for Admin `/api/admin/rentals` or User `/api/rentals`)
```json
{
  "status": "ACTIVE",
  "issueDateBefore": "2024-12-01",
  "dueDateAfter": "2024-01-01",
  "bookTitle": "Harry Potter",
  "page": 0,
  "size": 5,
  "orderBy": "dueDate",
  "dir": "desc"
}
```

### User Filter (for `/api/admin/users`)
```json
{
  "name": "John",
  "role": "USER",
  "rentalsGreaterThan": 2,
  "rentedAfter": "2024-01-01",
  "page": 0,
  "size": 10,
  "orderBy": "name",
  "dir": "asc"
}
```

## Planned Enhancements
- **JWT Refresh Tokens**: Implement refresh tokens for better session management.
- **Logout and OAuth2**: Add OAuth2.0 capabilities and support true logout.
- **Redis Caching**: Improve API performance using Redis.
- **Password Reset**: Integrate an email service for password reset functionality.
- **Testing**: Add comprehensive unit and integration tests.
- **Rental Fees and Penalties**: Include a feature for rental fees and late return penalties, integrated with a payment microservice.
- **Monitoring**: Add monitoring with Spring Boot Actuator.
- **ElasticSearch**: Add full text search capabilities with ElasticSearch

## Getting Started
### Prerequisites
- Java 21 or higher
- Maven 3.x
- H2 Database (dev) / MySQL Database (prod)

### Installation
1. Clone the repository.
2. Set up the database and update configuration files for `application-dev.properties` or `application-prod.properties`.
3. Build the project using Maven:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### API Documentation
Swagger UI is available at `/swagger-ui.html` when the application is running.