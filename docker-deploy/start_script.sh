#!/bin/sh
# Load images
for f in *.tar; do
	cat $f | docker load
done
# Start containers
docker compose up &

