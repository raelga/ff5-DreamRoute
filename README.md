# 🛩 DreamRoute Backend API️

Welcome to the backend API for Happy Travel, a web app designed to let users share and explore their dream travel destinations. This API permits the management of users and their desired travel locations and connect with an existing React and Next frontend.

## 🚀 Features (MVP)

#### User Management & Authentication:

- User Registration & Login: Secure sign-up and authentication for new and existing users.

- Secure Password Storage: Passwords are encrypted using BCrypt before storage.

- JWT-based Authentication: Login generates a JSON Web Token (JWT) for subsequent API request authentication.

- Protected Routes: All sensitive API endpoints are secured using JWT Bearer Tokens.

- User Profile Retrieval: Authenticated users can securely retrieve their personal information.

- Administrator User Control: Allows admins to list, modify data/roles, and delete any registered user.

#### Destination Management:

- Destination Creation: Authenticated users can create new destinations, automatically associated with their account.

- Public Destination Listing & Filtering: All users (authenticated or not) can view the complete list of destinations and filter them by location or title.

- Personal Destination Control: Authenticated users can view, edit, and delete only the destinations they have created.

- Administrator Destination Control: Administrators have full management capabilities over all destinations in the system.

#### General:

- Input Validation: Robust input validation implemented across all endpoints to ensure data integrity.

- CORS Configuration: Configured for seamless integration with various frontend applications, allowing for local development.

## 🖥Technologies
### Backend
- Java 21
- Spring Boot
- Spring Security
- Maven
- Jakarta Validation
- JWT
- Lombok
- Mockito
- MySQL
- Data initialization (via data.sql)
- Postman for checking endpoints

### Frontend (provided seperately by customer Happy Travel but some extra features were added)
- HTML, CSS, JS
- React.js
- Next,js

## 🛠️Tools
- IntelliJ IDEA
- Postman
- Cloudinary

## 🗃️ Architecture
The backend is built with a 3-layer architecture, promoting separation of concerns and maintainability:
- Controllers (Presentation Layer): Handle incoming HTTP requests, route them to the appropriate services, and return HTTP responses. They utilize DTOs (Data Transfer Objects) for data exchange.
- Services (Business Logic Layer): Contain the core business logic, orchestrating operations and applying validation rules.
- Repositories (Data Access Layer): Interact directly with the database, performing CRUD operations via Spring Data JPA.

## 🛞 Setup & Running

### Backend
### 1. 🐬 Install MySQL
Make sure you have MySQL running locally and create a database for the project:
```sql
CREATE DATABASE dreamroute;
```

### 2. 📦 Clone the Repository
```shell
git clone https://github.com/Femcoders-Travellers/DreamRoute.git
```

### 3. 🔑 Configure Environment Variables
Create a .env file in the project root with your database credentials:
```shell
DB_URL=jdbc:mysql://localhost:3306/dreamroute
DB_USER=your_mysql_user
DB_PASSWORD=your_mysql_password
```
**Note:
The backend uses java-dotenv to load these variables.**

### 4. 🧱 Build the Project
```shell
mvn clean install
```
### 5. 🚀 Run the Application
```shell
mvn spring-boot:run
```
### Frontend
### 1. 📦 Clone the Repository
```shell
https://github.com/Femcoders-Travellers/happy-travel-front.git
```
### 2. 🚀 Install and run the Frontend Application
```shell
npm install
```

```shell
npm run dev
```

## ➡️ Endpoints

### DESTINATIONS
- POST `/destinations/{destinatuibId}` → To update a destination by DestinationID
- GET `/destinations` → To list all destinations
- GET `/destinations/{destinationId}` → To show a destination by ID 
```json
 {
        "id": 1,
        "country": "Colombia",
        "city": "Santa Marta",
        "description": "La más hermosa y maravillosa ciudad del mundo, aunque calurosa llena de playas refrescantes",
        "image": "https://res.cloudinary.com/dwc2jpfbw/image/upload/v1752583230/santa-marta-img_vdhss8.jpg",
        "username": "May"
    }
 ```
- GET `/destinations/{userId}` → To show a list of destinations by User ID
- PUT `/destinations/{destinationID}` → To update a destination
- DEL `/destinations/{destinationID}` → To delete a destination by ID 

### USERS
- POST `/users/create` → To add a new user
- GET `/users/all` → To list all users
- GET `/users/id/{userId}` → To show a user by ID
- GET `/users/{username}` → To show a user by username
- PUT `/users/update/{userID}` → To update a user by ID
- DEL `/users/delete/{userID}` → To delete a user by ID

### ROLES
- GET `/roles` → To list all roles
- GET `/roles/{roleId}` → To show a role by ID

### SECURITY
- POST `/login` → To login
```json
 {
  "username": "May",
  "password": "May12345."
}
 ```
- POST  `/register` → To register
```json
 {
  "username":"Viole",
  "email": "violeta@gmail.com",
  "password": "Viole12345."
}
 ```

## 🧪 Testing
The project includes unit and integration tests.
- Unit Tests: Focus on individual components (e.g., service methods in isolation).
- Integration Tests: Use MockMvc to simulate HTTP requests and verify controller behavior, including security aspects. Database entries are managed using test-data.sql scripts.

To run all tests:
```Bash
./mvnw test
```

#### ---------------------------


## 🏖 ️By:

#### Débora Rubio (https://github.com/debsrdev)

#### Mayleth Carrascal (https://github.com/may-leth)

#### Nia Espinal (https://github.com/niaofnarnia)

#### Mary Kenny (https://github.com/marykenny123)
 
