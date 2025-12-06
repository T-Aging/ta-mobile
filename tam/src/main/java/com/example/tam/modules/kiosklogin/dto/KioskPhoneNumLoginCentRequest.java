package com.example.tam.modules.kiosklogin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KioskPhoneNumLoginCentRequest {
    private String phoneNumber;
    
    private Integer storeId;
}
