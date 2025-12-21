package com.recipeapp.dto;

import com.recipeapp.entity.Recipe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeRequest {
    private String name;
    private List<Map<String, Object>> ingredientsList;
    private Integer temperature;
    private Integer cookingTime;
    private String instructions;
    private Recipe.RecipeType recipeType;
    private Integer creatorRating;
    private String creatorComment;
    private List<String> externalLinks;
    private Recipe.Language language;
}
