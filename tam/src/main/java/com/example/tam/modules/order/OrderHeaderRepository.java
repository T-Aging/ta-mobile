package com.example.tam.modules.order;

import com.example.tam.common.entity.OrderHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Integer> {
    // [수정] OrderDatetime -> OrderDateTime (대소문자 T 주의)
    List<OrderHeader> findByUserIdOrderByOrderDateTimeDesc(Integer userId);
    
    // [수정] OrderDatetime -> OrderDateTime
    List<OrderHeader> findByUserIdAndOrderDateBetweenOrderByOrderDateTimeDesc(
        Integer userId, LocalDate startDate, LocalDate endDate);
}