FROM openjdk:21
ADD target/api-gateway-0.0.1-SNAPSHOT.jar app/build/api-gateway.jar
WORKDIR app/build
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]