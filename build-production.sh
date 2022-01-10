#!/bin/bash
cd frontend
npm run build
sed -i -e 's#<title>frontend</title>#<title>frontend</title><script src="https://unpkg.com/@egjs/flicking@4.0.0-beta.4/dist/flicking.pkgd.min.js"></script><link rel="stylesheet" href="https://unpkg.com/@egjs/flicking@4.0.0/dist/flicking.css">#g' ./dist/index.html
cd ../backend
./../mvnw package -DskipTests -D"spring.profiles.active=prod"
cd ..
docker compose build
docker compose up -d

