package com.example.sharefavplace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sharefavplace.model.Favplace;

@Service
public interface FavplaceService {

  /**
   * idによるFavplace取得
   * 
   * @param id
   * @return Favplace
   */
  public Favplace getFavplaceById(Integer id);

  /**
   * user_idによるFavplaces取得
   * 
   * @param userId
   * @return List<Favplace>
   */
  public List<Favplace> getFavplacesByUserId(Integer userId);

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
