FROM openjdk:21
ADD target/notification-service-0.0.1-SNAPSHOT.jar app/build/notification-service.jar
WORKDIR app/build
ENTRYPOINT ["java", "-jar", "notification-service.jar"]