package com.example.sharefavplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sharefavplace.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
