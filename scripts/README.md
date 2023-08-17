# Deployment scripts
The scripts located in this folder are used to deploy Diveni on our server.
There are two scripts:
1. startDiveni.sh \
   This script is used to deploy Diveni on our production instance. It is available at [Diveni.io](https://diveni.io) \
   It uses the prebuilt docker images provided by GitHub. There is a docker-compose.yml inside the Diveni folder.
2. startDevDiveni.sh \
   This script is used to deploy Diveni on our staging instance. It is available at [Dev.Diveni.io](https://dev.diveni.io) \
   It uses the cloned repository of Diveni and builds the docker images itself. \
   To use the script, just run the following command and replace the branch and directory with the desired ones.
   ```shell
   bash startDevDiveni.sh -b <branch> -d <directory>
   ```
