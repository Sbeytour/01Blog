#!/bin/bash

echo "Starting 01Blog Application..."
echo "================================"

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker first."
    exit 1
fi

# Start PostgreSQL
echo "Starting PostgreSQL database..."
cd backend
docker-compose up -d
cd ..

# Wait for PostgreSQL to be ready
echo "Waiting for database to be ready..."
sleep 5

# Start Backend in background
echo "Starting Spring Boot backend..."
cd backend
./mvnw spring-boot:run &
BACKEND_PID=$!
cd ..

# Wait for backend to start
echo "Waiting for backend to start..."
sleep 10

# Start Frontend
echo "Starting Angular frontend..."
cd frontend

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo "Installing frontend dependencies..."
    npm install
fi

npm start &
FRONTEND_PID=$!
cd ..

echo ""
echo "================================"
echo "Application started successfully!"
echo "================================"
echo "Frontend: http://localhost:4200"
echo "Backend:  http://localhost:8080"
echo "pgAdmin:  http://localhost:5050"
echo ""
echo "Backend PID: $BACKEND_PID"
echo "Frontend PID: $FRONTEND_PID"
echo ""
echo "To stop the application, run: ./stop.sh"
echo "Or press Ctrl+C and manually stop the processes"
