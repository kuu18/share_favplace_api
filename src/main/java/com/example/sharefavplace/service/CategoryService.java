package com.example.sharefavplace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sharefavplace.model.Category;
import com.example.sharefavplace.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository; 

  /**
   * 全カテゴリー取得
   * 
   * @return 全カテゴリー
   */
  public List<Category> findAllCategory() {
    return categoryRepository.findAll();
  }
}
