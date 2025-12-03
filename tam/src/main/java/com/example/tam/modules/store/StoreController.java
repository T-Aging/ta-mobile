package com.example.tam.modules.store;

import com.example.tam.common.entity.Store;
import com.example.tam.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/t-age/store")
@RequiredArgsConstructor
@Tag(name = "매장", description = "매장 조회 API")
public class StoreController {

    private final StoreRepository storeRepository;

    @Operation(summary = "전체 매장 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Store>>> getAllStores() {
        List<Store> stores = storeRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("매장 조회 성공", stores));
    }

    @Operation(summary = "매장 상세 조회")
    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<Store>> getStoreDetail(@PathVariable Integer storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다"));
        return ResponseEntity.ok(ApiResponse.success("매장 상세 조회 성공", store));
    }
}
