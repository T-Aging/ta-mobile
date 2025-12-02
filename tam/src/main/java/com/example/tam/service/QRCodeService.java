package com.example.tam.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class QrCodeService {
    
    private static final int QR_CODE_SIZE = 300;
    private static final String IMAGE_FORMAT = "PNG";
    private static final long QR_VALIDITY_SECONDS = 180;
    private static final String SECRET_KEY = "tam-qr-secret-2025";
    
    private final Map<String, QrTokenInfo> qrTokenCache = new ConcurrentHashMap<>();

    public QrCodeResponse generateUserQrCode(Long userId) {
        try {
            String token = generateSecureToken(userId);
            long expiryTime = Instant.now().getEpochSecond() + QR_VALIDITY_SECONDS;
            
            String qrContent = String.format("TAM:QR:%s", token);
            
            QrTokenInfo tokenInfo = new QrTokenInfo(userId, token, expiryTime);
            qrTokenCache.put(token, tokenInfo);
            
            cleanupExpiredTokens();
            
            String qrCodeBase64 = generateQrCodeBase64(qrContent);
            
            log.info("동적 QR 코드 생성 완료 - userId: {}, token: {}, expiresAt: {}", 
                    userId, token.substring(0, 8) + "...", expiryTime);
            
            return QrCodeResponse.builder()
                    .qrCodeBase64(qrCodeBase64)
                    .token(token)
                    .userId(userId)
                    .expiresAt(expiryTime)
                    .validitySeconds(QR_VALIDITY_SECONDS)
                    .build();
                    
        } catch (Exception e) {
            log.error("QR 코드 생성 실패 - userId: {}", userId, e);
            throw new RuntimeException("QR 코드 생성에 실패했습니다", e);
        }
    }
    
    public QrValidationResult validateQrCode(String token) {
        QrTokenInfo tokenInfo = qrTokenCache.get(token);
        
        if (tokenInfo == null) {
            return QrValidationResult.builder()
                    .valid(false)
                    .message("유효하지 않은 QR 코드입니다")
                    .build();
        }
        
        long currentTime = Instant.now().getEpochSecond();
        if (currentTime > tokenInfo.getExpiryTime()) {
            qrTokenCache.remove(token);
            return QrValidationResult.builder()
                    .valid(false)
                    .message("QR 코드가 만료되었습니다")
                    .build();
        }
        
        return QrValidationResult.builder()
                .valid(true)
                .userId(tokenInfo.getUserId())
                .message("유효한 QR 코드입니다")
                .remainingSeconds(tokenInfo.getExpiryTime() - currentTime)
                .build();
    }
    
    private String generateSecureToken(Long userId) {
        try {
            String rawData = String.format("%d:%d:%s:%s", 
                    userId, 
                    Instant.now().toEpochMilli(),
                    UUID.randomUUID().toString(),
                    SECRET_KEY);
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawData.getBytes());
            
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("토큰 생성 실패", e);
        }
    }
    
    private void cleanupExpiredTokens() {
        long currentTime = Instant.now().getEpochSecond();
        qrTokenCache.entrySet().removeIf(entry -> 
            currentTime > entry.getValue().getExpiryTime());
    }
    
    private String generateQrCodeBase64(String content) throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
            content, 
            BarcodeFormat.QR_CODE, 
            QR_CODE_SIZE, 
            QR_CODE_SIZE, 
            hints
        );

        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(qrImage, IMAGE_FORMAT, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        
        return "data:image/png;base64," + base64Image;
    }
    
    @lombok.Data
    @lombok.AllArgsConstructor
    private static class QrTokenInfo {
        private Long userId;
        private String token;
        private Long expiryTime;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class QrCodeResponse {
        private String qrCodeBase64;
        private String token;
        private Long userId;
        private Long expiresAt;
        private Long validitySeconds;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class QrValidationResult {
        private boolean valid;
        private Long userId;
        private String message;
        private Long remainingSeconds;
    }
}
