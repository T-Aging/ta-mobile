package com.example.tam.modules.custom;

import com.example.tam.modules.custom.entity.CustomMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomMenuRepository extends JpaRepository<CustomMenu, Long> {
    
    List<CustomMenu> findByUserId(Long userId);
    
    List<CustomMenu> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<CustomMenu> findByUserIdAndIsFavoriteTrueOrderByCreatedAtDesc(Long userId);
    
    List<CustomMenu> findByUserIdAndIsRecentTrue(Long userId);
    
    Optional<CustomMenu> findByIdAndUserId(Long id, Long userId);
    
    void deleteByIdAndUserId(Long id, Long userId);
}
