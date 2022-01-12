FROM sapmachine:lts

WORKDIR /app

COPY ./target/app.jar ./aume.jar

CMD java -jar -Dspring.profiles.active=prod ./aume.jar
