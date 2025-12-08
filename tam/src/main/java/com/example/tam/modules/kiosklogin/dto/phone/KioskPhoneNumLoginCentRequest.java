package com.example.tam.modules.kiosklogin.dto.phone;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KioskPhoneNumLoginCentRequest {
    private String phoneNumber;
    
    private Integer storeId;
}
