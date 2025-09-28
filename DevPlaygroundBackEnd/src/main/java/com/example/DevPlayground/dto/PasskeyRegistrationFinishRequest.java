package com.example.DevPlayground.dto;

import lombok.Data;

@Data
public class PasskeyRegistrationFinishRequest {
    private String username;
    private RegistrationResponse registrationResponse;

    @Data
    public static class RegistrationResponse {
        private String id;
        private String rawId;
        private String type;
        private String clientDataJSON;
        private String attestationObject;
    }
}