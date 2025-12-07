package com.example.tam.modules.custom;

// import com.example.tam.common.entity.CustomDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomDetailRepository { // extends JpaRepository<CustomDetail, Integer> {
    // List<CustomDetail> findByCustomId(Integer customId);
}