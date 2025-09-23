package com.example.DevPlayground.controller;

import com.example.DevPlayground.dto.PasskeyRegistrationFinishRequest;
import com.example.DevPlayground.dto.PasskeyRegistrationFinishResponse;
import com.example.DevPlayground.dto.PasskeyRegistrationStartResponse;
import com.example.DevPlayground.dto.PasskeyLoginStartResponse;
import com.example.DevPlayground.dto.PasskeyLoginFinishRequest;
import com.example.DevPlayground.dto.PasskeyLoginFinishResponse;
import com.example.DevPlayground.service.PasskeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final PasskeyService passkeyService;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, PasskeyService passkeyService) {
        this.authenticationManager = authenticationManager;
        this.passkeyService = passkeyService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
        Authentication authenticationResponse =
                this.authenticationManager.authenticate(authenticationRequest);
        
        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
        
        // Save authentication to session
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        
        LoginResponse response = new LoginResponse(authenticationResponse.isAuthenticated(), authenticationResponse.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutManual(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/current-user")
    public ResponseEntity<UserInfo> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            System.out.println("Current user authorities: " + authentication.getAuthorities());
            String[] roles = authentication.getAuthorities().stream()
                    .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                    .toArray(String[]::new);
            System.out.println("Processed roles: " + java.util.Arrays.toString(roles));
            UserInfo userInfo = new UserInfo(authentication.getName(), authentication.getAuthorities().toString(), roles);
            return ResponseEntity.ok(userInfo);
        }
        
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/auth/passkey/register/start")
    public ResponseEntity<PasskeyRegistrationStartResponse> startPasskeyRegistration(@RequestParam String username) {
        try {
            System.out.println("Starting passkey registration for user: " + username);
            PasskeyRegistrationStartResponse response = passkeyService.startRegistration(username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error starting passkey registration: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/auth/passkey/register/finish")
    public ResponseEntity<PasskeyRegistrationFinishResponse> finishPasskeyRegistration(@RequestBody PasskeyRegistrationFinishRequest request) {
        try {
            System.out.println("Finishing passkey registration for user: " + request.getUsername());
            PasskeyRegistrationFinishResponse response = passkeyService.finishRegistration(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error finishing passkey registration: " + e.getMessage());
            PasskeyRegistrationFinishResponse errorResponse = new PasskeyRegistrationFinishResponse(false, "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/auth/passkey/login/start")
    public ResponseEntity<PasskeyLoginStartResponse> startPasskeyLogin(@RequestParam String username) {
        try {
            System.out.println("Starting passkey login for user: " + username);
            PasskeyLoginStartResponse response = passkeyService.startLogin(username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error starting passkey login: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/auth/passkey/login/finish")
    public ResponseEntity<PasskeyLoginFinishResponse> finishPasskeyLogin(@RequestBody PasskeyLoginFinishRequest request, HttpServletRequest httpRequest) {
        try {
            System.out.println("Finishing passkey login for user: " + request.getUsername());
            PasskeyLoginFinishResponse response = passkeyService.finishLogin(request);
            
            if (response.isSuccess()) {
                // Spring Security認証を設定
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(response.getUsername(), null, null);
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                // セッションに保存
                HttpSession session = httpRequest.getSession(true);
                session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error finishing passkey login: " + e.getMessage());
            PasskeyLoginFinishResponse errorResponse = new PasskeyLoginFinishResponse(false, "Login failed: " + e.getMessage(), null);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    public record LoginRequest(String username, String password) {
    }
    
    public record LoginResponse(boolean success, String username) {
    }
    
    public record UserInfo(String username, String authorities, String[] roles) {
    }
}
