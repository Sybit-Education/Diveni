#!/bin/bash
cd frontend
npm run build
cd ../backend
./../mvnw package -DskipTests -D"spring.profiles.active=prod"
cd ..
docker compose build
docker compose up -d

