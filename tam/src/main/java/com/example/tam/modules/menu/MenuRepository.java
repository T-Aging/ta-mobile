package com.example.tam.modules.menu;

import com.example.tam.common.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {
    // [수정] findByMenuNameContaining -> findByNameContaining
    // Menu 엔티티의 실제 필드명이 'name'이므로 메서드 이름도 그에 맞춰야 합니다.
    List<Menu> findByNameContaining(String keyword);
}