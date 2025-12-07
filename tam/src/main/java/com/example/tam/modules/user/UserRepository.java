package com.example.tam.modules.user;

import com.example.tam.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    // [추가] QR 코드로 사용자 찾기
    Optional<User> findByUserQr(String userQr);
}