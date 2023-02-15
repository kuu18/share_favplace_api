package com.example.sharefavplace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sharefavplace.model.Favplace;

@Service
public interface FavplaceService {

  /**
   * idによるFavplace取得（1件）
   * 
   * @param id
   * @return Favplace
   */
  public Favplace getFavplaceById(Integer userId);

  /**
   * ユーザーのFavplaces一覧取得（ページネーション）
   * 
   * @param userId
   * @return List<Favplace>
   */
  public List<Favplace> getFavplacesByUserId(Integer userId, final int pPageIndex, final int pCountPerPage);

  /**
   * ユーザーのFavplace総数取得
   * 
   * @param userId
   * @return Favplace数
   */
  public Long getUsersFavplacesCount(Integer userId);

  /**
   * Favplace新規登録
   * 
   * @param favplace
   * @return Favplace
   */
  public Favplace saveFavplace(Favplace favplace);

  /**
   * Favplace更新
   * 
   * @param favplace
   * @return Favplace
   */
  public Favplace updateFavplace(Favplace favplace);

  /**
   * Favplaceのスケジュール更新
   * 
   * @param favplace
   * @return Favplace
   */
  public Favplace updateFavplaceSchedule(Favplace favplace);

}
