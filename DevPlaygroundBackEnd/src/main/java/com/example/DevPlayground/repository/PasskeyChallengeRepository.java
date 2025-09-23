package com.example.DevPlayground.repository;

import com.example.DevPlayground.entity.PasskeyChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasskeyChallengeRepository extends JpaRepository<PasskeyChallenge, Long> {
    
    Optional<PasskeyChallenge> findByUsernameAndChallenge(String username, String challenge);
    
    @Modifying
    @Query("DELETE FROM PasskeyChallenge pc WHERE pc.expiresAt < :now")
    void deleteExpiredChallenges(LocalDateTime now);
    
    void deleteByUsername(String username);
}