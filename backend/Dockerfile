FROM sapmachine:lts

WORKDIR /app

COPY ./target/app.jar ./diveni.jar

CMD java -jar -Dspring.profiles.active=prod ./diveni.jar
