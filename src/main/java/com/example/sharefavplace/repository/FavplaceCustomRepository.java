package com.example.sharefavplace.repository;

import java.util.List;

import com.example.sharefavplace.model.Favplace;

public interface FavplaceCustomRepository {

  /**
   * user_idによるFavplaces取得（ページネーション）
   * 
   * @param userId
   * @return List<Favplace>
   */
  public List<Favplace> selectFavplacesbyUserId(Integer userId, final int pPageIndex, final int pCountPerPage);

  /**
   * user_idによるFavplace数取得
   * 
   * @param userId
   * @return Favplace数
   */
  public long getUsersFavplacesCount(Integer userId);
}
