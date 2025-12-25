#!/bin/bash

echo "Stopping 01Blog Application..."
echo "================================"

# Stop frontend (Angular)
echo "Stopping Angular frontend..."
pkill -f "ng serve"

# Stop backend (Spring Boot)
echo "Stopping Spring Boot backend..."
pkill -f "spring-boot:run"
pkill -f "BlogApplication"

# Stop PostgreSQL
echo "Stopping PostgreSQL..."
cd backend
docker-compose down
cd ..

echo ""
echo "Application stopped successfully!"
