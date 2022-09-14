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

### JIRA Cloud

TODO

### JIRA Enterprise

For setting up the JIRA application, the steps described here should be sufficient:
<https://developer.atlassian.com/server/jira/platform/oauth/>

It doesn't matter what you enter in remaining fields (URL, name, type, and so on).
This is because we only want to retrieve data from Jira, therefore we only need to set up a
one-way (incoming) link from the client to Jira.

Afterwards, you will see the client secret and ID, which needs to be provided for Diveni on
JIRA-Server.
