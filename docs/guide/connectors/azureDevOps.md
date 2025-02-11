### Azure DevOps Connector

For setting up the Azure DevOps Connector, you have to generate a personal access token:
<https://learn.microsoft.com/en-us/azure/devops/organizations/accounts/use-personal-access-tokens-to-authenticate#create-a-pat>

Afterwards you have to enter the used organisation in `AZURE_ORGANIZATION` and the personal access token in `AZURE_CLIENTPAT` in `backend/.env`.

## Configuration
#### Available configuration
| Parameter | Description | Default value |
|---|---|:---:|
| SERVER_URL | URL the server is running on, used for CORS-settings | null |
| LOCALE | The locale the frontend should be set to, available locales=en,de,es,fr,it,pl,pt,uk | en |
| AZURE_ORGANIZATION | The name of the organization in Azure | null
| AZURE_CLIENTPAT | Azure Personal Access Token | null

::: warning Important:
* To use Azure DevOps all parameters with prefix `AZURE_` have to be set.
  :::


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
      - "AZURE_ORGANIZATION=organization"
      - "AZURE_CLIENTPAT=[xxx]"
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

#The name of the organization in Azure
AZURE_ORGANIZATION=organization

#Azure Personal Access Token
AZURE_CLIENTPAT=xxx
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
:::


## Starting App

If all the configuration is done, Diveni could be started:

```shell
docker-compose up -d
```
