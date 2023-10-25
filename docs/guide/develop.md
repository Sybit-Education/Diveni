# Developers Guide

![GitHub Repo stars](https://img.shields.io/github/stars/Sybit-Education/Diveni?style=social)

See also: 

* [Code of Conduct](../code_of_conduct)
* [Contribution](./contribution)

## Preconditions / Installation

### Java Environment

Java 17 is required. We suggest SAPMachine.

- Download SAPMachine <https://sap.github.io/SapMachine/>
- Select the appropriate installer for your OS

![Download_Selection_Java17](../img/Java17_Installer_Selection.png)
  
- Gradle
  - Download from Gradle official site <https://gradle.org/install/>

### Frontend

Node.js is required. We suggest Node.js 16 LTS.

- Download from nodejs <https://nodejs.org/en/download/>
- Select the appropriate installer for your OS

![Download_Selection_nodejs](../img/nodejs_Installer_Selection.png)


### Database: MongoDB

MongoDB is running in the background at port 27017
  - <https://www.mongodb.com/docs/manual/administration/install-community/>
    ![Download_Selection_MongoDB](../img/MongoDB_Installer_Selection.png)
  - Select the appropriate installer for your OS and follow the instructions

#### Docker

On Linux/WSL, you can also use Docker to run MongoDB:

```bash
docker pull mongodb/mongodb-community-server
docker run -p 27017:27017 --name mongo -d mongodb/mongodb-community-server
```

## Setup Project

1. Clone repository with Git from GitHub:<br />
      *git clone https://github.com/Sybit-Education/Diveni.git* <br />
   Or download the repository and unzip it’s content:
      *https://github.com/Sybit-Education/Diveni/archive/refs/heads/main.zip*
2. Start backend
   * Open  first terminal and enter these commands:
   * cd /backend
   * gradle bootRun
   * The backend should now be running and accessible at http://localhost:8081
3. Start frontend
   * Open the second terminal and enter these commands:
   * cd /frontend
   * npm install
   * npm run serve
   * The frontend should now be running and accessible at http://localhost:8080
4. To use Diveni simply enter the following url in your standard browser: <http://localhost:8080>


## Connectors

### JIRA Connection


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

## Troubleshooting

- When working with IntelliJ and starting Diveni's Backend with IntelliJs editor it may throw an
  exception when trying to connect with Jira.\
  This is because it can not find the desired .env file in your working directory (/backend).
  When this happens make sure to include ```/backend``` to your run working directory in the run
  configuration settings.
