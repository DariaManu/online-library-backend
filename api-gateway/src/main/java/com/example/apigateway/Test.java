package com.example.apigateway;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8081/authorize/test1"))
                    .GET()
                    .build();
            try (HttpClient client = HttpClient.newHttpClient()) {
                HttpResponse<Stream<String>> response = client.send(req, HttpResponse.BodyHandlers.ofLines());
                response.body().toList().forEach(System.out::println);
                System.out.println(response.statusCode());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
