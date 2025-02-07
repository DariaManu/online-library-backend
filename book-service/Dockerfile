FROM openjdk:21
ADD target/book-service-0.0.1-SNAPSHOT.jar app/build/book-service.jar
WORKDIR app/build
ENTRYPOINT ["java", "-jar", "book-service.jar"]