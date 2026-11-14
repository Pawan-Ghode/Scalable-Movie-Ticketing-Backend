# Movie Ticket Booking Backend



A RESTful Movie Ticket Booking Backend built using Java 17, Spring Boot, Spring Data JPA, PostgreSQL and Spring Security with JWT authentication.



The application supports movie management, user registration and login, secure seat booking, database-backed persistence, request validation and protection against double booking.



## Features



### Movie Management

- Create, retrieve, update and delete movies.

- Search movies by title.

- Filter movies by maximum price.

- Validate incoming requests using Jakarta Bean Validation.

- Use DTOs to separate API request/response models from entities.



### User Authentication

- User registration and login.

- Password hashing using BCrypt.

- JWT-based stateless authentication.

- Public authentication endpoints.

- Protected booking and movie modification endpoints.



### Movie Booking

- Create bookings for a particular movie show.

- Validate show and seat existence.

- Validate that selected seats belong to the show's screen.

- Reject duplicate seat IDs in a request.

- Calculate booking amount using show price and number of seats.

- Prevent double booking using pessimistic database locking and a database-level unique constraint.

- Maintain booking and booking-seat records transactionally.



### API Documentation

- OpenAPI documentation.

- Swagger UI for exploring and testing endpoints.



## Tech Stack



| Technology | Purpose |

|---|---|

| Java 17 | Programming language |

| Spring Boot | Application framework |

| Spring Web | REST APIs |

| Spring Data JPA | Database persistence |

| Hibernate | ORM |

| PostgreSQL | Relational database |

| Spring Security | Authentication and authorization |

| JWT (JJWT) | Token-based authentication |

| BCrypt | Password hashing |

| Jakarta Validation | Request validation |

| Swagger / OpenAPI | API documentation |

| Maven | Build and dependency management |

| JUnit 5 | Automated testing |

| Spring Security Test | Security testing support |



## Architecture



The application follows a layered architecture:



```text

Client (Postman / Browser)

&#x20;         |

&#x20;         v

&#x20;   REST Controllers

&#x20;         |

&#x20;         v

&#x20;      Services

&#x20;         |

&#x20;         v

&#x20;    Repositories

&#x20;         |

&#x20;         v

&#x20;     PostgreSQL

```



### Main components



- \*\*Controller layer:\*\* Handles HTTP requests and responses.

- \*\*Service layer:\*\* Contains business logic and transaction management.

- \*\*Repository layer:\*\* Handles database operations through Spring Data JPA.

- \*\*Entity layer:\*\* Represents database tables.

- \*\*DTO layer:\*\* Defines request and response payloads.

- \*\*Security layer:\*\* Handles JWT authentication and request authorization.



## Database Design



The application uses PostgreSQL with the following main entities:



| Table | Purpose |

|---|---|

| movies | Stores movie information |

| theaters | Stores theater information |

| screens | Represents screens within theaters |

| movie\_shows | Stores movie show details |

| seats | Stores seats associated with screens |

| app\_users | Stores registered users and encoded passwords |

| bookings | Stores booking information |

| booking\_seats | Maps bookings to selected seats and shows |



### Important relationships



- A theater can have multiple screens.

- A screen can contain multiple seats.

- A movie can have multiple shows.

- A show is associated with a movie and a screen.

- A booking belongs to a show.

- Booking-seat records associate selected seats with a booking.



The `booking\_seats` table has a unique constraint on `(show\_id, seat\_id)` to prevent the same seat from being booked more than once for a particular show.



## API Endpoints



Base URL:



```text

http://localhost:8080

```



### 1. Health Check



| Method | Endpoint | Access |

|---|---|---|

| GET | `/hello` | Public |



### 2. Movie APIs



| Method | Endpoint | Description | Access |

|---|---|---|---|

| GET | `/movies` | Get all movies | Public |

| GET | `/movies/{id}` | Get movie by ID | Public |

| POST | `/movies` | Create a movie | Authenticated |

