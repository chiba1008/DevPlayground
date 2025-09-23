package com.example.DevPlayground.service;

import com.example.DevPlayground.dto.PasskeyRegistrationFinishRequest;
import com.example.DevPlayground.dto.PasskeyRegistrationFinishResponse;
import com.example.DevPlayground.dto.PasskeyRegistrationStartResponse;
import com.example.DevPlayground.entity.Passkey;
import com.example.DevPlayground.entity.PasskeyChallenge;
import com.example.DevPlayground.entity.Users;
import com.example.DevPlayground.repository.PasskeyChallengeRepository;
import com.example.DevPlayground.repository.PasskeyRepository;
import com.example.DevPlayground.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasskeyService {

    private final PasskeyChallengeRepository passkeyChallengeRepository;
    private final PasskeyRepository passkeyRepository;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public PasskeyRegistrationStartResponse startRegistration(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // 既存のchallengeを削除
        passkeyChallengeRepository.deleteByUsername(username);
        
        // 新しいchallengeを生成
        String challenge = generateChallenge();
        
        PasskeyChallenge passkeyChallenge = new PasskeyChallenge();
        passkeyChallenge.setUsername(username);
        passkeyChallenge.setChallenge(challenge);
        passkeyChallengeRepository.save(passkeyChallenge);

        // WebAuthn registrationオプションを返す
        return new PasskeyRegistrationStartResponse(
                challenge,
                new PasskeyRegistrationStartResponse.RelyingParty("localhost", "DevPlayground"),
                new PasskeyRegistrationStartResponse.User(
                        user.getId().toString(),
                        user.getUsername(),
                        user.getUsername()
                ),
                List.of(
                        new PasskeyRegistrationStartResponse.PublicKeyCredentialParameters("public-key", -7), // ES256
                        new PasskeyRegistrationStartResponse.PublicKeyCredentialParameters("public-key", -257) // RS256
                )
        );
    }

    @Transactional
    public PasskeyRegistrationFinishResponse finishRegistration(PasskeyRegistrationFinishRequest request) {
        String username = request.getUsername();
        String challengeFromClient = extractChallengeFromClientData(request.getRegistrationResponse().getClientDataJSON());

        // challengeを検証
        Optional<PasskeyChallenge> storedChallenge = passkeyChallengeRepository
                .findByUsernameAndChallenge(username, challengeFromClient);

        if (storedChallenge.isEmpty()) {
            return new PasskeyRegistrationFinishResponse(false, "Invalid challenge");
        }

        // 期限切れチェック
        if (storedChallenge.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            passkeyChallengeRepository.delete(storedChallenge.get());
            return new PasskeyRegistrationFinishResponse(false, "Challenge expired");
        }

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Passkeyを保存
        Passkey passkey = new Passkey();
        passkey.setUsername(username);
        passkey.setCredentialId(request.getRegistrationResponse().getId());
        passkey.setPublicKey(request.getRegistrationResponse().getAttestationObject());
        passkey.setAttestationObject(request.getRegistrationResponse().getAttestationObject());
        passkey.setClientDataJSON(request.getRegistrationResponse().getClientDataJSON());
        passkey.setSignatureCount(0L);
        passkeyRepository.save(passkey);

        // challengeを削除
        passkeyChallengeRepository.delete(storedChallenge.get());

        return new PasskeyRegistrationFinishResponse(true, "Passkey registered successfully");
    }

    @Transactional
    public void cleanupExpiredChallenges() {
        passkeyChallengeRepository.deleteExpiredChallenges(LocalDateTime.now());
    }

    private String generateChallenge() {
        byte[] challengeBytes = new byte[32];
        secureRandom.nextBytes(challengeBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(challengeBytes);
    }

    private String extractChallengeFromClientData(String clientDataJSON) {
        try {
            String decodedClientData = new String(Base64.getUrlDecoder().decode(clientDataJSON));
            // 簡単なJSON解析（実際のプロダクションではJSONライブラリを使用）
            String challengePrefix = "\"challenge\":\"";
            int startIndex = decodedClientData.indexOf(challengePrefix) + challengePrefix.length();
            int endIndex = decodedClientData.indexOf("\"", startIndex);
            return decodedClientData.substring(startIndex, endIndex);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract challenge from client data", e);
        }
    }
}
