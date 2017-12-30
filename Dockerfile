FROM openjdk:8-jre-alpine

RUN addgroup -S n26 && adduser -S -g n26 n26
USER n26

COPY target/n26-transaction-service.jar app.jar

ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT exec java -jar /app.jar