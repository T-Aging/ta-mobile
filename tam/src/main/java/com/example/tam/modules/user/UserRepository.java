package com.example.tam.modules.user;

import com.example.tam.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}

// modules/auth/KakaoRepository.java
package com.example.tam.modules.auth;

import com.example.tam.common.entity.Kakao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface KakaoRepository extends JpaRepository<Kakao, String> {
    Optional<Kakao> findByUserId(Integer userId);
}
