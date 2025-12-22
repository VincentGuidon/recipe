package com.recipeapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "images")
@Data
public class Image {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String displayName;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String attachment; // File path or URL
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType imageType;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ImageType {
        STOCK, PRIVATE, STEP_BY_STEP
    }
}