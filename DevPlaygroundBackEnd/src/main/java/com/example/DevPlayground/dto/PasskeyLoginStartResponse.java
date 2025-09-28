package com.example.DevPlayground.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasskeyLoginStartResponse {
    private String challenge;
    private List<AllowCredentials> allowCredentials;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllowCredentials {
        private String type;
        private String id;
    }
}