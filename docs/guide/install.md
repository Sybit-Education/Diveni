# Installation Guide

*Guide to install Diveni on production environments.*

### System Requirements

Using Docker is the preferred way to install Diveni.

Preferred Docker environment: 

* Docker host: Linux, Windows, Mac
* Arch: amd64, arm64
* CPU: TBD
* Memory: TBD
* HDD space: TBD

## Preconditions

To install Diveni in combination with JIRA some configuration data to connect the issue 
tracker are required. These data have to be added to configuration (see below).

Connection to JIRA Cloud and JIRA Enterprise is optional. Diveni could also be used without any
connection.

### JIRA Cloud Connector

    TBD

### JIRA Enterprise Connector

For setting up the JIRA application, the steps described here should be sufficient:
<https://developer.atlassian.com/server/jira/platform/oauth/>

It doesn't matter what you enter in remaining fields (URL, name, type, and so on).
This is because we only want to retrieve data from Jira, therefore we only need to set up a
one-way (incoming) link from the client to Jira.

Afterwards, you will see the `client secret` and `ID`, which needs to be provided for Diveni on
JIRA-Server.

## Docker

The infrastructure is divided in several Docker containers:

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

### Docker Compose 

```yaml
version: "3"

services:
  database:
    image: mongo:4
    container_name: diveni_database
    restart: unless-stopped

  backend:
    image: ghcr.io/sybit-education/diveni-backend:latest
    container_name: diveni_backend
    depends_on:
      - database
    restart: unless-stopped
    environment:
      - "SPRING_PROFILES_ACTIVE=prod" # required

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

### Configuration
#### Available configuration
| Parameter | Function | default value |
|---|---|---|
| SERVER_URL | URL the server is running on, used for CORS-settings | null |
| LOCALE | The locale the frontend should be set to, available locales=en,de,es,fr,it,pl,pt,uk | en |
| JIRA_CLOUD_CLIENTID | ClientId is shown in the Atlassion Developer app settings | null |
| JIRA_CLOUD_CLIENTSECRET | ClientSecret is shown in the Atlassion Developer app settings | null |
| JIRA_CLOUD_ESTIMATIONFIELD | The estimation field is a customfield which is different for every Jira instance and must therefore be set manually | customfield_10016 |
| JIRA_CLOUD_AUTHORIZE_URL | URL for the authorization server from Jira Cloud | null |
| JIRA_SERVER_JIRAHOME | URL to the Jira Server instance | null |
| JIRA_SERVER_CONSUMERKEY | Consumer key can be set in the Jira application link | OauthKey |
| JIRA_SERVER_PRIVATEKEY | Private key from jira_privatekey.pcks8 | null |
| JIRA_SERVER_ESTIMATIONFIELD | The estimation field is a customfield which is different for every Jira instance and must therefore be set manually | customfield_10111 |
| JIRA_SERVER_RANKNAME | Used for ordering of the issues. Depends on the Jira language and is either RANK or RANG | RANK |

**Important:**
* In order to use Jira Cloud all parameters with prefix `JIRA_CLOUD_` are required to be set.
* To use Jira Server all parameters with prefix `JIRA_SERVER_` have to be set.


#### Add configuration to the Diveni instance
<details>
<summary>Diveni with configuration via docker compose</summary>

Update Docker Compose to environment variables
```yaml
[...]
  backend:
    image: ghcr.io/sybit-education/diveni-backend:latest
    container_name: diveni_backend
    depends_on:
      - database
    restart: unless-stopped
    environment:
      - "SPRING_PROFILES_ACTIVE=prod" # required
      - "SERVER_URL=http://localhost:8080"
      - "LOCALE=en"
      - "JIRA_CLOUD_CLIENTID=[xxx]"
      - "JIRA_CLOUD_CLIENTSECRET=[xxx]"
      - "JIRA_CLOUD_ESTIMATIONFIELD=customfield_10016"
      - "JIRA_CLOUD_AUTHORIZE_URL=https://auth.atlassian.com"
      - "JIRA_SERVER_JIRAHOME=https://jira.company.com"
      - "JIRA_SERVER_CONSUMERKEY=OauthKey"
      - "JIRA_SERVER_PRIVATEKEY=[xxx]"
      - "JIRA_SERVER_ESTIMATIONFIELD=customfield_10152"
      - "JIRA_SERVER_RANKNAME=RANG"
[...]
```

</details>

<details>
<summary>Diveni with configuration via .env</summary>

To configure your local environment, you have to add your configuration to `/backend/.env`

See below for example:
```properties 
#URL the server is running on
SERVER_URL=http://localhost:8080

#The locale the frontend should be set to
LOCALE=en

#ClientId and ClientSecret are shown in the Atlassian Developer app settings
JIRA_CLOUD_CLIENTID=[xxx]
JIRA_CLOUD_CLIENTSECRET=[xxx]

#URL for the authorization server
JIRA_CLOUD_AUTHORIZE_URL=https://auth.atlassian.com/authorize?...

#The estimation field is a customfield which is different for every Jira instance and must therefore be set manually
JIRA_CLOUD_ESTIMATIONFIELD=customfield_10016

#URL to the Jira Server instance
JIRA_SERVER_JIRAHOME=https://jira.company.com

#Consumer key can be set in the Jira application link
JIRA_SERVER_CONSUMERKEY=OauthKey

#Private key from jira_privatekey.pcks8
JIRA_SERVER_PRIVATEKEY=[xxx]

#The estimation field is a customfield which is different for every Jira instance and must therefore be set manually
JIRA_SERVER_ESTIMATIONFIELD=customfield_10152

#Used for correct sortation of the issues. Depends on the Jira language e.g. RANK or RANG
JIRA_SERVER_RANKNAME=RANG
```
Copy the configured file to `/backend/.env`

Update Docker Compose to use .env

```yaml
[...]
  backend:
    image: ghcr.io/sybit-education/diveni-backend:latest
    container_name: diveni_backend
    depends_on:
      - database
    restart: unless-stopped
    environment:
      - "SPRING_PROFILES_ACTIVE=prod" # required
    env_file:
      - ./backend/.env
[...]
```

</details>


### Starting App 

If all the configuration is done, Diveni could be started:

```shell
docker-compose up -d
```
