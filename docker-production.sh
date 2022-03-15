#!/bin/sh
docker save -o docker-deploy/backend.tar  diveni_backend
docker save -o docker-deploy/frontend.tar diveni_frontend
docker save -o docker-deploy/database.tar diveni_database
docker save -o docker-deploy/proxy.tar diveni_proxy

tar -czf diveni.tar.gz -C docker-deploy .

