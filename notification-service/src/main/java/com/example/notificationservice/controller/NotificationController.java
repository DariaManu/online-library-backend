package com.example.notificationservice.controller;

import com.example.notificationservice.sockets.Socket;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @PostMapping
    public ResponseEntity<String> testSocket(@RequestParam("message") String message) {
        Socket.broadcast(message);
        String successMessage = String.format("Operation completed! " +
                "Data broadcast to %s listeners", Socket.listeners.size());
        return ResponseEntity.ok().body(successMessage);
    }
}
