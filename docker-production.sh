#!/bin/sh
docker save -o docker-deploy/backend.tar aume2021-team-2_backend
docker save -o docker-deploy/frontend.tar aume2021-team-2_frontend
docker save -o docker-deploy/database.tar aume2021-team-2_database
docker save -o docker-deploy/proxy.tar aume2021-team-2_proxy

tar -czf aume2021-team-2.tar.gz -C docker-deploy .

