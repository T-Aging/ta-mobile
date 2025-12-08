package com.example.tam.modules.kiosklogin.dto.qr;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KioskQrLoginCentRequest {
    private String qrCode;
    
    private Integer storeId;
}
