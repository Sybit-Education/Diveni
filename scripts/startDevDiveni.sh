#!/bin/bash
Help()
{
   # Display Help
   echo "To start the dev instance of Diveni, just run the startDevDiveni.sh script with a branch and a directory as argument."
   echo
   echo "Syntax: bash startDevDiveni.sh -b <branch> -d <directory>"
   echo "options:"
   echo "-b     Branch to be deployed."
   echo "-d     Directory where the cloned repository is located."
   echo "-h     Print this Help."
   echo
}

while getopts ":hb:d:" o; do
    case "${o}" in
      h) # display Help
         Help
         exit;;
      b)
         branch=${OPTARG}
         ;;
      d)
         dir=${OPTARG}
         ;;
      *)
         Help
         ;;
   esac
done

if [ -z "${branch}" ] || [ -z "${dir}" ]; then
    echo "Invalid arguments. Use -h if you need help."
    exit 22
fi

echo Change directory to "${dir}"
cd "${dir}" || exit

echo Reverting uncommitted changes ...
git reset --hard

echo Git checkout branch "${branch}"
git checkout "${branch}"

echo Git fetch ...
git fetch

echo Updating Diveni ...
git pull

echo Updating devs docker compose file ...
sed -i 's/    container_name: diveni_database/    container_name: dev_diveni_database/g' docker-compose.dev.yml
sed -i 's/    container_name: diveni_backend/    container_name: dev_diveni_backend/g' docker-compose.dev.yml
sed -i 's/    container_name: diveni_frontend/    container_name: dev_diveni_frontend/g' docker-compose.dev.yml
sed -i 's/    container_name: diveni_proxy/    container_name: dev_diveni_proxy/g' docker-compose.dev.yml
sed -i 's/    container_name: diveni_ai/    container_name: dev_diveni_ai/g' docker-compose.dev.yml
sed -i 's/      - "80:80"/      - "9999:80"/g' docker-compose.dev.yml

echo Updating devs backend prod file ...
sed -i 's/diveni_database/dev_diveni_database/g' backend/src/main/resources/application-prod.properties

echo Starting Diveni ...
docker compose -f docker-compose.dev.yml up --build -d
