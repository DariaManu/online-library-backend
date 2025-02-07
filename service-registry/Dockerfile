FROM openjdk:21
ADD target/service-registry-0.0.1-SNAPSHOT.jar app/build/service-registry.jar
WORKDIR app/build
ENTRYPOINT ["java", "-jar", "service-registry.jar"]
