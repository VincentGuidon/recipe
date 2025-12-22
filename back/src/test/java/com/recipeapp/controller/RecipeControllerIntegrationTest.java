package com.recipeapp.controller;

import com.recipeapp.BaseIntegrationTest;
import com.recipeapp.dto.AuthRequest;
import com.recipeapp.dto.AuthResponse;
import com.recipeapp.dto.RecipeRequest;
import com.recipeapp.entity.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RecipeControllerIntegrationTest extends BaseIntegrationTest {
    
    private String authToken;
    private Recipe testRecipe;
    
    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        authToken = loginAndGetToken("test@example.com", "password123");
        createTestRecipe();
    }
    
    private String loginAndGetToken(String email, String password) {
        try {
            AuthRequest request = new AuthRequest();
            request.setEmail(email);
            request.setPassword(password);
            
            MvcResult result = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(request)))
                    .andExpect(status().isOk())
                    .andReturn();
            
            String response = result.getResponse().getContentAsString();
            AuthResponse authResponse = objectMapper.readValue(response, AuthResponse.class);
            return authResponse.getToken();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get auth token", e);
        }
    }
    
    private void createTestRecipe() {
        testRecipe = new Recipe();
        testRecipe.setName("Test Recipe");
        testRecipe.setIngredientsList(Arrays.asList(
            Map.of("name", "Flour", "quantity", "2 cups"),
            Map.of("name", "Sugar", "quantity", "1 cup")
        ));
        testRecipe.setTemperature(180);
        testRecipe.setCookingTime(30);
        testRecipe.setInstructions("Mix ingredients and bake");
        testRecipe.setRecipeType(Recipe.RecipeType.DESSERT);
        testRecipe.setCreatorRating(4);
        testRecipe.setLanguage(Recipe.Language.EN);
        testRecipe.setUser(testUser);
        testRecipe = recipeRepository.save(testRecipe);
    }
    
    @Test
    public void testGetAllPublicRecipes_Success() throws Exception {
        mockMvc.perform(get("/api/recipes/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Recipe"));
    }
    
    @Test
    public void testGetAllRecipes_Authenticated() throws Exception {
        mockMvc.perform(get("/api/recipes")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Recipe"));
    }
    
    @Test
    public void testGetAllRecipes_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    public void testGetRecipeById_Success() throws Exception {
        mockMvc.perform(get("/api/recipes/" + testRecipe.getId())
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Recipe"))
                .andExpect(jsonPath("$.cookingTime").value(30))
                .andExpect(jsonPath("$.temperature").value(180));
    }
    
    @Test
    public void testGetRecipeById_NotFound() throws Exception {
        mockMvc.perform(get("/api/recipes/non-existent-id")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().is5xxServerError());
    }
    
    @Test
    public void testCreateRecipe_Success() throws Exception {
        RecipeRequest request = new RecipeRequest();
        request.setName("New Recipe");
        request.setIngredientsList(Arrays.asList(
            Map.of("name", "Eggs", "quantity", "3"),
            Map.of("name", "Milk", "quantity", "1 cup")
        ));
        request.setTemperature(200);
        request.setCookingTime(45);
        request.setInstructions("Beat eggs, add milk, bake");
        request.setRecipeType(Recipe.RecipeType.MAIN);
        request.setCreatorRating(5);
        request.setLanguage(Recipe.Language.EN);
        
        mockMvc.perform(post("/api/recipes")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Recipe"))
                .andExpect(jsonPath("$.cookingTime").value(45))
                .andExpect(jsonPath("$.creatorUsername").value("Test User"));
    }
    
    @Test
    public void testCreateRecipe_Unauthenticated() throws Exception {
        RecipeRequest request = new RecipeRequest();
        request.setName("Unauthorized Recipe");
        
        mockMvc.perform(post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    public void testUpdateRecipe_Success() throws Exception {
        RecipeRequest request = new RecipeRequest();
        request.setName("Updated Recipe Name");
        request.setIngredientsList(testRecipe.getIngredientsList());
        request.setTemperature(220);
        request.setCookingTime(35);
        request.setInstructions("Updated instructions");
        request.setRecipeType(Recipe.RecipeType.DESSERT);
        request.setLanguage(Recipe.Language.FR);
        
        mockMvc.perform(put("/api/recipes/" + testRecipe.getId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Recipe Name"))
                .andExpect(jsonPath("$.temperature").value(220))
                .andExpect(jsonPath("$.cookingTime").value(35));
    }
    
    @Test
    public void testUpdateRecipe_Unauthorized() throws Exception {
        String otherUserToken = loginAndGetToken("admin@example.com", "admin123");
        
        RecipeRequest request = new RecipeRequest();
        request.setName("Hacked Recipe");
        
        mockMvc.perform(put("/api/recipes/" + testRecipe.getId())
                .header("Authorization", "Bearer " + otherUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().is5xxServerError());
    }
    
    @Test
    public void testDeleteRecipe_Success() throws Exception {
        mockMvc.perform(delete("/api/recipes/" + testRecipe.getId())
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());
        
        // Verify recipe is soft deleted (isActive = false)
        Recipe deletedRecipe = recipeRepository.findById(testRecipe.getId()).orElseThrow();
        assert !deletedRecipe.getIsActive();
    }
    
    @Test
    public void testDeleteRecipe_Unauthorized() throws Exception {
        String otherUserToken = loginAndGetToken("admin@example.com", "admin123");
        
        mockMvc.perform(delete("/api/recipes/" + testRecipe.getId())
                .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().is5xxServerError());
    }
    
    @Test
    public void testSearchByName_Success() throws Exception {
        mockMvc.perform(get("/api/recipes/search/name")
                .param("keyword", "Test")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Recipe"));
    }
    
    @Test
    public void testSearchByName_NoResults() throws Exception {
        mockMvc.perform(get("/api/recipes/search/name")
                .param("keyword", "Nonexistent")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
    
    @Test
    public void testSearchByIngredient_Success() throws Exception {
        mockMvc.perform(get("/api/recipes/search/ingredient")
                .param("ingredient", "Flour")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }
    
    @Test
    public void testSearchByType_Success() throws Exception {
        mockMvc.perform(get("/api/recipes/search/type")
                .param("type", "DESSERT")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].recipeType").value("DESSERT"));
    }
    
    @Test
    public void testGetMyRecipes_Success() throws Exception {
        mockMvc.perform(get("/api/recipes/my-recipes")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].creatorUsername").value("Test User"));
    }
    
    @Test
    public void testGetMyRecipes_EmptyForNewUser() throws Exception {
        String newUserToken = loginAndGetToken("admin@example.com", "admin123");
        
        mockMvc.perform(get("/api/recipes/my-recipes")
                .header("Authorization", "Bearer " + newUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}