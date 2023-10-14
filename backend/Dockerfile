FROM gradle:7.5.0 as build-stage
COPY --chown=gradle:gradle . /app/Diveni
WORKDIR /app/Diveni
RUN gradle --no-daemon assemble

FROM ghcr.io/graalvm/graalvm-ce:ol8-java17 as graalvm-build-stage
COPY --from=build-stage /app/Diveni/build/libs/backend-1.0.0-SNAPSHOT.jar diveni.jar
WORKDIR /app/Diveni

RUN gu install native-image

RUN native-image --no-server -cp diveni.jar

FROM debian:bullseye-slim as production-stage
WORKDIR /app/Diveni
COPY --from=graalvm-build-stage /app/Diveni/diveni diveni
ENTRYPOINT ["./diveni"]
