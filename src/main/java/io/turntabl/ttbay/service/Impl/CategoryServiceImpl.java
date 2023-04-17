package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.enums.Category;
import io.turntabl.ttbay.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Override
    public List<Category> getAllCategories(){
        return List.of(Category.values());
    }
}