| PUT | `/movies/{id}` | Update movie details | Authenticated |

| PATCH | `/movies/{id}` | Update movie price | Authenticated |

| DELETE | `/movies/{id}` | Delete a movie | Authenticated |

| GET | `/movies/search?title=Inception` | Search by title | Public |

| GET | `/movies/filter?maxPrice=300` | Filter by maximum price | Public |



### 3. Authentication APIs



| Method | Endpoint | Description | Access |

|---|---|---|---|

| POST | `/api/auth/register` | Register a user | Public |

| POST | `/api/auth/login` | Authenticate and receive JWT | Public |



\*\*Registration request:\*\*



```json

{

&#x20; "email": "user@example.com",

&#x20; "password": "password123"

}

```



Password must contain at least 8 characters.



\*\*Login request:\*\*



```json

{

&#x20; "email": "user@example.com",

&#x20; "password": "password123"

}

```



The login endpoint returns an authentication response containing a JWT and its token type.



Use the returned token in protected requests:



```http

Authorization: Bearer <your-jwt-token>

```



### 4. Booking API



| Method | Endpoint | Description | Access |

|---|---|---|---|

| POST | `/api/bookings` | Create a seat booking | Authenticated |



\*\*Booking request:\*\*



```json

{

&#x20; "showId": 2,

&#x20; "seatIds": \[1, 2]

}

```



\*\*Possible responses:\*\*



| HTTP status | Meaning |

|---|---|

| 201 | Booking created |

| 400 | Invalid request, duplicate seat IDs or seats from another screen |

| 401 | Missing or invalid authentication |

| 404 | Show not found |

| 409 | One or more seats already booked |



## Booking Concurrency and Double-Booking Prevention



Preventing double booking is one of the important business requirements of this project.



The booking flow uses:



1\. `@Transactional` to execute the booking operation within a database transaction.

2\. Pessimistic write locking using `PESSIMISTIC\_WRITE` on the selected show.

3\. Validation to reject seats that are already booked.

4\. A unique database constraint on `(show\_id, seat\_id)` as an additional integrity safeguard.



### Booking flow



```text

Booking Request

&#x20;     |

&#x20;     v

Validate Show

&#x20;     |

&#x20;     v

Lock Show Row

&#x20;     |

&#x20;     v

Validate Seat IDs

&#x20;     |

&#x20;     v

Check Screen Ownership

&#x20;     |

&#x20;     v

Check Existing Bookings

&#x20;     |

&#x20;     v

Calculate Total Amount

&#x20;     |

&#x20;     v

Save Booking

&#x20;     |

&#x20;     v

Save Booking Seats

&#x20;     |

&#x20;     v

Return Booking Response

```



The show-level lock serializes booking operations for the same show. The unique constraint provides an additional database-level safeguard against duplicate seat assignments.



## Security Implementation



The application uses Spring Security with stateless JWT authentication.



- Passwords are stored using BCrypt hashing.

- Users authenticate through the login endpoint.

- A JWT is generated after successful authentication.

- A custom JWT filter extracts and validates bearer tokens.

- The authenticated user is placed in the Spring Security context.

- Protected endpoints reject unauthenticated requests with HTTP 401.

- HTTP sessions and form login are disabled.



Swagger documentation, authentication endpoints and movie GET endpoints are publicly accessible. Other endpoints require authentication.



## Getting Started



### Prerequisites



- Java 17

- PostgreSQL

- Maven (or use the included Maven wrapper)

- Git



### 1. Clone the repository



```bash

git clone https://github.com/Pawan-Ghode/movie-booking.git

cd movie-booking

```



### 2. Create the PostgreSQL database



```sql

CREATE DATABASE movie\_booking;

```



### 3. Configure environment variables



The application expects these environment variables:



- `DB\_PASSWORD` â€” PostgreSQL password.

- `JWT\_SECRET` â€” Base64-encoded JWT signing secret suitable for HS256.



Do not commit actual passwords or production JWT secrets to Git.



\*\*PowerShell example:\*\*



