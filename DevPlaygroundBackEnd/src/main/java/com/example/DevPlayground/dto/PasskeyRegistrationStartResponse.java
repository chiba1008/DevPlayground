package com.example.DevPlayground.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasskeyRegistrationStartResponse {
    private String challenge;
    private RelyingParty rp;
    private User user;
    private List<PublicKeyCredentialParameters> pubKeyCredParams;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelyingParty {
        private String id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private String id;
        private String name;
        private String displayName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PublicKeyCredentialParameters {
        private String type;
        private int alg;
    }
}