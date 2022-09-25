package com.example.sharefavplace.api;

import java.util.List;

import com.example.sharefavplace.model.Category;
import com.example.sharefavplace.service.CategoryService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryResource {

  private final CategoryService categoryService;
  
  /**
   * 全カテゴリー取得
   * 
   * @return 全カテゴリー
   */
  @GetMapping
  public ResponseEntity<List<Category>> getCategories() {
    return ResponseEntity.ok().body(categoryService.findAllCategory());
  }
}