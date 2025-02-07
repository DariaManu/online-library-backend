FROM openjdk:21
ADD target/user-service-0.0.1-SNAPSHOT.jar app/build/user-service.jar
WORKDIR app/build
ENTRYPOINT ["java", "-jar", "user-service.jar"]