package com.example.tam.modules.option.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tam.common.entity.OptionGroup;

@Repository
public interface OptionGroupRepository extends JpaRepository<OptionGroup, Integer> {

    
} 