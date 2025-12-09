package com.example.tam.modules.kiosklogin.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.tam.common.entity.User;
import com.example.tam.modules.kiosklogin.dto.phone.KioskPhoneNumLoginCentRequest;
import com.example.tam.modules.kiosklogin.dto.phone.KioskPhoneNumLoginCentResponse;
import com.example.tam.modules.kiosklogin.dto.qr.KioskQrLoginCentRequest;
import com.example.tam.modules.kiosklogin.dto.qr.KioskQrLoginCentResponse;
import com.example.tam.modules.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KioskInterAuthService {
    private final UserRepository userRepository;

    public KioskPhoneNumLoginCentResponse loginByPhone(KioskPhoneNumLoginCentRequest request){
        String phoneNum=request.getPhoneNumber();

        if(phoneNum == null || phoneNum.isBlank()){
            return KioskPhoneNumLoginCentResponse.builder()
            .login_success(false)
            .message("INVALID_PHONE_NUMBER")
            .build();
        }

        String masked = phoneNum.replaceAll("[^0-9]", "");

        Optional<User> userOption = userRepository.findByPhoneNumber(masked);

        if(userOption.isEmpty()){
            return KioskPhoneNumLoginCentResponse.builder()
            .login_success(false)
            .message("NOT_FOUND")
            .build();
        }

        User user = userOption.get();

        return KioskPhoneNumLoginCentResponse.builder()
                .login_success(true)
                .message("SUCCESS")
                .userId(user.getUserId())
                .username(user.getUsername())
                .maskedPhone(maskPhone(user.getPhoneNumber()))
                .build();
    }

    private String maskPhone(String phone){
        if(phone == null) return null;
        String digits=phone.replaceAll("[^0-9]", "");

        if(digits.length() <7) return phone;

        return digits.substring(0,3) + "-****-" + digits.substring(digits.length() - 4);
    }

    public KioskQrLoginCentResponse loginByQr(KioskQrLoginCentRequest request){
        String qrCode=request.getQrCode();

        if(qrCode == null || qrCode.isBlank()){
            return KioskQrLoginCentResponse.builder()
            .login_success(false)
            .message("INVALID_QR_CODE")
            .build();
        }

        Optional<User> userOption = userRepository.findByUserQr(qrCode);

        if(userOption.isEmpty()){
            return KioskQrLoginCentResponse.builder()
            .login_success(false)
            .message("NOT_FOUND")
            .build();
        }

        User user = userOption.get();


        return KioskQrLoginCentResponse.builder()
                .login_success(true)
                .message("SUCCESS")
                .userId(user.getUserId())
                .username(user.getUsername())
                .build();
    }
}
