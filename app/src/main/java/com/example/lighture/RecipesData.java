package com.example.lighture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Static catalogue of recipes for the Recipes screen, mirroring {@link HomeData}
 * until a real data layer lands. Category values match the filter chips.
 */
public final class RecipesData {

    private RecipesData() {
    }

    public static final String CATEGORY_ALL = "All Recipes";
    public static final String CATEGORY_QUICK = "Quick & Easy";
    public static final String CATEGORY_VEGETARIAN = "Vegetarian";
    public static final String CATEGORY_HIGH_PROTEIN = "High Protein";
    public static final String CATEGORY_LOW_WASTE = "Low Waste";

    public static List<Recipe> all() {
        return new ArrayList<>(Arrays.asList(
                new Recipe("Creamy Tomato Pasta",
                        "A creamy and comforting pasta made with tomatoes, garlic and herbs.",
                        "25 min", CATEGORY_QUICK,
                        Arrays.asList("Pasta", "Tomato", "Garlic", "Basil", "Cream"), R.drawable.ic_bg_pizza),
                new Recipe("Veggie Omelette",
                        "Fluffy omelette packed with fresh veggies and perfect seasoning.",
                        "12 min", CATEGORY_QUICK,
                        Arrays.asList("Eggs", "Pepper", "Mushroom", "Onion", "Cheese"), R.drawable.ic_bg_egg_fried),
                new Recipe("Chicken Stir Fry",
                        "Quick and healthy stir fry with tender chicken and crisp veggies.",
                        "20 min", CATEGORY_HIGH_PROTEIN,
                        Arrays.asList("Chicken", "Broccoli", "Soy", "Garlic", "Rice"), R.drawable.ic_bg_meat),
                new Recipe("Rainbow Veggie Bowl",
                        "A colourful bowl full of roasted veg, grains and a zingy dressing.",
                        "22 min", CATEGORY_VEGETARIAN,
                        Arrays.asList("Carrot", "Quinoa", "Spinach", "Tomato", "Avocado"), R.drawable.ic_bg_salad),
                new Recipe("Garlic Butter Salmon",
                        "Pan-seared salmon finished with garlic butter, lemon and herbs.",
                        "28 min", CATEGORY_HIGH_PROTEIN,
                        Arrays.asList("Salmon", "Garlic", "Lemon", "Butter", "Herbs"), R.drawable.ic_bg_fish),
                new Recipe("Cheesy Tomato Toast",
                        "Crispy toast topped with melted cheese and fresh tomato.",
                        "10 min", CATEGORY_LOW_WASTE,
                        Arrays.asList("Bread", "Cheese", "Tomato", "Basil"), R.drawable.ic_bg_bread),
                new Recipe("Banana Oat Pancakes",
                        "Fluffy pancakes made with ripe bananas, oats and eggs.",
                        "15 min", CATEGORY_LOW_WASTE,
                        Arrays.asList("Banana", "Oats", "Egg", "Milk", "Honey"), R.drawable.ic_bg_banana),
                new Recipe("Mushroom Risotto",
                        "Creamy risotto with golden mushrooms and parmesan.",
                        "30 min", CATEGORY_VEGETARIAN,
                        Arrays.asList("Rice", "Mushroom", "Parmesan", "Onion", "Stock"), R.drawable.ic_bg_mushroom)
        ));
    }
}
