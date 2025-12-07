package com.example.tam.modules.auth;

import com.example.tam.common.entity.Kakao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface KakaoRepository extends JpaRepository<Kakao, String> {
    // [수정] findByUserId -> findByUserUserId
    // (Kakao.user 객체 안의 User.userId 필드를 찾도록 명시)
    Optional<Kakao> findByUserUserId(Integer userId);
}