package com.example.tam.repository;

import com.example.tam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(String kakaoId);
    Optional<User> findByEmail(String email);
    boolean existsByKakaoId(String kakaoId);
    boolean existsByEmail(String email);
}
