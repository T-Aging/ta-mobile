package com.example.tam.modules.order;

import com.example.tam.common.entity.OrderHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Integer> {
    List<OrderHeader> findByUserIdOrderByOrderDatetimeDesc(Integer userId);
    List<OrderHeader> findByUserIdAndOrderDateBetweenOrderByOrderDatetimeDesc(
        Integer userId, LocalDate startDate, LocalDate endDate);
}
