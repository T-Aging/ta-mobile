package com.example.tam.modules.order;

import com.example.tam.dto.ApiResponse;
import com.example.tam.dto.OrderDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/t-age/users/{userid}/order")
@RequiredArgsConstructor
@Tag(name = "주문", description = "주문 관리 API")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 내역 조회 (기본/상세/영수증)")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> getOrders(
            @PathVariable Long userid,
            @RequestParam(required = false) Long orderId,
            Authentication auth) {
        
        validateUser(userid, auth);
        
        if (orderId != null) {
            var order = orderService.getOrderDetail(userid, orderId);
            return ResponseEntity.ok(ApiResponse.success("주문 상세 조회 성공", order));
        }
        
        var orders = orderService.getAllOrders(userid);
        return ResponseEntity.ok(ApiResponse.success("주문 내역 조회 성공", orders));
    }

    @Operation(summary = "기간 설정 및 기간 내 주문 조회")
    @PostMapping("/date-set")
    public ResponseEntity<ApiResponse<?>> getOrdersByDate(
            @PathVariable Long userid,
            @Valid @RequestBody OrderDto.DateRangeRequest request,
            Authentication auth) {
        
        validateUser(userid, auth);
        
        var orders = orderService.getOrdersByDateRange(
                userid, request.getFromDate(), request.getToDate());
        return ResponseEntity.ok(ApiResponse.success("기간 내 주문 조회 성공", orders));
    }

    @Operation(summary = "주문 생성")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderDto.Response>> createOrder(
            @PathVariable Long userid,
            @Valid @RequestBody OrderDto.CreateRequest request,
            Authentication auth) {
        
        validateUser(userid, auth);
        
        OrderDto.Response response = orderService.createOrder(userid, request);
        return ResponseEntity.ok(ApiResponse.success("주문 생성 성공", response));
    }

    @Operation(summary = "주문 내역으로 커스텀 메뉴 생성")
    @PostMapping("/cus-gen")
    public ResponseEntity<ApiResponse<?>> createCustomFromOrder(
            @PathVariable Long userid,
            @Valid @RequestBody OrderDto.CreateFromCustomRequest request,
            Authentication auth) {
        
        validateUser(userid, auth);
        
        var response = orderService.createOrderFromCustomMenu(
                userid, request.getCustomMenuId(), request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("커스텀 메뉴 기반 주문 생성 성공", response));
    }

    private void validateUser(Long userid, Authentication auth) {
        Long requestUserId = (Long) auth.getPrincipal();
        if (!userid.equals(requestUserId)) {
            throw new RuntimeException("본인의 주문만 조회할 수 있습니다");
        }
    }
}
