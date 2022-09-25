package com.example.sharefavplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sharefavplace.model.Favplace;

public interface FavplaceRepository extends JpaRepository<Favplace, Integer> {
}
