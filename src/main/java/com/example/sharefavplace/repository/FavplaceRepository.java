package com.example.sharefavplace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.sharefavplace.model.Favplace;

@Repository
public interface FavplaceRepository extends JpaRepository<Favplace, Integer>, FavplaceCustomRepository{

  /**
   * user_idによるFavplace全件取得
   * 
   * @param userId
   * @return List<Favplace>
   */
  public List<Favplace> findAllByUserId(Integer userId);

}
