package com.example.tam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // JPA Auditing을 활성화하여 @CreatedDate, @LastModifiedDate 등이 작동하도록 설정
}
