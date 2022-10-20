package com.example.sharefavplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.sharefavplace.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
