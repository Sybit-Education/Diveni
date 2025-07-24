cd Diveni

echo Pulling Docker images ...
docker compose pull

echo Starting Diveni ...
docker compose up -d
