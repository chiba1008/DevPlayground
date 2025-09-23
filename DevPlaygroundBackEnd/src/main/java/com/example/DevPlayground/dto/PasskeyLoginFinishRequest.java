package com.example.DevPlayground.dto;

import lombok.Data;

@Data
public class PasskeyLoginFinishRequest {
    private String username;
    private AuthenticationResponse authenticationResponse;

    @Data
    public static class AuthenticationResponse {
        private String id;
        private String rawId;
        private String type;
        private String clientDataJSON;
        private String authenticatorData;
        private String signature;
    }
}