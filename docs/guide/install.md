# Installation Guide

*Guide to install Diveni on production environments.*

### System Requirements

Using Docker is the preferred way to install Diveni.

Preferred Docker environment: 

* Docker host: Linux, Windows, Mac
* CPU: TBD
* Memory: TBD
* HDD space: TBD

## Preconditions

To install Diveni in combination with an issue tracker, some configuration is required to connect to the issue
tracker. The information needs to be added to the configuration file `backend/.env` (see below).

Connecting to an issue tracker is optional. Diveni can also be used without a connection.

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

In the end make sure to enable Jira Server by setting `VUE_APP_ENABLE_JIRA_SERVER` in `frontend/.env` to `true`.

### Azure DevOps Connector

For setting up the Azure DevOps Connector, you have to generate a personal access token:
<https://learn.microsoft.com/en-us/azure/devops/organizations/accounts/use-personal-access-tokens-to-authenticate?view=azure-devops&tabs=Windows#create-a-pat>

Afterwards you have to enter the used organisation in `AZURE_ORGANIZATION` and the personal access token in `AZURE_CLIENTPAT` in `backend/.env`.

In the end make sure to enable Azure DevOps by setting `VUE_APP_ENABLE_AZURE_DEVOPS` in `frontend/.env` to `true`.

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
version: "3.8"

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

### Configuration using `.env`

To configure your local environment, you have to add your configuration to `/backend/.env`

See below for example:

```properties 
#URL the server is running on
SERVER_URL=http://localhost:8080

#ClientId and ClientSecret are shown in the Atlassian Developer app settings
JIRA_CLOUD_CLIENTID=[xxx]
JIRA_CLOUD_CLIENTSECRET=[xxx]

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

### Starting App 

If all the configuration is done, Diveni could be started:

```shell
docker-compose up -d
```
