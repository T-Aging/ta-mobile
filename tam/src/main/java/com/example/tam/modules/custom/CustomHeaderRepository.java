package com.example.tam.modules.custom;

// import com.example.tam.common.entity.CustomHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomHeaderRepository { // extends JpaRepository<CustomHeader, Integer> {
    // List<CustomHeader> findByUserId(Integer userId);
    // List<CustomHeader> findByUserIdOrderByCustomIdDesc(Integer userId);
} 