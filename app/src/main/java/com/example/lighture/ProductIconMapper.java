package com.example.lighture;

import java.util.HashMap;
import java.util.Map;

/**
 * Intelligent mapper that returns a food icon based on keywords found in the product name.
 * Prevents hardcoding UI assets in the data layer.
 */
public final class ProductIconMapper {

    private static final Map<String, Integer> keywordMap = new HashMap<>();
    private static final Map<String, String> emojiMap = new HashMap<>();

    static {
        // High-quality PNG mappings
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
        keywordMap.put("breast", R.drawable.ic_bg_chicken);
        keywordMap.put("fillet", R.drawable.ic_bg_fish);
        keywordMap.put("sirloin", R.drawable.ic_bg_steak);
        keywordMap.put("cheddar", R.drawable.ic_bg_cheese);
        keywordMap.put("lettuce", R.drawable.ic_bg_salad);
        keywordMap.put("spinach", R.drawable.ic_bg_salad);
        keywordMap.put("beef", R.drawable.ic_bg_steak);

        // Emoji fallbacks for items without custom icons
        emojiMap.put("avocado", "🥑");
        emojiMap.put("garlic", "🧄");
        emojiMap.put("onion", "🧅");
        emojiMap.put("corn", "🌽");
        emojiMap.put("shrimp", "🍤");
        emojiMap.put("pork", "🥩");
        emojiMap.put("rice", "🍚");
        emojiMap.put("pasta", "🍝");
        emojiMap.put("noodles", "🍜");
        emojiMap.put("honey", "🍯");
        emojiMap.put("salt", "🧂");
        emojiMap.put("sugar", "🧂");
        emojiMap.put("flour", "🍞");
        emojiMap.put("oil", "🫗");
        emojiMap.put("vinegar", "🍶");
        emojiMap.put("wine", "🍷");
        emojiMap.put("beer", "🍺");
        emojiMap.put("tea", "🍵");
        emojiMap.put("strawberry", "🍓");
        emojiMap.put("blueberry", "🫐");
        emojiMap.put("pineapple", "🍍");
        emojiMap.put("mango", "🥭");
        emojiMap.put("peach", "🍑");
        emojiMap.put("watermelon", "🍉");
        emojiMap.put("cucumber", "🥒");
        emojiMap.put("eggplant", "🍆");
        emojiMap.put("broccoli", "🥦");
        emojiMap.put("tofu", "🧊");
        emojiMap.put("beans", "🫘");
        emojiMap.put("nuts", "🥜");
        emojiMap.put("bacon", "🥓");
        emojiMap.put("ham", "🥓");
        emojiMap.put("turkey", "🍗");
        emojiMap.put("lamb", "🍖");
        emojiMap.put("ribs", "🍖");
        emojiMap.put("orange", "🍊");
        emojiMap.put("citrus", "🍋");
        emojiMap.put("cabbage", "🥬");
        emojiMap.put("cauliflower", "🥦");
        emojiMap.put("pear", "🍐");
        emojiMap.put("zucchini", "🥒");
        emojiMap.put("squash", "🎃");
        emojiMap.put("jam", "🍯");
        emojiMap.put("jelly", "🍯");
        emojiMap.put("chocolate", "🍫");
        emojiMap.put("ice cream", "🍦");
        emojiMap.put("wrap", "🌯");
        emojiMap.put("tortilla", "🌯");
        emojiMap.put("bagel", "🥯");
        emojiMap.put("pastry", "🥐");
        emojiMap.put("dragon fruit", "🐲");
        emojiMap.put("kiwi", "🥝");
    }

    private ProductIconMapper() {}

    /**
     * Finds the best matching icon for a given product name.
     * @param productName The name of the product (e.g. "Fuji Apple").
     * @return The resource ID of the icon, or 0 if no custom icon matches.
     */
    public static int getIconFor(String productName) {
        if (productName == null) return 0;

        String lowerName = productName.toLowerCase();
        for (Map.Entry<String, Integer> entry : keywordMap.entrySet()) {
            if (lowerName.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return 0; // No match found
    }

    /**
     * Returns an emoji fallback for items that don't have custom icons.
     * @param productName The name of the product.
     * @return A single emoji string, or a generic food emoji.
     */
    public static String getEmojiFor(String productName) {
        if (productName == null) return "🍲";

        String lowerName = productName.toLowerCase();
        for (Map.Entry<String, String> entry : emojiMap.entrySet()) {
            if (lowerName.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return "🍲"; // Clean fallback emoji (Bowl of food)
    }
}
