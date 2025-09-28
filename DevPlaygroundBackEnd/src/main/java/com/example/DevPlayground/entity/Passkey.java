package com.example.DevPlayground.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "passkeys")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Passkey {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String credentialId;
    
    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String publicKey;
    
    @Column(nullable = false)
    private Long signatureCount;
    
    @Column(nullable = false)
    private String attestationObject;
    
    @Column(nullable = false)
    private String clientDataJSON;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}