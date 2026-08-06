package com.example.lighture;

import java.util.HashMap;
import java.util.Map;

/**
 * Intelligent mapper that returns a food icon based on keywords found in the product name.
 * Prevents hardcoding UI assets in the data layer.
 */
public final class ProductIconMapper {

    private static final Map<String, Integer> keywordMap = new HashMap<>();

    static {
        keywordMap.put("apple", R.drawable.ic_bg_apple);
        keywordMap.put("banana", R.drawable.ic_bg_banana);
        keywordMap.put("bread", R.drawable.ic_bg_bread);
        keywordMap.put("broccoli", R.drawable.ic_bg_broccoli);
        keywordMap.put("butter", R.drawable.ic_bg_butter);
        keywordMap.put("carrot", R.drawable.ic_bg_carrot);
        keywordMap.put("cheese", R.drawable.ic_bg_cheese);
        keywordMap.put("cherry", R.drawable.ic_bg_cherry);
        keywordMap.put("chicken", R.drawable.ic_bg_chicken);
        keywordMap.put("coffee", R.drawable.ic_bg_coffee);
        keywordMap.put("cookie", R.drawable.ic_bg_cookie);
        keywordMap.put("egg", R.drawable.ic_bg_egg);
        keywordMap.put("fish", R.drawable.ic_bg_fish);
        keywordMap.put("grape", R.drawable.ic_bg_grape);
        keywordMap.put("juice", R.drawable.ic_bg_juice);
        keywordMap.put("lemon", R.drawable.ic_bg_lemon);
        keywordMap.put("meat", R.drawable.ic_bg_meat);
        keywordMap.put("milk", R.drawable.ic_bg_milk);
        keywordMap.put("mushroom", R.drawable.ic_bg_mushroom);
        keywordMap.put("onion", R.drawable.ic_bg_onion);
        keywordMap.put("pepper", R.drawable.ic_bg_pepper);
        keywordMap.put("pizza", R.drawable.ic_bg_pizza);
        keywordMap.put("potato", R.drawable.ic_bg_potato);
        keywordMap.put("salad", R.drawable.ic_bg_salad);
        keywordMap.put("sauce", R.drawable.ic_bg_sauce);
        keywordMap.put("sausage", R.drawable.ic_bg_sausage);
        keywordMap.put("soda", R.drawable.ic_bg_soda);
        keywordMap.put("steak", R.drawable.ic_bg_steak);
        keywordMap.put("tomato", R.drawable.ic_bg_tomato);
        keywordMap.put("water", R.drawable.ic_bg_water);
        keywordMap.put("yogurt", R.drawable.ic_bg_yogurt);
        keywordMap.put("lettuce", R.drawable.ic_bg_salad); // Fallback to salad
    }

    private ProductIconMapper() {}

    /**
     * Finds the best matching icon for a given product name.
     * @param productName The name of the product (e.g. "Fuji Apple").
     * @return The resource ID of the icon, or a generic fallback icon.
     */
    public static int getIconFor(String productName) {
        if (productName == null) return R.drawable.ic_outline_basket;

        String lowerName = productName.toLowerCase();
        for (Map.Entry<String, Integer> entry : keywordMap.entrySet()) {
            if (lowerName.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return R.drawable.ic_outline_basket; // Elegant generic fallback
    }
}
