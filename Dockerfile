FROM amazoncorretto:17-alpine

RUN apk update && apk add --no-cache curl

EXPOSE 8080

COPY MathCaptain/weakness/build/libs/weakness-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
