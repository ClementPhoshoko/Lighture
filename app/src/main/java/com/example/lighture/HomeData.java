package com.example.lighture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Temporary in-memory dummy data so the home screen has content to render
 * before the real fridge and recipe pipelines land. Replace the contents of
 * {@link #recipes()} and {@link #overview()} with live data sources later.
 */
public final class HomeData {

    private HomeData() {
    }

    /** Recipe suggestion shown in the meal-idea carousel. */
    public static final class Recipe {
        public final String title;
        public final String time;
        public final String likes;
        public final int artRes;
        public final String tag;
        public final int tagBgRes;
        public final int tagTextRes;

        Recipe(String title, String time, String likes, int artRes,
               String tag, int tagBgRes, int tagTextRes) {
            this.title = title;
            this.time = time;
            this.likes = likes;
            this.artRes = artRes;
            this.tag = tag;
            this.tagBgRes = tagBgRes;
            this.tagTextRes = tagTextRes;
        }
    }

    /** One fridge-overview stat card (items / expiring / waste). */
    public static final class OverviewStat {
        public final String value;
        public final int labelRes;

        OverviewStat(String value, int labelRes) {
            this.value = value;
            this.labelRes = labelRes;
        }
    }

    /** Smart suggestion matched to the user's fridge ingredients. */
    public static final class Suggestion {
        public final String title;
        public final String time;
        public final int ingredientCount;
        public final int artRes;

        Suggestion(String title, String time, int ingredientCount, int artRes) {
            this.title = title;
            this.time = time;
            this.ingredientCount = ingredientCount;
            this.artRes = artRes;
        }
    }

    /** Smart suggestions list, in display order. */
    public static List<Suggestion> suggestions() {
        return new ArrayList<>(Arrays.asList(
                new Suggestion("Creamy garlic pasta", "18 min", 7, R.drawable.ic_bg_pizza),
                new Suggestion("Rainbow veggie bowl", "22 min", 9, R.drawable.ic_bg_salad),
                new Suggestion("Tomato & basil toast", "10 min", 4, R.drawable.ic_bg_bread),
                new Suggestion("Cheesy omelette breakfast", "12 min", 6, R.drawable.ic_bg_egg_fried)
        ));
    }

    /** Meal-idea carousel entries, in display order. */
    public static List<Recipe> recipes() {
        return new ArrayList<>(Arrays.asList(
                new Recipe("Creamy garlic pasta", "18 min", "248", R.drawable.ic_bg_pizza,
                        "Easy", R.color.tag_easy_background, R.color.tag_easy_text),
                new Recipe("Rainbow veggie bowl", "22 min", "183", R.drawable.ic_bg_salad,
                        "Vegetarian", R.color.tag_vegetarian_background, R.color.tag_vegetarian_text),
                new Recipe("Tomato & basil toast", "10 min", "96", R.drawable.ic_bg_bread,
                        "Quick", R.color.tag_quick_background, R.color.tag_quick_text),
                new Recipe("Cheesy omelette breakfast", "12 min", "412", R.drawable.ic_bg_egg_fried,
                        "Easy", R.color.tag_easy_background, R.color.tag_easy_text),
                new Recipe("Smoky sausage stir-fry", "25 min", "367", R.drawable.ic_bg_sausage,
                        "Quick", R.color.tag_quick_background, R.color.tag_quick_text),
                new Recipe("Lemon herb grilled fish", "28 min", "520", R.drawable.ic_bg_fish,
                        "Healthy", R.color.tag_healthy_background, R.color.tag_healthy_text)
        ));
    }

    /** Fridge overview stat cards, in the order the layout renders them. */
    public static List<OverviewStat> overview() {
        return new ArrayList<>(Arrays.asList(
                new OverviewStat("24", R.string.home_overview_items),
                new OverviewStat("6", R.string.home_overview_expiring),
                new OverviewStat("2", R.string.home_overview_waste)
        ));
    }

    /** Fridge-care tip shown in the rotating advisory card. */
    public static final class Advisory {
        public final int iconRes;
        public final String title;
        public final String body;

        Advisory(int iconRes, String title, String body) {
            this.iconRes = iconRes;
            this.title = title;
            this.body = body;
        }
    }

    /** Fridge-care tips, rotated automatically by MainActivity every 10s. */
    public static List<Advisory> advisories() {
        return new ArrayList<>(Arrays.asList(
                new Advisory(R.drawable.ic_bg_carrot, "First in, first out",
                        "Eat the oldest items first so nothing gets forgotten."),
                new Advisory(R.drawable.ic_bg_salad, "Keep greens crisp",
                        "Wrap leafy greens in a damp cloth to make them last longer."),
                new Advisory(R.drawable.ic_bg_milk, "Use the cold shelf",
                        "Dairy and leftovers keep longer on the coldest shelf, not the door."),
                new Advisory(R.drawable.ic_bg_banana, "Bananas like the counter",
                        "Cold air turns banana skins brown — keep them at room temperature."),
                new Advisory(R.drawable.ic_bg_fish, "Freeze to save",
                        "Freeze what you won't eat this week; it will keep for months.")
        ));
    }
}
