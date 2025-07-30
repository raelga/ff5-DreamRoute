package com.Travellers.DreamRoute.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Setter
@Getter
public class ErrorResponse {
    String message;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    LocalDateTime timestamp = LocalDateTime.now();

    Map<String, String> details;
}