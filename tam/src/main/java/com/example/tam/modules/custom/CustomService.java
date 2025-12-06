package com.example.tam.modules.custom;

import com.example.tam.dto.CustomMenuDto;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class CustomService {
    public List<CustomMenuDto.Response> getCustomMenus(Integer userId, String keyword) {
        return Collections.emptyList();
    }
    public List<CustomMenuDto.Response> getAllCustomMenus(Integer userId) {
        return Collections.emptyList();
    }
    public CustomMenuDto.Response getCustomMenuDetail(Integer userId, Integer customId) {
        return new CustomMenuDto.Response();
    }
    public CustomMenuDto.Response createCustomMenu(Integer userId, CustomMenuDto.CreateRequest request) {
        return new CustomMenuDto.Response();
    }
    public CustomMenuDto.Response updateCustomMenu(Integer userId, CustomMenuDto.UpdateRequest request) {
        return new CustomMenuDto.Response();
    }
    public void deleteCustomMenu(Integer userId, Integer customId) {
    }
}