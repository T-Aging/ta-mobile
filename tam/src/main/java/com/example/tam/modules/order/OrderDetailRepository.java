package com.example.tam.modules.order;

import com.example.tam.common.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    
    // [수정] od.orderHeader.orderId -> od.orderHeader.id
    // (OrderHeader 엔티티의 실제 필드명이 'id'입니다)
    @Query("SELECT od FROM OrderDetail od WHERE od.orderHeader.id = :orderId")
    List<OrderDetail> findByOrderId(@Param("orderId") Integer orderId);
}