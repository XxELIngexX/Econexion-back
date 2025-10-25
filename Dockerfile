FROM openjdk:21-jdk

WORKDIR /app

ENV PORT=35000
EXPOSE 35000

COPY target/econexion-1.0-SNAPSHOT.jar app.jar


ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=lab"]
