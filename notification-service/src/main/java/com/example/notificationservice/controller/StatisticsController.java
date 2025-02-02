package com.example.notificationservice.controller;

import com.example.notificationservice.model.BookDownloadHistoryItem;
import com.example.notificationservice.model.BookDownloadHistoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/statistics")
public class StatisticsController {
    @Value("${aws.lambda.function-url}")
    private String awsLambdaFunctionUrl;
    private ObjectMapper objectMapper = new ObjectMapper();

    private final BookDownloadHistoryRepository repository;

    public StatisticsController(final BookDownloadHistoryRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/genre")
    public ResponseEntity<String> getMostPopularGenre() {
        List<BookDownloadHistoryItem> downloadHistoryItems = new ArrayList<>();
        repository.findAll().forEach(downloadHistoryItems::add);
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(downloadHistoryItems);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(awsLambdaFunctionUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            try (HttpClient client = HttpClient.newHttpClient()) {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == HttpStatus.OK.value()) {
                    return ResponseEntity.ok().body(response.body());
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
