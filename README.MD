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

### User Profile Management
- `GET /api/users/profile` – View current user's profile.
- `PUT /api/users/profile` – Update user profile (name, email, username).
- `PUT /api/users/password` – Change password (requires current password).
- `DELETE /api/users/profile` – Deactivate user account.

### Book Management
#### Public Endpoints
- `GET /api/books` – List all books with pagination and filtering based on provided optional query params.
- `GET /api/books/{id}` – View details of a specific book.

#### Admin Endpoints
- `POST /api/admin/books` – Add a new book (details fetched from the Google Books API).
- `PUT /api/admin/books/{id}` – Update book details.
- `DELETE /api/admin/books/{id}` – Remove a book.
- `PATCH /api/admin/books/{id}/copies` – Update the number of copies available.

### Rental Management
#### User Endpoints
- `GET /api/rentals` – List current and past rentals of the user.
- `GET /api/rentals/{id}` – View details of a specific rental.
- `POST /api/rentals` – Create a new rental (supports multiple books).
- `PATCH /api/rentals/{id}/return` – Return rented book(s).

#### Admin Endpoints
- `GET /api/admin/rentals` – View all rentals with filtering options.
- `DELETE /api/admin/rentals/{id}` – Delete a completed rental record.

### Admin User Management
- `GET /api/admin/users` – View all users.
- `GET /api/admin/users/{id}` – View details of a specific user.
- `DELETE /api/admin/users/{id}` – Remove a user.
- `PATCH /api/admin/users/{id}/role` – Update user role (promote/demote).

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