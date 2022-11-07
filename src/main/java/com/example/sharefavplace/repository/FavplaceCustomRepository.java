package com.example.sharefavplace.repository;

import java.util.List;

import com.example.sharefavplace.model.Favplace;

public interface FavplaceCustomRepository {

  /**
   * idによるFavplace取得
   * 
   * @param id
   * @return Favplace
   */
  public Favplace selectFavplacebyId(Integer id);

  /**
   * user_idによるFavplace全件取得
   * 
   * @param userId
   * @return List<Favplace>
   */
  public List<Favplace> selectAllFavplacesbyUserId(Integer userId);
}
