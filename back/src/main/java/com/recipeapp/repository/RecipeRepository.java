package com.recipeapp.repository;

import com.recipeapp.entity.Recipe;
import com.recipeapp.entity.Recipe.RecipeType;
import com.recipeapp.entity.Recipe.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, String> {
    
    List<Recipe> findByIsActiveTrue();
    
    List<Recipe> findByUserId(String userId);
    
    List<Recipe> findByRecipeType(RecipeType recipeType);
    
    List<Recipe> findByLanguage(Language language);
    
    @Query("SELECT r FROM Recipe r WHERE r.isActive = true AND " +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Recipe> searchByName(@Param("keyword") String keyword);
    
    @Query("SELECT r FROM Recipe r WHERE r.isActive = true AND " +
           "CAST(r.ingredientsList AS string) LIKE LOWER(CONCAT('%', :ingredient, '%'))")
    List<Recipe> searchByIngredient(@Param("ingredient") String ingredient);
}