package com.example.tam.modules.order;

import com.example.tam.common.entity.OrderOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderOptionRepository extends JpaRepository<OrderOption, Integer> {
    List<OrderOption> findByOrderDetailId(Integer orderDetailId);
}
