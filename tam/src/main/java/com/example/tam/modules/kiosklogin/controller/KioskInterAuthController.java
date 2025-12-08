package com.example.tam.modules.kiosklogin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tam.modules.kiosklogin.dto.phone.KioskPhoneNumLoginCentRequest;
import com.example.tam.modules.kiosklogin.dto.phone.KioskPhoneNumLoginCentResponse;
import com.example.tam.modules.kiosklogin.dto.qr.KioskQrLoginCentRequest;
import com.example.tam.modules.kiosklogin.dto.qr.KioskQrLoginCentResponse;
import com.example.tam.modules.kiosklogin.service.KioskInterAuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/inter/ta-kiosk/auth")
@RequiredArgsConstructor
public class KioskInterAuthController {
    private final KioskInterAuthService kioskInterAuthService;
    
    @PostMapping("/phone-num")
    public ResponseEntity<KioskPhoneNumLoginCentResponse> loginByPhoneNum(
        @RequestBody KioskPhoneNumLoginCentRequest request
    ){
        return ResponseEntity.ok(kioskInterAuthService.loginByPhone(request));
    }

    @PostMapping("/qr")
    public ResponseEntity<KioskQrLoginCentResponse> loginByQr(
        @RequestBody KioskQrLoginCentRequest request
    ){
        return ResponseEntity.ok(kioskInterAuthService.loginByQr(request));
    }
}
