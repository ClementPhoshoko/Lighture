package com.example.lighture;

import java.util.List;

/**
 * One recipe shown in the Recipes screen. Mutable favourite state so the
 * heart toggle can update a single card without rebuilding the list.
 * Placeholder art (rather than URLs) matches the app's pastel-image style
 * until real food photos exist.
 */
public final class Recipe {

    public final String title;
    public final String description;
    public final String cookingTime;
    public final String category;
    public final List<String> ingredients;
    public final int imageRes;
    public boolean isFavorite;

    Recipe(String title, String description, String cookingTime, String category,
           List<String> ingredients, int imageRes) {
        this.title = title;
        this.description = description;
        this.cookingTime = cookingTime;
        this.category = category;
        this.ingredients = ingredients;
        this.imageRes = imageRes;
        this.isFavorite = false;
    }
}
