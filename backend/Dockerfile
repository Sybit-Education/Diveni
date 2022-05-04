FROM maven:3.8.5-openjdk-11-slim as build-stage
WORKDIR /app/Diveni
COPY src src
COPY pom.xml .
RUN mvn package -DskipTests

FROM openjdk:11 as production-stage
WORKDIR /app/Diveni
COPY --from=build-stage /app/Diveni/target/app.jar diveni.jar
ENTRYPOINT ["java", "-jar", "diveni.jar"]
