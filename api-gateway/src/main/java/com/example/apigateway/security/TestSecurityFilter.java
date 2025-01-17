package com.example.apigateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TestSecurityFilter extends AbstractGatewayFilterFactory<TestSecurityFilter.Config> {
    @Autowired
    private ObjectMapper objectMapper;

    public TestSecurityFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            System.out.println(path);
            HttpHeaders httpHeaders = request.getHeaders();
            String token = httpHeaders.getFirst(HttpHeaders.AUTHORIZATION);
            System.out.println(token);

            if (token == null || !token.toLowerCase().startsWith("basic")) {
                return handleAuthorizationError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String base64Credentials = token.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            final String[] values = credentials.split(":", 2);
            System.out.println(values[0]);
            System.out.println(values[1]);

            if (config.getCheckForUserRole().contains(path)) {
                try {
                    HttpRequest req = HttpRequest.newBuilder()
                            .uri(new URI("http://localhost:8080/authorize/user"))
                            .header("Authorization", token)
                            .GET()
                            .build();
                    try (HttpClient client = HttpClient.newHttpClient()) {
                        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
                        System.out.println(response);
                        System.out.println(response.statusCode());
                        System.out.println(response.body());
                        if (response.statusCode() == HttpStatus.FORBIDDEN.value()) {
                            return handleAuthorizationError(exchange, "Forbidden", HttpStatus.FORBIDDEN);
                        }
                        if (response.statusCode() == HttpStatus.UNAUTHORIZED.value()) {
                            return handleAuthorizationError(exchange, "Unauthorized", HttpStatus.UNAUTHORIZED);
                        }
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            } else if (config.getCheckForAdminRole().contains(path)) {
                try {
                    HttpRequest req = HttpRequest.newBuilder()
                            .uri(new URI("http://localhost:8080/authorize/admin"))
                            .header("Authorization", token)
                            .GET()
                            .build();
                    try (HttpClient client = HttpClient.newHttpClient()) {
                        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
                        System.out.println(response);
                        System.out.println(response.statusCode());
                        System.out.println(response.body());
                        if (response.statusCode() == HttpStatus.FORBIDDEN.value()) {
                            return handleAuthorizationError(exchange, "Forbidden", HttpStatus.FORBIDDEN);
                        }
                        if (response.statusCode() == HttpStatus.UNAUTHORIZED.value()) {
                            return handleAuthorizationError(exchange, "Unauthorized", HttpStatus.UNAUTHORIZED);
                        }
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            } else if (config.getCheckForBothRoles().contains(path)) {
                try {
                    HttpRequest req = HttpRequest.newBuilder()
                            .uri(new URI("http://localhost:8080/authorize/all"))
                            .header("Authorization", token)
                            .GET()
                            .build();
                    try (HttpClient client = HttpClient.newHttpClient()) {
                        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
                        System.out.println(response);
                        System.out.println(response.statusCode());
                        System.out.println(response.body());
                        if (response.statusCode() == HttpStatus.FORBIDDEN.value()) {
                            return handleAuthorizationError(exchange, "Forbidden", HttpStatus.FORBIDDEN);
                        }
                        if (response.statusCode() == HttpStatus.UNAUTHORIZED.value()) {
                            return handleAuthorizationError(exchange, "Unauthorized", HttpStatus.UNAUTHORIZED);
                        }
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            return chain.filter(exchange);
        }));
    }

    private Mono<Void> handleAuthorizationError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timeStamp", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        responseBody.put("message", message);
        responseBody.put("status", httpStatus.value());
        responseBody.put("errorCode", httpStatus.value());

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(responseBody);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Config {
        private List<String> checkForUserRole = List.of("/authorize/test1");
        private List<String> checkForAdminRole = List.of("/authorize/test2");
        private List<String> checkForBothRoles = List.of("/authorize/test3");

        public List<String> getCheckForUserRole() {
            return checkForUserRole;
        }

        public List<String> getCheckForAdminRole() {
            return checkForAdminRole;
        }

        public List<String> getCheckForBothRoles() {
            return checkForBothRoles;
        }

        public void setCheckForUserRole(List<String> checkForUserRole) {
            this.checkForUserRole = checkForUserRole;
        }

        public void setCheckForAdminRole(List<String> checkForAdminRole) {
            this.checkForAdminRole = checkForAdminRole;
        }

        public void setCheckForBothRoles(List<String> checkForBothRoles) {
            this.checkForBothRoles = checkForBothRoles;
        }
    }
}
