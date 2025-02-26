FROM amazoncorretto:17

EXPOSE 8080

COPY MathCaptain/weakness/build/libs/weakness-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
