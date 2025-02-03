package com.example.apigateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import java.util.regex.Pattern;

@Component
public class ServiceEndpointsSecurityFilter extends AbstractGatewayFilterFactory<ServiceEndpointsSecurityFilter.Config> {
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${authorization.uri}")
    private String authorizationUri;

    public ServiceEndpointsSecurityFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            System.out.println(path);
            HttpMethod method = request.getMethod();
            System.out.println(method);
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

            if (checkIfPathMatchesPatternToCheck(method, path, config.getCheckForUserRole())) {
                System.out.println("Authorize for user role access");
                try {
                    HttpRequest req = HttpRequest.newBuilder()
                            .uri(new URI(authorizationUri + "/authorize/user"))
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
            } else if (checkIfPathMatchesPatternToCheck(method, path, config.getCheckForAdminRole())) {
                System.out.println("Authorize for admin role access");
                try {
                    HttpRequest req = HttpRequest.newBuilder()
                            .uri(new URI(authorizationUri + "/authorize/admin"))
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
            } else if (checkIfPathMatchesPatternToCheck(method, path, config.getCheckForBothRoles())) {
                System.out.println("Authorize access for both roles");
                try {
                    HttpRequest req = HttpRequest.newBuilder()
                            .uri(new URI(authorizationUri + "/authorize/all"))
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

    private boolean checkIfPathMatchesPatternToCheck(HttpMethod method,
                                                     String path,
                                                     Map<HttpMethod, List<Pattern>> patternsToCheck) {
        if (patternsToCheck.containsKey(method)) {
            List<Pattern> pathsToMatch = patternsToCheck.get(method);
            for (Pattern pathPattern: pathsToMatch) {
                if (pathPattern.matcher(path).matches()) {
                    return true;
                }
            }
        }
        return false;
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
        private Map<HttpMethod, List<Pattern>> checkForUserRole = new HashMap<>();
        private Map<HttpMethod, List<Pattern>> checkForAdminRole = Map.ofEntries(
                Map.entry(HttpMethod.POST, List.of(
                        Pattern.compile("/books"),
                        Pattern.compile("/books/[0-9]+/coverImage"),
                        Pattern.compile("/books/[0-9]+/resource")
                )),
                Map.entry(HttpMethod.GET, List.of(
                        Pattern.compile("/statistics/genre")
                ))
        );
        private Map<HttpMethod, List<Pattern>> checkForBothRoles = Map.ofEntries(
                Map.entry(HttpMethod.GET, List.of(
                        Pattern.compile("/books"),
                        Pattern.compile("/books/[0-9]+/coverImage"),
                        Pattern.compile("/books/[0-9]+/resource")
                ))
        );

        public Map<HttpMethod, List<Pattern>> getCheckForUserRole() {
            return checkForUserRole;
        }

        public Map<HttpMethod, List<Pattern>> getCheckForAdminRole() {
            return checkForAdminRole;
        }

        public Map<HttpMethod, List<Pattern>> getCheckForBothRoles() {
            return checkForBothRoles;
        }

        public void setCheckForUserRole(Map<HttpMethod, List<Pattern>> checkForUserRole) {
            this.checkForUserRole = checkForUserRole;
        }

        public void setCheckForAdminRole(Map<HttpMethod, List<Pattern>> checkForAdminRole) {
            this.checkForAdminRole = checkForAdminRole;
        }

        public void setCheckForBothRoles(Map<HttpMethod, List<Pattern>> checkForBothRoles) {
            this.checkForBothRoles = checkForBothRoles;
        }
    }
}
