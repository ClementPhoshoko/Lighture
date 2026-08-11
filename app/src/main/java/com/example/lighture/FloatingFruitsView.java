package com.example.lighture;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Themed fruit/produce background. Draws a random selection of produce icons,
 * scattered with random position, size and rotation. The layout is shuffled
 * once when the view is sized, so each screen shows a different arrangement
 * at zero ongoing cost (no animation loop). Colors resolve per light/dark mode
 * through the @color/bg_icon_* tokens.
 *
 * Add as the first, full-bleed child of a screen's root layout, behind the
 * content container.
 */
public class FloatingFruitsView extends View {

    private static final int[] ICONS = {
            R.drawable.ic_bg_carrot, R.drawable.ic_bg_salad, R.drawable.ic_bg_mushroom,
            R.drawable.ic_bg_apple, R.drawable.ic_bg_pepper, R.drawable.ic_bg_cherry,
            R.drawable.ic_bg_grape, R.drawable.ic_bg_lemon, R.drawable.ic_bg_banana,
            R.drawable.ic_bg_bread, R.drawable.ic_bg_cookie, R.drawable.ic_bg_pizza,
            R.drawable.ic_bg_candy, R.drawable.ic_bg_milk, R.drawable.ic_bg_cheese,
            R.drawable.ic_bg_egg, R.drawable.ic_bg_egg_fried, R.drawable.ic_bg_meat,
            R.drawable.ic_bg_sausage, R.drawable.ic_bg_fish, R.drawable.ic_bg_coffee
    };

    private static final int MIN_COUNT = 12;
    private static final int MAX_COUNT = 16;
    private static final float MIN_SIZE_DP = 64f;
    private static final float MAX_SIZE_DP = 120f;
    private static final int MAX_ROTATION_DEG = 20;

    private final Random random = new Random();
    private final List<Drawable> iconPool = new ArrayList<>();
    private final List<FloatingItem> items = new ArrayList<>();

    public FloatingFruitsView(Context context) {
        super(context);
        init();
    }

    public FloatingFruitsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatingFruitsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        for (int res : ICONS) {
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), res, getContext().getTheme());
            if (drawable != null) {
                // Mutate so alpha changes don't leak to other views using the same resource
                iconPool.add(drawable.mutate());
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        shuffle();
    }

    private void shuffle() {
        items.clear();
        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0 || iconPool.isEmpty()) {
            return;
        }
        int count = MIN_COUNT + random.nextInt(MAX_COUNT - MIN_COUNT + 1);
        for (int i = 0; i < count; i++) {
            Drawable drawable = iconPool.get(random.nextInt(iconPool.size()));
            float size = dp(MIN_SIZE_DP + random.nextFloat() * (MAX_SIZE_DP - MIN_SIZE_DP));
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            float rotation = random.nextInt(MAX_ROTATION_DEG * 2 + 1) - MAX_ROTATION_DEG;
            items.add(new FloatingItem(drawable, x, y, size, rotation));
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (FloatingItem item : items) {
            canvas.save();
            canvas.rotate(item.rotation, item.x, item.y);
            int half = (int) (item.size / 2f);
            item.drawable.setBounds(new Rect(item.x - half, item.y - half, item.x + half, item.y + half));
            item.drawable.setAlpha(60); // Low opacity (approx 23%)
            item.drawable.draw(canvas);
            canvas.restore();
        }
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private static class FloatingItem {
        final Drawable drawable;
        final int x;
        final int y;
        final float size;
        final float rotation;

        FloatingItem(Drawable drawable, int x, int y, float size, float rotation) {
            this.drawable = drawable;
            this.x = x;
            this.y = y;
            this.size = size;
            this.rotation = rotation;
        }
    }
}
