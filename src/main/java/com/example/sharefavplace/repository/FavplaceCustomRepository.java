package com.example.sharefavplace.repository;

import java.util.List;

import com.example.sharefavplace.model.Favplace;

public interface FavplaceCustomRepository {

  /**
   * user_idによるFavplace一覧取得（ページネーション）
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

  /**
   * idによるFavplace取得（1件）
   * 
   * @param userId
   * @return Favplace
   */
  public Favplace selectFavplacesbyId(Integer id);

  /**
   * Favplaceのスケジュール更新
   * 
   * @param favplace
   * @return Favplace
   */
  public Favplace updateFavplaceSchedule(Favplace favplace);

  /**
   * Favplace更新
   * 
   * @param favplace
   * @return Favplace
   */
  public void updateFavplace(Favplace favplace);

}
