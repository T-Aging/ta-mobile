package com.example.tam.modules.push;

import com.example.tam.common.entity.PushHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PushHistoryRepository extends JpaRepository<PushHistory, Integer> {
    List<PushHistory> findByUserIdOrderBySentAtDesc(Integer userId);
    List<PushHistory> findByUserIdAndPushType(Integer userId, String pushType);
}
