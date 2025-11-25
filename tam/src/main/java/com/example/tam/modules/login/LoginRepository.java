package com.example.tam.modules.login;

import com.example.tam.modules.user.User; // 방금 만든 User 파일을 가져옵니다.
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}