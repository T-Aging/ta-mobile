package com.example.tam.modules.kiosklogin.dto.qr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KioskQrLoginCentResponse {
    private boolean login_success;

    private String message;
    
    private Integer userId;
    
    private String username;
}
