### Jira Cloud Connector

1. To set up Diveni to connect to JIRA Cloud instances, refer to this guide to generate an RSA public/private key pair:
   <https://developer.atlassian.com/cloud/jira/platform/jira-rest-api-oauth-authentication/#step-2--configure-the-client-application-as-an-oauth-consumer>

2. After that you need to define a `consumer key`. The `consumer key` can be anything as long as it matches the application link.
   The `private key` (generated in step 1) and the `consumer key` have to be entered as `JIRA_CLOUD_PRIVATEKEY` and `JIRA_CLOUD_CONSUMERKEY` in `backend/.env`

##  Create new Session with connection to Jira Cloud
To use JIRA Cloud with Diveni you can choose to either use our official server [Diveni.io](https://diveni.io) or use a self-hosted instance
of Diveni. Either way, the connection process is the same for both, but for your local instance you will need to provide different credentials.
::: info
You must be an administrator of your JIRA Cloud instance to create an application link for Diveni.
:::

::: details Set-Up Guide for Application Links
#### 1. Step
Go to your JIRA Cloud instance and click on the Settings icon in the top right corner. Then select `Products`.
![Jira Cloud Step 1](/img/connector/jira_cloud_step1.png)

#### 2. Step
On the configuration page click on `Application links` under `INTEGRATIONS` on the left side.
![JIRA Cloud Step 2](/img/jira/jira_cloud_step2.png)

#### 3. Step
Under Application Links, click the `Create link` button to create an application link.
![JIRA Cloud Step 3](/img/jira/jira_cloud_step3.png)

#### 4. Step
A modal should appear to create a link. Make sure to select `Direct application link` and enter either `https://diveni.io` or the URL of
your self-hosted instance if you do not want to use our official server. After that click on continue.
![JIRA Cloud Step 4](/img/jira/jira_cloud_step4.png)

If you get a warning that there was no response from the URL you entered, ignore it. Just click continue again.
![JIRA Cloud Step 4 Warning](/img/jira/jira_cloud_step4_warning.png)

#### 5. Step
Now a review link modal will pop up. All you have to do is enter `Diveni` as Application name and select `Generic Application` in the
Application Type dropdown. Finally, make sure you check the `Create incoming link` checkbox and click on continue.
![JIRA Cloud Step 5](/img/jira/jira_cloud_step5.png)

#### 6. Step
The last on your JIRA Cloud instance is to create an incoming link.
1. Set `Diveni_OauthKey` as Consumer Key (or your own consumer key for self-hosted instances)
2. Set `Diveni` as Consumer Name
3. Enter the following key (or your own public key for self-hosted instances) as Public Key
4. Click on continue and wait for the application link to be set up
   ![JIRA Cloud Step 6](/img/jira/jira_cloud_step6.png)
   ::: tip Public Key
   MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC1dCI+Ks75SQKHIpxeefj+K8HE
   hihwn9L/8NAc+A8LDCUSOClL3CDO0H8oVQOvvQwJm8RQDO3TFL+dZfaC4oW6zTkj
   ch2oV9sBGaZuK4gVhhzZSErzWEpZ9BnGvk3/ZuTF8niRUc35y/wo6o6lFiICRfc5
   iBE48mAsbEu7kiiKvQIDAQAB
   :::

::: details Guide for using JIRA Cloud on Diveni
#### 7. Step
When using Diveni, you click on `Planning with Issue-Tracker`. Then click on the `Sign in with Jira Cloud` button and a input field will appear.
Enter the URL of your Atlassian instance. At the end click `Connect`.
![JIRA Cloud Step 7](/img/jira/jira_cloud_step7.png)

#### 8. Step
A new browser tab will open asking you to allow access to your Atlassian instance. Simply click on `Allow`.
![JIRA Cloud Step 8](/img/jira/jira_cloud_step8.png)

#### 9. Step
You will be redirected to a page that says `Access Approved`. From there you have to copy the verification code (marked red in the screenshot).
Then you can close this browser tab and return to Diveni.
![JIRA Cloud Step 9]( /img/jira/jira_cloud_step9.png)

#### 10. Step
Back at Diveni a modal will appear. There you have to enter the copied verification code into the input field and click `OK`.
![JIRA Cloud Step 10](/img/jira/jira_cloud_step10.png)
:::
## Configuration
#### Available configuration
| Parameter | Description | Default value |
|---|---|:---:|
| SERVER_URL | URL the server is running on, used for CORS-settings | null |
| LOCALE | The locale the frontend should be set to, available locales=en,de,es,fr,it,pl,pt,uk | en |
| JIRA_CLOUD_CONSUMERKEY | Consumer key | OauthKey |
| JIRA_CLOUD_PRIVATEKEY | Private key | null |

::: warning Important:
* In order to use Jira Cloud all parameters with prefix `JIRA_CLOUD_` are required to be set.
  :::
## Add configuration to the Diveni instance
::: details Diveni with configuration via docker compose
To configure your local environment, you have to add your configuration to `/backend/.env`

See below for example:
```properties 
#URL the server is running on
SERVER_URL=http://localhost:8080

#The locale the frontend should be set to
LOCALE=en

#Consumer key
JIRA_CLOUD_CONSUMERKEY=OauthKey

#Private key
JIRA_CLOUD_PRIVATEKEY=[key]
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
    - "SERVER_URL=http://localhost:8080"
    - "LOCALE=en"
    - "JIRA_CLOUD_CONSUMERKEY=OauthKey"
    - "JIRA_CLOUD_PRIVATEKEY=[xxx]"
  [...]
```
:::


## Starting App

If all the configuration is done, Diveni could be started:

```shell
docker-compose up -d
```


