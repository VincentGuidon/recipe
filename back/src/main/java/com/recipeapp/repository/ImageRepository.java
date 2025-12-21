package com.recipeapp.repository;

import com.recipeapp.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, String> {
    List<Image> findByRecipeId(String recipeId);
    List<Image> findByIsActiveTrue();
}