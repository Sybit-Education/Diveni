# Developers Guide

## Installation

### Technical Requirements

- Java 11
  - Download from SAPMachine <https://sap.github.io/SapMachine/>
    <img :src="$withBase('/img/Java11_Installer_Selection.png')" alt="Download_Selection_Java11">
  - Select the appropriate installer for your OS
- Maven
  - Download from Apache <https://maven.apache.org/download.cgi>
    <img :src="$withBase('/img/Maven_Installer_Selection.png')" alt="Download_Selection_Maven">
  - Click the link in the marked line
- Node.js
  - Download from nodejs <https://nodejs.org/en/download/>
    <img :src="$withBase('/img/nodejs_Installer_Selection.png')" alt="Download_Selection_nodejs">
  - Select the appropriate installer for your OS
- MongoDB
  - MongoDB is running in the background at port 27017
  - <https://www.mongodb.com/docs/manual/administration/install-community/>
    <img :src="$withBase('/img/MongoDB_Installer_Selection.png')" alt="Download_Selection_MongoDB">
  - Select the appropriate installer for your OS and follow the instructions

### Setup Project

1. Clone repository with Git from Github:
      git clone https://github.com/Sybit-Education/Diveni.git
   Or download the repository and unzip it’s content:
      https://github.com/Sybit-Education/Diveni/archive/refs/heads/main.zip
2. Start backend
   a. Open  first terminal and enter these commands:
   b. cd /backend
   c. mvn spring-boot:run
   d. The backend should now be running and accessible at http://localhost:8081
3. Start frontend
   a. Open the second terminal and enter these commands:
   b. cd /frontend
   c. npm install
   d. npm run serve
   e. The frontend should now be running and accessible at http://localhost:8080
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
