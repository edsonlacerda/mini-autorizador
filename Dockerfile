FROM eclipse-temurin:17-jdk-alpine AS build
RUN apk update && \
    apk add --no-cache maven
WORKDIR /app
COPY pom.xml .
COPY src /app/src
RUN mvn clean install

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
RUN mkdir -p /app/target && \
    touch /app/target/mini-autorizador-0.0.1-SNAPSHOT.jar
COPY --from=build /app/target/mini-autorizador-0.0.1-SNAPSHOT.jar /app/mini-autorizador-0.0.1-SNAPSHOT.jar
RUN ls -l /app/mini-autorizador-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/app/mini-autorizador-0.0.1-SNAPSHOT.jar"]