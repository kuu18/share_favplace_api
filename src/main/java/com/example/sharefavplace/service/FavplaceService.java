package com.example.sharefavplace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sharefavplace.model.Category;
import com.example.sharefavplace.model.Favplace;
import com.example.sharefavplace.repository.CategoryRepository;
import com.example.sharefavplace.repository.FavplaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavplaceService {

  private final FavplaceRepository favplaceRepository;
  private final CategoryRepository categoryRepository;

  /**
   * Favplaces新規登録
   * 
   * @param favplace
   * @return Favplace
   */
  public Favplace saveFavplace(Favplace favplace) {
    return favplaceRepository.save(favplace);
  }

  /**
   * favplaceにcategoryを追加するメソッド
   * 
   * @param favplaceId
   * @param categoryId
   */
  public Favplace addCategoryToFavplaces(Integer favplaceId, Iterable<Integer> categoryIds) {
    Favplace favplace = favplaceRepository.findById(favplaceId).get();
    Iterable<Integer> iterableCategoryIds = categoryIds;
    List<Category> categories = categoryRepository.findAllById(iterableCategoryIds);
    categories.stream().forEach(category -> favplace.getCategories().add(category));
    return favplace;
  }
}
