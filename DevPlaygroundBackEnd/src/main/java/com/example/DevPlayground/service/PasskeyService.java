package com.example.DevPlayground.service;

import com.example.DevPlayground.dto.*;
import com.example.DevPlayground.entity.Passkey;
import com.example.DevPlayground.entity.PasskeyChallenge;
import com.example.DevPlayground.entity.Users;
import com.example.DevPlayground.repository.PasskeyChallengeRepository;
import com.example.DevPlayground.repository.PasskeyRepository;
import com.example.DevPlayground.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * Passkeyに関するサービス
 */
@Service
@RequiredArgsConstructor
public class PasskeyService {

    private final PasskeyChallengeRepository passkeyChallengeRepository;
    private final PasskeyRepository passkeyRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Passkey登録の開始
     * usernameを受け取り、challengeを生成してDBに保存し、
     * WebAuthnの登録オプションを返す
     *
     * @param username ユーザー名
     * @return PasskeyRegistrationStartResponse
     */
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

    /**
     * Passkey登録の完了
     * クライアントからのレスポンスを受け取り、challengeを検証し、
     * PasskeyをDBに保存する
     *
     * @param request PasskeyRegistrationFinishRequest
     * @return PasskeyRegistrationFinishResponse
     */
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
    public PasskeyLoginStartResponse startLogin(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // ユーザーのPasskeyを取得
        List<Passkey> userPasskeys = passkeyRepository.findByUsername(username);
        if (userPasskeys.isEmpty()) {
            throw new RuntimeException("No passkeys found for user: " + username);
        }

        // 既存のchallengeを削除
        passkeyChallengeRepository.deleteByUsername(username);
        
        // 新しいchallengeを生成
        String challenge = generateChallenge();
        
        PasskeyChallenge passkeyChallenge = new PasskeyChallenge();
        passkeyChallenge.setUsername(username);
        passkeyChallenge.setChallenge(challenge);
        passkeyChallengeRepository.save(passkeyChallenge);

        // allowCredentialsを作成
        List<PasskeyLoginStartResponse.AllowCredentials> allowCredentials = userPasskeys.stream()
                .map(passkey -> new PasskeyLoginStartResponse.AllowCredentials("public-key", passkey.getCredentialId()))
                .toList();

        return new PasskeyLoginStartResponse(challenge, allowCredentials);
    }

    @Transactional
    public PasskeyLoginFinishResponse finishLogin(PasskeyLoginFinishRequest request) {
        String username = request.getUsername();
        String challengeFromClient = extractChallengeFromClientData(request.getAuthenticationResponse().getClientDataJSON());

        // challengeを検証
        Optional<PasskeyChallenge> storedChallenge = passkeyChallengeRepository
                .findByUsernameAndChallenge(username, challengeFromClient);

        if (storedChallenge.isEmpty()) {
            return new PasskeyLoginFinishResponse(false, "Invalid challenge", null);
        }

        // 期限切れチェック
        if (storedChallenge.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            passkeyChallengeRepository.delete(storedChallenge.get());
            return new PasskeyLoginFinishResponse(false, "Challenge expired", null);
        }

        // credentialIdでPasskeyを検証
        String credentialId = request.getAuthenticationResponse().getId();
        Optional<Passkey> passkey = passkeyRepository.findByCredentialIdAndUsername(credentialId, username);
        
        if (passkey.isEmpty()) {
            return new PasskeyLoginFinishResponse(false, "Invalid credential", null);
        }

        // challengeを削除
        passkeyChallengeRepository.delete(storedChallenge.get());

        return new PasskeyLoginFinishResponse(true, "Login successful", username);
    }

    /**
     * 期限切れのchallengeを定期的にクリーンアップ
     * TODO Springのスケジューラー等でN時間に1回実行するように設定
     */
    @Transactional
    public void cleanupExpiredChallenges() {
        passkeyChallengeRepository.deleteExpiredChallenges(LocalDateTime.now());
    }

    /**
     * ランダムなchallengeを生成
     *
     * @return 生成されたchallenge文字列
     */
    private String generateChallenge() {
        byte[] challengeBytes = new byte[32];
        secureRandom.nextBytes(challengeBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(challengeBytes);
    }

    /**
     * ClientDataJSONからchallengeを抽出
     * Base64デコード後、JacksonでJSONをパースしてchallengeフィールドを取得
     *
     * @param clientDataJSON Base64エンコードされたClientDataJSON
     * @return 抽出されたchallenge文字列
     */
    private String extractChallengeFromClientData(String clientDataJSON) {
        try {
            // Base64 URLデコード
            String decodedClientData = new String(Base64.getUrlDecoder().decode(clientDataJSON));
            
            // JacksonでJSONをパース
            JsonNode clientDataNode = objectMapper.readTree(decodedClientData);
            
            // challengeフィールドを取得
            JsonNode challengeNode = clientDataNode.get("challenge");
            if (challengeNode == null || !challengeNode.isTextual()) {
                throw new RuntimeException("Challenge field not found or invalid in client data");
            }
            
            return challengeNode.asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract challenge from client data", e);
        }
    }
}
