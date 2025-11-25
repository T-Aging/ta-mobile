package com.example.tam.modules.login;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LoginService {

    private final LoginRepository loginRepository;

    // @RequiredArgsConstructor 대신 직접 생성자를 만들어줍니다.
    public LoginService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @Transactional
    public String login(String username, String password) {
        // 실제 로그인 로직이 들어갈 자리
        return "temp-jwt-token-string"; 
    }
}