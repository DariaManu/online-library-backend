package com.example.bookservice.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookFileDTO implements Serializable {
    private String mimeType;
    private StreamingResponseBody streamingResponseBody;
}
