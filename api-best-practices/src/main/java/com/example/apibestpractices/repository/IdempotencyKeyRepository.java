package com.example.apibestpractices.repository;

import com.example.apibestpractices.model.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, String> {
    
    Optional<IdempotencyKey> findByKeyValueAndExpiresAtAfter(String keyValue, OffsetDateTime now);
    
    @Modifying
    @Query("DELETE FROM IdempotencyKey ik WHERE ik.expiresAt < :now")
    void deleteExpiredKeys(@Param("now") OffsetDateTime now);
    
    @Query("SELECT COUNT(ik) FROM IdempotencyKey ik WHERE ik.expiresAt > :now")
    long countActiveKeys(@Param("now") OffsetDateTime now);
}
