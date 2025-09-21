package com.example.DevPlayground.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HelloResponse {
    private final String message;
    private final String status;
}
