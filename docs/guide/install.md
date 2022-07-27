# Installation Guide

*Guide to install Diveni on production environments.*

## Docker

Using Docker is the preferred way to install Diveni.

The infrastructure is divided in several containers:

```mermaid
flowchart TD
  subgraph Reverse-Proxy Container
    direction RL
      Reverse-Proxy(Nginx)
  end  
  subgraph Frontend Container
    direction RL
      Frontend(node.js)
  end
  subgraph Backend Container
    direction RL
      Backend(openjdk)
  end
  subgraph Database Container
    direction RL
      Database(MongoDB)
  end  

  Reverse-Proxy <--> Frontend
  Reverse-Proxy <--> Backend  
  Frontend <--> Backend
  Backend <--> Database
  
````

### System Requirements

TBD 

### Docker Compose 

```yaml
version: "3.8"

services:
  database:
    image: mongo:4.4.11-rc0-focal
    container_name: diveni_database
    restart: unless-stopped    

  backend:
    image: ghcr.io/sybit-education/diveni-backend:latest
    container_name: diveni_backend
    depends_on:
      - database
    restart: unless-stopped    
    env_file:
      - ./backend/.env

  frontend:
    image: ghcr.io/sybit-education/diveni-frontend:latest
    container_name: diveni_frontend
    depends_on:
      - backend
    restart: unless-stopped      

  proxy:
    image: ghcr.io/sybit-education/diveni_proxy:latest
    container_name: diveni_proxy
    depends_on:
      - frontend
      - backend
      - database    
    restart: unless-stopped
    ports:
      - "80:80"

```

## Connectors

TBD
