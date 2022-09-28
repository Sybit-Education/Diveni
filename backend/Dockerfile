FROM gradle:7.5.0 as build-stage
COPY --chown=gradle:gradle . /app/Diveni
WORKDIR /app/Diveni
COPY src src
RUN gradle build --no-daemon -x test

FROM openjdk:11 as production-stage
WORKDIR /app/Diveni
RUN echo "Test"
COPY --from=build-stage /app/Diveni/build/libs/backend-1.0.0-SNAPSHOT.jar diveni.jar
ENTRYPOINT ["java", "-jar", "diveni.jar"]
