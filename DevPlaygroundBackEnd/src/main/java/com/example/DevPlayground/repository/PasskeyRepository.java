package com.example.DevPlayground.repository;

import com.example.DevPlayground.entity.Passkey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasskeyRepository extends JpaRepository<Passkey, Long> {
    
    List<Passkey> findByUsername(String username);
    
    Optional<Passkey> findByCredentialId(String credentialId);
    
    boolean existsByCredentialId(String credentialId);
    
    void deleteByUsername(String username);
}