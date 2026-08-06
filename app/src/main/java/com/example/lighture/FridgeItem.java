package com.example.lighture;

/**
 * One ingredient tracked in the Fridge screen. Status drives the badge colour
 * via the shared freshness tokens (fresh / expiring / expired) defined in
 * colors.xml. Expiry text is display-ready ("5 days left", "1 day ago").
 */
public final class FridgeItem {

    public static final int STATUS_FRESH = 0;
    public static final int STATUS_EXPIRING = 1;
    public static final int STATUS_EXPIRED = 2;

    public final String name;
    public final String quantity;
    public final int imageRes;
    public final int status;
    public final String expiryText;

    public FridgeItem(String name, String quantity, int imageRes, int status, String expiryText) {
        this.name = name;
        this.quantity = quantity;
        this.imageRes = imageRes;
        this.status = status;
        this.expiryText = expiryText;
    }

    /**
     * Constructor for items relying on the dynamic icon mapper.
     */
    public FridgeItem(String name, String quantity, int status, String expiryText) {
        this(name, quantity, 0, status, expiryText);
    }

    public static int statusColorRes(int status) {
        switch (status) {
            case STATUS_FRESH:
                return R.color.fresh_color;
            case STATUS_EXPIRING:
                return R.color.expiring_color;
            default:
                return R.color.expired_color;
        }
    }

    public static int statusBackgroundRes(int status) {
        switch (status) {
            case STATUS_FRESH:
                return R.color.fresh_background;
            case STATUS_EXPIRING:
                return R.color.expiring_background;
            default:
                return R.color.expired_background;
        }
    }

    public static int statusLabelRes(int status) {
        switch (status) {
            case STATUS_FRESH:
                return R.string.fridge_status_fresh;
            case STATUS_EXPIRING:
                return R.string.fridge_status_expiring;
            default:
                return R.string.fridge_status_expired;
        }
    }
}
