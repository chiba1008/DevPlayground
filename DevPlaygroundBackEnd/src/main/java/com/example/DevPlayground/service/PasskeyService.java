package com.example.DevPlayground.service;

import com.webauthn4j.data.PublicKeyCredentialRpEntity;
import com.webauthn4j.data.PublicKeyCredentialUserEntity;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class PasskeyService {

    public static class PasskeyRegistrationStartResponse {
        public String challenge;
        public Rp rp;
        public User user;
        public List<PubKeyCredParam> pubKeyCredParams;
        public PasskeyRegistrationStartResponse(String challenge, Rp rp, User user, List<PubKeyCredParam> pubKeyCredParams) {
            this.challenge = challenge;
            this.rp = rp;
            this.user = user;
            this.pubKeyCredParams = pubKeyCredParams;
        }
        public static class Rp {
            public String id;
            public String name;
            public Rp(String id, String name) {
                this.id = id;
                this.name = name;
            }
        }
        public static class User {
            public String id;
            public String name;
            public String displayName;
            public User(String id, String name, String displayName) {
                this.id = id;
                this.name = name;
                this.displayName = displayName;
            }
        }
        public static class PubKeyCredParam {
            public String type;
            public int alg;
            public PubKeyCredParam(String type, int alg) {
                this.type = type;
                this.alg = alg;
            }
        }
    }

    public PasskeyRegistrationStartResponse registerPasskeyStart(String userName) {
        // チャレンジを発行
        DefaultChallenge challenge = new DefaultChallenge();
        String challengeBase64 = Base64.getEncoder().encodeToString(challenge.getValue());
        PublicKeyCredentialRpEntity rpEntity = new PublicKeyCredentialRpEntity(
                "localhost",
                "Example App"
        );

        PublicKeyCredentialUserEntity userEntity = new PublicKeyCredentialUserEntity(
                userName.getBytes(), // 後でUserIdにする
                userName,
                userName
        );

        List<PasskeyRegistrationStartResponse.PubKeyCredParam> pubKeyCredParams = List.of(
                new PasskeyRegistrationStartResponse.PubKeyCredParam("public-key", -7),
                new PasskeyRegistrationStartResponse.PubKeyCredParam("public-key", -257)
        );
        return new PasskeyRegistrationStartResponse(
                challengeBase64,
                new PasskeyRegistrationStartResponse.Rp(rpEntity.getId(), rpEntity.getName()),
                new PasskeyRegistrationStartResponse.User(
                        Base64.getEncoder().encodeToString(userEntity.getId()),
                        userEntity.getName(),
                        userEntity.getDisplayName()
                ),
                pubKeyCredParams
        );
    }
}
