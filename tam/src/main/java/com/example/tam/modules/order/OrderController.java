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

import java.util.List;

@RestController
@RequestMapping("/t-age/users/{userid}/orders")
@RequiredArgsConstructor
@Tag(name = "주문", description = "주문 내역 및 영수증 관리 API")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 내역 조회 (기간 필터링)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDto.Response>>> getOrders(
            @PathVariable Integer userid,
            @Parameter(description = "시작 날짜") @RequestParam(required = false) String fromDate,
            @Parameter(description = "종료 날짜") @RequestParam(required = false) String toDate) {
        
        List<OrderDto.Response> orders;
        if (fromDate != null && toDate != null) {
            orders = orderService.getOrdersByDateRange(userid, fromDate, toDate);
        } else {
            orders = orderService.getAllOrders(userid);
        }
        return ResponseEntity.ok(ApiResponse.success("주문 내역 조회 성공", orders));
    }

    @Operation(summary = "주문 상세 조회")
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDto.Response>> getOrderDetail(
            @PathVariable Integer userid,
            @PathVariable Integer orderId) {
        OrderDto.Response order = orderService.getOrderDetail(userid, orderId);
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

    @Operation(summary = "과거 주문으로 커스텀 메뉴 생성")
    @PostMapping("/{orderId}/to-custom")
    public ResponseEntity<ApiResponse<Void>> createCustomFromOrder(
            @PathVariable Integer userid,
            @PathVariable Integer orderId,
            @RequestParam String customName) {
        orderService.createCustomMenuFromOrder(userid, orderId, customName);
        return ResponseEntity.ok(ApiResponse.success("주문 기반 커스텀 메뉴 생성 성공", null));
    }
}