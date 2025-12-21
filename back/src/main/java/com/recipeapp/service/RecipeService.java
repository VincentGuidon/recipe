package com.recipeapp.service;

import com.recipeapp.entity.Recipe;
import com.recipeapp.entity.User;
import com.recipeapp.repository.RecipeRepository;
import com.recipeapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {
    
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public Recipe createRecipe(Recipe recipe, String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        recipe.setUser(user);
        recipe.setIsActive(true);
        return recipeRepository.save(recipe);
    }
    
    @Transactional(readOnly = true)
    public List<Recipe> getAllActiveRecipes() {
        return recipeRepository.findByIsActiveTrue();
    }
    
    @Transactional(readOnly = true)
    public Recipe getRecipeById(String id) {
        return recipeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recipe not found"));
    }
    
    @Transactional(readOnly = true)
    public List<Recipe> getRecipesByUser(String userId) {
        return recipeRepository.findByUserId(userId);
    }
    
    @Transactional
    public Recipe updateRecipe(String id, Recipe updatedRecipe, String userId) {
        Recipe recipe = recipeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recipe not found"));
        
        if (!recipe.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this recipe");
        }
        
        recipe.setName(updatedRecipe.getName());
        recipe.setIngredientsList(updatedRecipe.getIngredientsList());
        recipe.setTemperature(updatedRecipe.getTemperature());
        recipe.setCookingTime(updatedRecipe.getCookingTime());
        recipe.setInstructions(updatedRecipe.getInstructions());
        recipe.setRecipeType(updatedRecipe.getRecipeType());
        recipe.setCreatorRating(updatedRecipe.getCreatorRating());
        recipe.setCreatorComment(updatedRecipe.getCreatorComment());
        recipe.setExternalLinks(updatedRecipe.getExternalLinks());
        recipe.setLanguage(updatedRecipe.getLanguage());
        
        return recipeRepository.save(recipe);
    }
    
    @Transactional
    public void deleteRecipe(String id, String userId) {
        Recipe recipe = recipeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recipe not found"));
        
        if (!recipe.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this recipe");
        }
        
        recipe.setIsActive(false);
        recipeRepository.save(recipe);
    }
    
    @Transactional(readOnly = true)
    public List<Recipe> searchByName(String keyword) {
        return recipeRepository.searchByName(keyword);
    }
    
    @Transactional(readOnly = true)
    public List<Recipe> searchByIngredient(String ingredient) {
        return recipeRepository.searchByIngredient(ingredient);
    }
    
    @Transactional(readOnly = true)
    public List<Recipe> searchByType(Recipe.RecipeType recipeType) {
        return recipeRepository.findByRecipeType(recipeType);
    }
}