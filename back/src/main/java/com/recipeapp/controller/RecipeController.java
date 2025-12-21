package com.recipeapp.controller;

import com.recipeapp.dto.RecipeRequest;
import com.recipeapp.dto.RecipeResponse;
import com.recipeapp.entity.Recipe;
import com.recipeapp.entity.User;
import com.recipeapp.service.RecipeService;
import com.recipeapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {
    
    private final RecipeService recipeService;
    private final UserService userService;
    
    @GetMapping("/public")
    public ResponseEntity<List<RecipeResponse>> getAllPublicRecipes() {
        List<Recipe> recipes = recipeService.getAllActiveRecipes();
        return ResponseEntity.ok(recipes.stream().map(this::toResponse).collect(Collectors.toList()));
    }
    
    @GetMapping
    public ResponseEntity<List<RecipeResponse>> getAllRecipes(Authentication authentication) {
        List<Recipe> recipes = recipeService.getAllActiveRecipes();
        return ResponseEntity.ok(recipes.stream().map(this::toResponse).collect(Collectors.toList()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipeById(@PathVariable String id) {
        Recipe recipe = recipeService.getRecipeById(id);
        return ResponseEntity.ok(toResponse(recipe));
    }
    
    @GetMapping("/my-recipes")
    public ResponseEntity<List<RecipeResponse>> getMyRecipes(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        List<Recipe> recipes = recipeService.getRecipesByUser(user.getId());
        return ResponseEntity.ok(recipes.stream().map(this::toResponse).collect(Collectors.toList()));
    }
    
    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(@RequestBody RecipeRequest request, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        
        Recipe recipe = toEntity(request);
        Recipe savedRecipe = recipeService.createRecipe(recipe, user.getId());
        return ResponseEntity.ok(toResponse(savedRecipe));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RecipeResponse> updateRecipe(@PathVariable String id, @RequestBody RecipeRequest request, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        
        Recipe recipe = toEntity(request);
        Recipe updatedRecipe = recipeService.updateRecipe(id, recipe, user.getId());
        return ResponseEntity.ok(toResponse(updatedRecipe));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable String id, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        
        recipeService.deleteRecipe(id, user.getId());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search/name")
    public ResponseEntity<List<RecipeResponse>> searchByName(@RequestParam String keyword) {
        List<Recipe> recipes = recipeService.searchByName(keyword);
        return ResponseEntity.ok(recipes.stream().map(this::toResponse).collect(Collectors.toList()));
    }
    
    @GetMapping("/search/ingredient")
    public ResponseEntity<List<RecipeResponse>> searchByIngredient(@RequestParam String ingredient) {
        List<Recipe> recipes = recipeService.searchByIngredient(ingredient);
        return ResponseEntity.ok(recipes.stream().map(this::toResponse).collect(Collectors.toList()));
    }
    
    @GetMapping("/search/type")
    public ResponseEntity<List<RecipeResponse>> searchByType(@RequestParam Recipe.RecipeType type) {
        List<Recipe> recipes = recipeService.searchByType(type);
        return ResponseEntity.ok(recipes.stream().map(this::toResponse).collect(Collectors.toList()));
    }
    
    private Recipe toEntity(RecipeRequest req) {
        Recipe recipe = new Recipe();
        recipe.setName(req.getName());
        recipe.setIngredientsList(req.getIngredientsList());
        recipe.setTemperature(req.getTemperature());
        recipe.setCookingTime(req.getCookingTime());
        recipe.setInstructions(req.getInstructions());
        recipe.setRecipeType(req.getRecipeType());
        recipe.setCreatorRating(req.getCreatorRating());
        recipe.setCreatorComment(req.getCreatorComment());
        recipe.setExternalLinks(req.getExternalLinks());
        recipe.setLanguage(req.getLanguage());
        return recipe;
    }
    
    private RecipeResponse toResponse(Recipe recipe) {
        return new RecipeResponse(
            recipe.getId(),
            recipe.getName(),
            recipe.getIngredientsList(),
            recipe.getTemperature(),
            recipe.getCookingTime(),
            recipe.getInstructions(),
            recipe.getRecipeType(),
            recipe.getCreatorRating(),
            recipe.getCreatorComment(),
            recipe.getExternalLinks(),
            recipe.getLanguage(),
            recipe.getUser().getUsername()
        );
    }
}