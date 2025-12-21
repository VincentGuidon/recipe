-- Create Database
CREATE DATABASE recipedb;

-- Connect to the database
\c recipedb;

-- Create Users Table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    username VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    last_login_at TIMESTAMP
);

-- Create Recipes Table
CREATE TABLE recipes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    ingredients_list JSONB,
    temperature INTEGER,
    cooking_time INTEGER NOT NULL,
    instructions TEXT,
    image_list JSONB,
    recipe_type VARCHAR(50) NOT NULL,
    creator_rating INTEGER CHECK (creator_rating >= 1 AND creator_rating <= 5),
    creator_comment TEXT,
    user_comments JSONB,
    external_links JSONB,
    is_active BOOLEAN NOT NULL DEFAULT true,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    language VARCHAR(5) NOT NULL DEFAULT 'EN'
);

-- Create Images Table
CREATE TABLE images (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    display_name VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    attachment VARCHAR(500) NOT NULL,
    image_type VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    recipe_id UUID REFERENCES recipes(id) ON DELETE CASCADE
);

-- Create Indexes for Performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_recipes_user_id ON recipes(user_id);
CREATE INDEX idx_recipes_recipe_type ON recipes(recipe_type);
CREATE INDEX idx_recipes_language ON recipes(language);
CREATE INDEX idx_recipes_is_active ON recipes(is_active);
CREATE INDEX idx_recipes_name ON recipes(name);
CREATE INDEX idx_images_recipe_id ON images(recipe_id);

-- Insert Sample Admin User (password: admin123)
INSERT INTO users (email, password_hash, username, role, is_active)
VALUES ('admin@recipeapp.com', '$2a$10$XptfskLsT0r8jKhYj9MKm.eSqbRLzCN2R9p1v8MNgZhQoC8kSFp12', 'Admin', 'ADMIN', true);

-- Insert Sample Regular User (password: user123)
INSERT INTO users (email, password_hash, username, role, is_active)
VALUES ('user@recipeapp.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'John Doe', 'USER', true);

-- Insert Sample Recipe
INSERT INTO recipes (name, ingredients_list, temperature, cooking_time, instructions, recipe_type, creator_rating, language, user_id)
VALUES (
    'Chocolate Chip Cookies',
    '[{"name": "Flour", "quantity": "2 cups"}, {"name": "Sugar", "quantity": "1 cup"}, {"name": "Butter", "quantity": "1 cup"}, {"name": "Chocolate chips", "quantity": "2 cups"}, {"name": "Eggs", "quantity": "2"}]'::jsonb,
    180,
    12,
    '1. Preheat oven to 180°C
2. Mix butter and sugar until creamy
3. Add eggs one at a time
4. Gradually add flour
5. Fold in chocolate chips
6. Drop spoonfuls onto baking sheet
7. Bake for 10-12 minutes',
    'DESSERT',
    5,
    'EN',
    (SELECT id FROM users WHERE email = 'user@recipeapp.com')
);