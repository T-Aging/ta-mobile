package com.example.tam.modules.order;

import com.example.tam.dto.ApiResponse;
import com.example.tam.dto.OrderDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/t-age/users/{userid}/order")
@RequiredArgsConstructor
@Tag(name = "주문", description = "주문 관리 API")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 내역 조회(기본) / 주문 상세 / 모바일 영수증 조회(이력)")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> getOrders(
            @PathVariable Integer userid,
            @RequestParam(required = false) Integer orderId) {
        
        if (orderId != null) {
            var order = orderService.getOrderDetail(userid, orderId);
            return ResponseEntity.ok(ApiResponse.success("주문 상세 조회 성공", order));
        }
        
        var orders = orderService.getAllOrders(userid);
        return ResponseEntity.ok(ApiResponse.success("주문 내역 조회 성공", orders));
    }

    @Operation(summary = "기간 설정 / 기간 내 최신 순 조회")
    @PostMapping("/date-set")
    public ResponseEntity<ApiResponse<?>> getOrdersByDate(
            @PathVariable Integer userid,
            @Valid @RequestBody OrderDto.DateRangeRequest request) {
        
        var orders = orderService.getOrdersByDateRange(
                userid, request.getFromDate(), request.getToDate());
        return ResponseEntity.ok(ApiResponse.success("기간 내 주문 조회 성공", orders));
    }

    @Operation(summary = "주문 생성")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderDto.Response>> createOrder(
            @PathVariable Integer userid,
            @Valid @RequestBody OrderDto.CreateRequest request) {
        
        OrderDto.Response response = orderService.createOrder(userid, request);
        return ResponseEntity.ok(ApiResponse.success("주문 생성 성공", response));
    }

    @Operation(summary = "커스텀 메뉴 생성 / 주문 내역 중 선택")
    @PostMapping("/cus-gen")
    public ResponseEntity<ApiResponse<?>> createCustomFromOrder(
            @PathVariable Integer userid,
            @RequestParam Integer orderId) {
        
        // TODO: 주문 내역을 기반으로 커스텀 메뉴 생성 로직 구현
        return ResponseEntity.ok(ApiResponse.success("주문 기반 커스텀 메뉴 생성 성공", null));
    }
}
