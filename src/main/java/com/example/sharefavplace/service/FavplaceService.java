package com.example.sharefavplace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sharefavplace.model.Favplace;

@Service
public interface FavplaceService {

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
   * ユーザーの予定済みFavplace一覧取得
   * 
   * @param userId
   * @return List<Favplace>
   */
  public List<Favplace> getScheduledFavplaces(Integer userId);

  /**
   * Favplaces新規登録
   * 
   * @param favplace
   * @return Favplace
   */
  public Favplace saveFavplace(Favplace favplace);

}
