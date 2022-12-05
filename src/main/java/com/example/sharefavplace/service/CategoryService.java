package com.example.sharefavplace.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.sharefavplace.model.Category;

@Service
public interface CategoryService {

  /**
   * 全カテゴリー取得
   * 
   * @return 全カテゴリー
   */
  public List<Category> findAllCategory();

  /**
   * Idによるカテゴリー取得
   * 
   * @param id
   * @return カテゴリー
   */
  public Optional<Category> findById(Integer id);

}