```powershell

$env:DB\_PASSWORD = "your-local-db-password"

$env:JWT\_SECRET = "your-base64-encoded-jwt-secret"

```



### 4. Configure application properties



In `src/main/resources/application.properties`:



```properties

spring.application.name=movie-booking



spring.datasource.url=jdbc:postgresql://localhost:5432/movie\_booking

spring.datasource.username=postgres

spring.datasource.password=${DB\_PASSWORD}



spring.jpa.hibernate.ddl-auto=update

spring.jpa.show-sql=true

spring.jpa.properties.hibernate.format\_sql=true



app.jwt.secret=${JWT\_SECRET}

app.jwt.expiration-ms=3600000

```



### 5. Run the application



On Windows PowerShell:



```powershell

.\\mvnw.cmd spring-boot:run

```



The application starts at:



```text

http://localhost:8080

```



### 6. Open Swagger UI



```text

http://localhost:8080/swagger-ui/index.html

```



Use Swagger or Postman to explore and test the APIs.



## Running Tests



The project contains automated unit and integration tests.



Test configuration is stored in:



```text

src/test/resources/application-test.properties

```



The test profile uses a separate PostgreSQL database:



```text

movie\_booking\_test

```



Create it before running the tests:



```sql

CREATE DATABASE movie\_booking\_test;

```



Set the database password in PowerShell:



```powershell

$env:DB\_PASSWORD = "your-local-db-password"

```



Run the test suite:



```powershell

.\\mvnw.cmd clean test

```



The current test suite contains \*\*17 tests\*\*, covering booking validation, successful bookings, invalid requests, missing resources, duplicate seats, booking conflicts and application context loading.



Latest verified result:



```text

Tests run: 17

Failures: 0

Errors: 0

BUILD SUCCESS

```



## Project Structure



```text

src/

â”œâ”€â”€ main/

â”‚   â”œâ”€â”€ java/com/pawan/moviebooking/

â”‚   â”‚   â”œâ”€â”€ Controllers

â”‚   â”‚   â”œâ”€â”€ Services

â”‚   â”‚   â”œâ”€â”€ Repositories

â”‚   â”‚   â”œâ”€â”€ Entities

â”‚   â”‚   â”œâ”€â”€ DTOs

â”‚   â”‚   â”œâ”€â”€ Security

â”‚   â”‚   â””â”€â”€ Configuration

â”‚   â”‚

â”‚   â””â”€â”€ resources/

â”‚       â””â”€â”€ application.properties

â”‚

â””â”€â”€ test/

&#x20;   â”œâ”€â”€ java/com/pawan/moviebooking/

&#x20;   â”‚   â”œâ”€â”€ BookingIntegrationTest.java

&#x20;   â”‚   â”œâ”€â”€ BookingServiceTest.java

&#x20;   â”‚   â””â”€â”€ MovieBookingApplicationTests.java

&#x20;   â”‚

&#x20;   â””â”€â”€ resources/

&#x20;       â””â”€â”€ application-test.properties

```



## Current Limitations and Future Enhancements



The following capabilities are not implemented in the current version and are possible future enhancements:



- Redis caching for frequently accessed movie and show data.

- Apache Kafka for asynchronous booking events and notifications.

- Docker containerization.

- Microservices architecture.

- AWS deployment.

- Payment gateway integration.

- Booking cancellation and refund workflows.

- Refresh token and token revocation support.

- Fine-grained role-based authorization.



## Learning Outcomes



This project demonstrates practical implementation of:



- REST API design and layered architecture.

- Spring Boot and Spring Data JPA.

- Relational database modelling.

- DTOs and request validation.

- Transaction management.

- Pessimistic locking and database constraints.

- JWT-based stateless authentication.

- Unit and integration testing.

- API documentation using Swagger.



---



\*\*Project:\*\* Movie Ticket Booking Backend

\*\*Language:\*\* Java 17

\*\*Framework:\*\* Spring Boot

\*\*Database:\*\* PostgreSQL
