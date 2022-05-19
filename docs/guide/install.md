# Installation Guide

*Guide to install Diveni on production environments.*

## System Requirements

TBD

## Docker Infrastructure

This is currently the preferred environment to install Diveni.

### Docker Compose 

```yaml
version: "3.8"

services:
  database:
    build: ./database
    image: diveni_database
    container_name: diveni_database
    ports:
      - 27017:27017
    restart: unless-stopped
  backend:
    depends_on:
      - database
    build: ./backend
    image: diveni_backend
    container_name: diveni_backend
    env_file:
      - ./backend/.env
    ports:
      - 9090:9090
    restart: unless-stopped
  frontend:
    depends_on:
      - backend
    image:  diveni_frontend
    build: ./frontend
    container_name: diveni_frontend
    ports:
      - 8080:8080
    restart: unless-stopped
  proxy:
    depends_on:
        - frontend
        - backend
        - database
    image:  diveni_proxy
    build: ./proxy
    container_name: diveni_proxy
    restart: unless-stopped
    ports:
        - 80:80

```

## Connectors

TBD