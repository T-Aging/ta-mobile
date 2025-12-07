package com.example.tam.modules.order;

import com.example.tam.common.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    
    // [수정] orderHeader 객체 안의 orderId를 찾도록 직접 쿼리 작성
    @Query("SELECT od FROM OrderDetail od WHERE od.orderHeader.orderId = :orderId")
    List<OrderDetail> findByOrderId(@Param("orderId") Integer orderId);
}