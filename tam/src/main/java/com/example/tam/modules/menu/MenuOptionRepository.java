package com.example.tam.modules.menu;

import com.example.tam.common.entity.MenuOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenuOptionRepository extends JpaRepository<MenuOption, Integer> {
    List<MenuOption> findByOptionClass(String optionClass);
}
