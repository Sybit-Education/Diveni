#!/bin/bash
cd frontend
npm run build
cd ../backend
mvn package -DskipTests -Dspring.profiles.active=prod
cd ..
docker compose build
docker compose up -d

