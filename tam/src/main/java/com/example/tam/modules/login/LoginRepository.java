package com.example.tam.modules.login;

// [수정] 아래 import가 modules.user.User로 되어있으면 common.entity.User로 바꿔주세요
import com.example.tam.common.entity.User; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}