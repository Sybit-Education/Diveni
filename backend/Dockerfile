FROM sapmachine:lts

WORKDIR /app

COPY ./target/aume-0.0.1-SNAPSHOT.jar ./aume.jar

CMD java -jar -Dspring.profiles.active=prod ./aume.jar
