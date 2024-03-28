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
    <https://www.mongodb.com/docs/manual/administration/install-community/>

    ![Download_Selection_MongoDB](../img/MongoDB_Installer_Selection.png)

#### Docker

On Linux/WSL, you can also use Docker to run MongoDB:

```bash
docker pull mongodb/mongodb-community-server
docker run -p 27017:27017 --name mongo -d mongodb/mongodb-community-server
```

## Setup Project

1. Clone repository with Git from GitHub:
    ```shell
    git clone https://github.com/Sybit-Education/Diveni.git
    ```
2. Start backend
* Open  first terminal and enter these commands:
  ```shell
  cd /backend
  gradle bootRun
  ```
* The backend should now be running and accessible at <http://localhost:8081>
3. Start frontend
* Open the second terminal and enter these commands:
  ```shell
  cd /frontend
  npm install
  npm run serve
  ```
* The frontend should now be running and accessible at <http://localhost:8080>
4. To use Diveni simply enter the following url in your standard browser: <http://localhost:8080>


## Issue-Tracker
- To set up Issue Tracker, see our [Installation Guide](install#issue-tracker).

## Troubleshooting

- When working with IntelliJ and starting Diveni's backend with IntelliJ's editor, it may throw an
  exception when trying to connect to an issue tracker.\
  This is because it cannot find the desired .env file in your working directory (/backend).
  If this happens, make sure to add ```/backend``` to your run working directory in the run
  configuration settings.
