FROM openjdk:17
ADD target/SunsetSunriseAPI-0.0.1-SNAPSHOT.jar SunsetSunriseAPI-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "SunsetSunriseAPI-0.0.1-SNAPSHOT.jar"]