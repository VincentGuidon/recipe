package com.recipeapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "recipes")
@Data
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> ingredientsList;
    
    private Integer temperature;
    
    @Column(nullable = false)
    private Integer cookingTime;
    
    @Column(columnDefinition = "TEXT")
    private String instructions;
    
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<String> imageList = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipeType recipeType;
    
    private Integer creatorRating;
    
    @Column(columnDefinition = "TEXT")
    private String creatorComment;
    
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> userComments = new ArrayList<>();
    
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<String> externalLinks = new ArrayList<>();
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language = Language.EN;
    
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();
    
    public enum RecipeType {
        APPETIZER, STARTER, MAIN, DESSERT, DRINK, OTHER, 
        DRESSING_SAUCE, SPREAD, BREAD, DOUGH
    }
    
    public enum Language {
        EN, FR
    }
}