package com.example.DevPlayground.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasskeyLoginFinishResponse {
    private boolean success;
    private String message;
    private String username;
}