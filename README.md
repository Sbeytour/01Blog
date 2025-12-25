# 01Blog

A full-stack blogging platform with user authentication, content management, and administrative moderation tools.

## Technologies Used

### Frontend
- Angular 20.3.0
- TypeScript 5.9.2
- Angular Material 20.2.5
- RxJS 7.8.1
- SCSS

### Backend
- Spring Boot 3.5.5
- Java 17
- Spring Security
- Spring Data JPA
- JWT (JJWT 0.11.5)
- PostgreSQL 15
- Maven

### DevOps
- Docker & Docker Compose
- pgAdmin 4

## How to Run

### Prerequisites
- Java 17 or higher
- Node.js and npm
- Docker and Docker Compose

### Quick Start (Recommended)

Run the entire application with a single command:

```bash
./start.sh
```

This will automatically:
- Start PostgreSQL database
- Start the Spring Boot backend
- Install frontend dependencies (if needed)
- Start the Angular frontend

To stop the application:
```bash
./stop.sh
```

### Manual Setup

#### Backend Setup

1. Navigate to the backend directory:
```bash
cd backend
```

2. Start PostgreSQL using Docker Compose:
```bash
docker-compose up -d
```

3. Run the Spring Boot application:
```bash
./mvnw spring-boot:run
```

The backend API will start on `http://localhost:8080`

#### Frontend Setup

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

The frontend application will be available at `http://localhost:4200`

## Database Access

- **PostgreSQL:** localhost:5432
  - Database: `blog_db`
  - User: `bloguser`
  - Password: `blogpass`

- **pgAdmin:** http://localhost:5050
  - Email: `admin@postly.com`
  - Password: `admin123`

## Features

- User registration and authentication with JWT
- Create, edit, and delete blog posts with media uploads
- Comments and likes system
- User profiles and subscriptions
- Real-time notifications
- Search functionality
- Admin dashboard for moderation
- Content reporting and user banning
