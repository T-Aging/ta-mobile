package com.example.tam.modules.order;

import com.example.tam.common.entity.OrderOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderOptionRepository extends JpaRepository<OrderOption, Integer> {

    // [수정] oo.orderDetail.orderDetailId -> oo.orderDetail.id
    // (OrderDetail 엔티티의 실제 필드명이 'id'입니다)
    @Query("SELECT oo FROM OrderOption oo WHERE oo.orderDetail.id = :orderDetailId")
    List<OrderOption> findByOrderDetailId(@Param("orderDetailId") Integer orderDetailId);
}