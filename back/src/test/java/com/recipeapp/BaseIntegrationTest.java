package com.recipeapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipeapp.entity.User;
import com.recipeapp.repository.ImageRepository;
import com.recipeapp.repository.RecipeRepository;
import com.recipeapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {
    
    @Autowired
    protected MockMvc mockMvc;
    
    @Autowired
    protected ObjectMapper objectMapper;
    
    @Autowired
    protected UserRepository userRepository;
    
    @Autowired
    protected RecipeRepository recipeRepository;
    
    @Autowired
    protected ImageRepository imageRepository;
    
    @Autowired
    protected PasswordEncoder passwordEncoder;
    
    protected User testUser;
    protected User testAdmin;
    protected String userToken;
    protected String adminToken;
    
    @BeforeEach
    public void setUp() {
        // Clean database before each test
        imageRepository.deleteAll();
        recipeRepository.deleteAll();
        userRepository.deleteAll();
        
        // Create test users
        createTestUsers();
    }
    
    protected void createTestUsers() {
        // Create regular user
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash(passwordEncoder.encode("password123"));
        testUser.setUsername("Test User");
        testUser.setRole(User.UserRole.USER);
        testUser.setIsActive(true);
        testUser = userRepository.save(testUser);
        
        // Create admin user
        testAdmin = new User();
        testAdmin.setEmail("admin@example.com");
        testAdmin.setPasswordHash(passwordEncoder.encode("admin123"));
        testAdmin.setUsername("Admin User");
        testAdmin.setRole(User.UserRole.ADMIN);
        testAdmin.setIsActive(true);
        testAdmin = userRepository.save(testAdmin);
    }
    
    protected String asJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}