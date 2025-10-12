#!/bin/bash

# Wait for MySQL to be ready
until nc -z db 3306; do
  echo "Waiting for MySQL..."
  sleep 2
done

# Start Spring Boot
echo "Starting Spring Boot app..."
exec java -jar app.jar