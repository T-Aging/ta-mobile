package com.example.tam.modules.order;

import com.example.tam.common.entity.OrderOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderOptionRepository extends JpaRepository<OrderOption, Integer> {

    // [수정] orderDetail 객체 안의 orderDetailId를 찾도록 직접 쿼리 작성
    @Query("SELECT oo FROM OrderOption oo WHERE oo.orderDetail.orderDetailId = :orderDetailId")
    List<OrderOption> findByOrderDetailId(@Param("orderDetailId") Integer orderDetailId);
}