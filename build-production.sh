#!/bin/bash
cd frontend
npm run build
sed -i -e 's#<title>frontend</title>#<title>frontend</title><script src="https://unpkg.com/@egjs/flicking@4.3.1-beta.4/dist/flicking.pkgd.min.js"></script><link rel="stylesheet" href="https://unpkg.com/@egjs/flicking@4.3.1/dist/flicking.css">#g' ./dist/index.html
cd ../backend
mvn package -DskipTests -Dspring.profiles.active=prod
cd ..
docker compose build
docker compose up -d

