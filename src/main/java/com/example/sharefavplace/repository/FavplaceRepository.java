package com.example.sharefavplace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sharefavplace.model.Favplace;

public interface FavplaceRepository extends JpaRepository<Favplace, Integer>, FavplaceCustomRepository{

  /**
   * user_idによるFavplace全件取得
   * 
   * @param userId
   * @return List<Favplace>
   */
  public List<Favplace> findAllByUserId(Integer userId);

}
