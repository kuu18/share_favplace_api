package com.example.sharefavplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.sharefavplace.model.Favplace;

@Repository
public interface FavplaceRepository extends JpaRepository<Favplace, Integer> {
}
