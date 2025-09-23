package com.example.DevPlayground.controller;

import com.example.DevPlayground.service.PasskeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/passkey")
public class PasskeyLoginController {
    private PasskeyService passkeyService;

    @Autowired
    public PasskeyLoginController (PasskeyService passkeyService) {
        this.passkeyService = passkeyService;
    }

    @PostMapping("/register-start")
    public PasskeyService.PasskeyRegistrationStartResponse registerPasskeyStart(@RequestBody PasskeyRegistrationStartRequest request) {
        return passkeyService.registerPasskeyStart(request.username);
    }

    @PostMapping("/register-passkey")
    public String registerPasskey(@RequestBody PasskeyRegistrationRequest request) {
        System.out.println("Registering passkey for user: " + request.username());
        return "Passkey registered for user: " + request.username();
    }

    // TODO あとでuserIdにする
    public record PasskeyLoginRequest(String username) {}
    public record PasskeyRegistrationRequest(String username) {}
    public record PasskeyRegistrationStartRequest(String username) {}
}
