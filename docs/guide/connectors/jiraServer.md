### JIRA Enterprise Connector

For setting up the JIRA application, the steps described here should be sufficient:
<https://developer.atlassian.com/server/jira/platform/oauth/>

It doesn't matter what you enter in remaining fields (URL, name, type, and so on).
This is because we only want to retrieve data from Jira, therefore we only need to set up a
one-way (incoming) link from the client to Jira.

Afterwards, you will see the `client secret` and `ID`, which needs to be provided for Diveni on
JIRA-Server.

## Configuration
#### Available configuration
| Parameter | Description | Default value |
|---|---|:---:|
| SERVER_URL | URL the server is running on, used for CORS-settings | null |
| LOCALE | The locale the frontend should be set to, available locales=en,de,es,fr,it,pl,pt,uk | en |
| JIRA_SERVER_JIRAHOME | URL to the Jira Server instance | null |
| JIRA_SERVER_CONSUMERKEY | Consumer key can be set in the Jira application link | OauthKey |
| JIRA_SERVER_PRIVATEKEY | Private key from jira_privatekey.pcks8 | null |
| JIRA_SERVER_ESTIMATIONFIELD | The estimation field is a customfield which is different for every Jira instance and must therefore be set manually | customfield_10111 |
| JIRA_SERVER_RANKNAME | Used for ordering of the issues. Depends on the Jira language and is either RANK or RANG | RANK |

## Add configuration to the Diveni instance
::: details Diveni with configuration via docker compose
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
      - "JIRA_SERVER_JIRAHOME=https://jira.company.com"
      - "JIRA_SERVER_CONSUMERKEY=OauthKey"
      - "JIRA_SERVER_PRIVATEKEY=[xxx]"
      - "JIRA_SERVER_ESTIMATIONFIELD=customfield_10152"
      - "JIRA_SERVER_RANKNAME=RANG"
[...]
```
:::

::: details Diveni with configuration via .env
To configure your local environment, you have to add your configuration to `/backend/.env`

See below for example:
```properties 
#URL the server is running on
SERVER_URL=http://localhost:8080

#The locale the frontend should be set to
LOCALE=en

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
:::


## Starting App

If all the configuration is done, Diveni could be started:

```shell
docker-compose up -d
```
