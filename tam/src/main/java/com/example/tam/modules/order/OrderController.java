package com.example.tam.modules.order;

import com.example.tam.dto.ApiResponse;
import com.example.tam.dto.OrderDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/t-age/users/{userid}/orders")
@RequiredArgsConstructor
@Tag(name = "주문", description = "주문 내역 및 영수증 관리 API")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 내역 조회 및 검색", description = "기간 파라미터(fromDate, toDate)가 없으면 전체 조회, 있으면 기간 내 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getOrders(
            @PathVariable Integer userid,
            @Parameter(description = "시작 날짜 (yyyy-MM-dd)") @RequestParam(required = false) String fromDate,
            @Parameter(description = "종료 날짜 (yyyy-MM-dd)") @RequestParam(required = false) String toDate) {
        
        if (fromDate != null && toDate != null) {
            var orders = orderService.getOrdersByDateRange(userid, fromDate, toDate);
            return ResponseEntity.ok(ApiResponse.success("기간 내 주문 조회 성공", orders));
        }
        
        var orders = orderService.getAllOrders(userid);
        return ResponseEntity.ok(ApiResponse.success("전체 주문 내역 조회 성공", orders));
    }

    @Operation(summary = "주문 상세 조회 (모바일 영수증)")
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<?>> getOrderDetail(
            @PathVariable Integer userid,
            @PathVariable Integer orderId) {
        var order = orderService.getOrderDetail(userid, orderId);
        return ResponseEntity.ok(ApiResponse.success("주문 상세 조회 성공", order));
    }

    @Operation(summary = "주문 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto.Response>> createOrder(
            @PathVariable Integer userid,
            @Valid @RequestBody OrderDto.CreateRequest request) {
        OrderDto.Response response = orderService.createOrder(userid, request);
        return ResponseEntity.ok(ApiResponse.success("주문 생성 성공", response));
    }

    @Operation(summary = "주문 내역을 기반으로 나만의 메뉴(커스텀) 생성")
    @PostMapping("/{orderId}/custom")
    public ResponseEntity<ApiResponse<?>> createCustomFromOrder(
            @PathVariable Integer userid,
            @PathVariable Integer orderId) {
        // OrderService 혹은 CustomService에 해당 로직 구현 필요
        // 예: orderService.createCustomFromOrder(userid, orderId);
        return ResponseEntity.ok(ApiResponse.success("주문 기반 커스텀 메뉴 생성 성공", null));
    }
}