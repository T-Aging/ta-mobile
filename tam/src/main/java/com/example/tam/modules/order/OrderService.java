package com.example.tam.modules.order;

import com.example.tam.dto.OrderDto;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class OrderService {
    public List<OrderDto.Response> getOrdersByDateRange(Integer userId, String from, String to) {
        return Collections.emptyList();
    }
    public List<OrderDto.Response> getAllOrders(Integer userId) {
        return Collections.emptyList();
    }
    public OrderDto.Response getOrderDetail(Integer userId, Integer orderId) {
        return new OrderDto.Response();
    }
    public OrderDto.Response createOrder(Integer userId, OrderDto.CreateRequest request) {
        return new OrderDto.Response();
    }
    public void createCustomMenuFromOrder(Integer userId, Integer orderId, String customName) {
    }
}