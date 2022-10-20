package com.example.sharefavplace.service;

import org.springframework.stereotype.Service;

import com.example.sharefavplace.model.Favplace;

@Service
public interface FavplaceService {

  /**
   * Favplaces新規登録
   * 
   * @param favplace
   * @return Favplace
   */
  public Favplace saveFavplace(Favplace favplace);

  /**
   * favplaceにcategoryを追加するメソッド
   * 
   * @param favplaceId
   * @param categoryId
   */
  public Favplace addCategoryToFavplaces(Integer favplaceId, Iterable<Integer> categoryIds);

}
