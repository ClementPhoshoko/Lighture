package com.example.lighture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Temporary in-memory dummy data for the Fridge screen, mirroring
 * {@link HomeData} and {@link RecipesData} until a real data layer lands.
 * Filter keys match the freshness chips; {@link #stats()} feeds the overview
 * card and stays in sync with {@link #all()}.
 */
public final class FridgeData {

    private FridgeData() {
    }

    public static final String FILTER_ALL = "All Items";
    public static final String FILTER_FRESH = "Fresh";
    public static final String FILTER_EXPIRING = "Expiring Soon";
    public static final String FILTER_EXPIRED = "Expired";

    /** Overview headline figures: items / fresh / expiring / potential saved. */
    public static final class FridgeStats {
        public final String items;
        public final String fresh;
        public final String expiring;
        public final String saved;

        FridgeStats(String items, String fresh, String expiring, String saved) {
            this.items = items;
            this.fresh = fresh;
            this.expiring = expiring;
            this.saved = saved;
        }
    }

    public static List<FridgeItem> all() {
        return new ArrayList<>(Arrays.asList(
                new FridgeItem("Tomatoes", "5 pieces",
                        FridgeItem.STATUS_FRESH, "5 days left"),
                new FridgeItem("Lettuce", "1 head",
                        FridgeItem.STATUS_FRESH, "5 days left"),
                new FridgeItem("Apples", "4 pieces",
                        FridgeItem.STATUS_FRESH, "7 days left"),
                new FridgeItem("Carrots", "500 g",
                        FridgeItem.STATUS_FRESH, "6 days left"),
                new FridgeItem("Cheese", "1 block",
                        FridgeItem.STATUS_FRESH, "9 days left"),
                new FridgeItem("Milk", "1 bottle (1L)",
                        FridgeItem.STATUS_EXPIRING, "2 days left"),
                new FridgeItem("Eggs", "6 pieces",
                        FridgeItem.STATUS_EXPIRING, "2 days left"),
                new FridgeItem("Chicken Breast", "2 pieces (400g)",
                        FridgeItem.STATUS_EXPIRED, "1 day ago"),
                new FridgeItem("Mushrooms", "200 g",
                        FridgeItem.STATUS_EXPIRED, "2 days ago"),
                new FridgeItem("Bananas", "3 pieces",
                        FridgeItem.STATUS_EXPIRED, "2 days ago"),
                new FridgeItem("Sausages", "6 pieces",
                        FridgeItem.STATUS_EXPIRED, "4 days ago"),
                new FridgeItem("Fish", "300 g",
                        FridgeItem.STATUS_EXPIRED, "2 days ago"),
                new FridgeItem("Exotic Dragonfruit", "1 piece",
                        FridgeItem.STATUS_FRESH, "10 days left")
        ));
    }

    public static FridgeStats stats() {
        int fresh = 0;
        int expiring = 0;
        for (FridgeItem item : all()) {
            if (item.status == FridgeItem.STATUS_FRESH) {
                fresh++;
            } else if (item.status == FridgeItem.STATUS_EXPIRING) {
                expiring++;
            }
        }
        return new FridgeStats(String.valueOf(all().size()),
                String.valueOf(fresh), String.valueOf(expiring), "R156");
    }

    public static String filterFor(int status) {
        switch (status) {
            case FridgeItem.STATUS_FRESH:
                return FILTER_FRESH;
            case FridgeItem.STATUS_EXPIRING:
                return FILTER_EXPIRING;
            default:
                return FILTER_EXPIRED;
        }
    }

    public static boolean matchesFilter(FridgeItem item, String filter) {
        return FILTER_ALL.equals(filter) || filterFor(item.status).equals(filter);
    }
}
