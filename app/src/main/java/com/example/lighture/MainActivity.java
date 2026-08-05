package com.example.lighture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

/**
 * Home screen. Hosts the scrollable sections (greeting, hero, suggestions,
 * fridge overview, meal-idea carousel) over the themed fruit background and a
 * fixed bottom navigation. Sections are stubs until their data pipelines land;
 * the suggestions section shows the friendly robot-chef empty state.
 */
public class MainActivity extends AppCompatActivity {

    private static final String ASSET_HERO_IMAGE = "home_fridge_hero_image.png";

    private static final long ADVISORY_INTERVAL_MS = 10_000L;
    private static final long ADVISORY_SLIDE_MS = 350L;

    private final Handler advisoryHandler = new Handler(Looper.getMainLooper());
    private final Runnable advisoryRunnable = new Runnable() {
        @Override
        public void run() {
            cycleAdvisory((advisoryIndex + 1) % advisories.size());
            advisoryHandler.postDelayed(this, ADVISORY_INTERVAL_MS);
        }
    };

    private List<HomeData.Advisory> advisories;
    private int advisoryIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homeTop), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int px = getResources().getDimensionPixelSize(R.dimen.page_padding_x);
            v.setPadding(bars.left + px, bars.top, bars.right + px, 0);
            return insets;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homeScroll), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, 0, bars.right, 0);
            return insets;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomNav), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, 0, bars.right, bars.bottom);
            return insets;
        });

        wireGreeting();
        wireHeaderActions();
        wireHeroActions();
        wireSeeAllLinks();
        wireBottomNavigation();
        setupSuggestions();
        bindOverview();
        setupCarousel();
        startAdvisoryCycle();
    }

    @Override
    protected void onDestroy() {
        advisoryHandler.removeCallbacks(advisoryRunnable);
        super.onDestroy();
    }

    private void wireHeroActions() {
        findViewById(R.id.heroTakePhoto).setOnClickListener(v ->
                showMessage(R.string.home_snackbar_photo));
        findViewById(R.id.heroLiveScan).setOnClickListener(v ->
                showMessage(R.string.home_snackbar_scan));
        loadAssetImage((ImageView) findViewById(R.id.heroImage), ASSET_HERO_IMAGE);
    }

    private void wireGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int greetingRes = hour < 12 ? R.string.home_greeting_morning
                : hour < 17 ? R.string.home_greeting_afternoon
                : R.string.home_greeting_evening;
        TextView greeting = findViewById(R.id.homeGreeting);
        greeting.setText(getString(greetingRes) + " " + getString(R.string.home_greeting_name));
    }

    private void wireHeaderActions() {
        findViewById(R.id.notificationButton).setOnClickListener(v ->
                showMessage(R.string.home_snackbar_notifications));
    }

    private void setupSuggestions() {
        List<HomeData.Suggestion> suggestions = HomeData.suggestions();
        wireHorizontalPager(R.id.suggestionsList, R.id.suggestionsDots,
                new SuggestionAdapter(suggestions), suggestions.size());
    }

    private void startAdvisoryCycle() {
        advisories = HomeData.advisories();
        bindAdvisory(0);
        advisoryHandler.removeCallbacks(advisoryRunnable);
        advisoryHandler.postDelayed(advisoryRunnable, ADVISORY_INTERVAL_MS);
    }

    private void cycleAdvisory(int index) {
        View row = findViewById(R.id.advisoryContentRow);
        Animation out = buildAdvisorySlide(false);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                advisoryIndex = index;
                bindAdvisory(index);
                row.startAnimation(buildAdvisorySlide(true));
            }
        });
        row.startAnimation(out);
    }

    private void bindAdvisory(int index) {
        HomeData.Advisory advisory = advisories.get(index);
        ((ImageView) findViewById(R.id.advisoryIcon)).setImageResource(advisory.iconRes);
        ((TextView) findViewById(R.id.advisoryTitle)).setText(advisory.title);
        ((TextView) findViewById(R.id.advisoryBody)).setText(advisory.body);
    }

    private Animation buildAdvisorySlide(boolean in) {
        AnimationSet set = new AnimationSet(true);
        TranslateAnimation translate = in
                ? new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f)
                : new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f,
                        Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        AlphaAnimation alpha = in ? new AlphaAnimation(0f, 1f) : new AlphaAnimation(1f, 0f);
        set.addAnimation(translate);
        set.addAnimation(alpha);
        set.setDuration(ADVISORY_SLIDE_MS);
        set.setInterpolator(new DecelerateInterpolator());
        return set;
    }

    private void wireSeeAllLinks() {
        wireSeeAll(R.id.recipesHeader, R.string.home_recipes_title);
        wireSeeAll(R.id.overviewHeader, R.string.home_overview_title);
        wireSeeAll(R.id.carouselHeader, R.string.home_carousel_title);
    }

    private void wireSeeAll(int headerId, int titleRes) {
        View header = findViewById(headerId);
        if (header == null) {
            return;
        }
        TextView title = header.findViewById(R.id.sectionTitle);
        if (title != null) {
            title.setText(titleRes);
        }
        String sectionName = getString(titleRes);
        header.findViewById(R.id.sectionAction).setOnClickListener(v -> showComingSoon(sectionName));
    }

    private void wireBottomNavigation() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navHome) {
                return true;
            }
            showComingSoon(item.getTitle().toString());
            return false;
        });
    }

    private void bindOverview() {
        List<HomeData.OverviewStat> stats = HomeData.overview();
        bindOverviewCard(R.id.statItemsValue, R.id.statItemsLabel, stats.get(0));
        bindOverviewCard(R.id.statExpiringValue, R.id.statExpiringLabel, stats.get(1));
        bindOverviewCard(R.id.statWasteValue, R.id.statWasteLabel, stats.get(2));
    }

    private void bindOverviewCard(int valueId, int labelId, HomeData.OverviewStat stat) {
        ((TextView) findViewById(valueId)).setText(stat.value);
        ((TextView) findViewById(labelId)).setText(stat.labelRes);
    }

    private void setupCarousel() {
        List<HomeData.Recipe> recipes = HomeData.recipes();
        wireHorizontalPager(R.id.carouselList, R.id.pagerDots,
                new RecipeAdapter(recipes), recipes.size());
    }

    private void wireHorizontalPager(int listId, int dotsId,
                                     RecyclerView.Adapter<?> adapter, int itemCount) {
        RecyclerView list = findViewById(listId);
        LinearLayoutManager layout =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        list.setLayoutManager(layout);
        list.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int gap = getResources().getDimensionPixelSize(R.dimen.space_2);
                outRect.left = gap;
                outRect.right = gap;
            }
        });
        list.setAdapter(adapter);
        new LinearSnapHelper().attachToRecyclerView(list);

        buildPagerDots(dotsId, itemCount);
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                updatePagerDots(dotsId, nearestPosition(list, layout));
            }
        });
        list.post(() -> updatePagerDots(dotsId, nearestPosition(list, layout)));
    }

    private void buildPagerDots(int dotsId, int count) {
        LinearLayout container = findViewById(dotsId);
        container.removeAllViews();
        int size = getResources().getDimensionPixelSize(R.dimen.carousel_dot);
        int gap = getResources().getDimensionPixelSize(R.dimen.carousel_dot_gap);
        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(gap, 0, gap, 0);
            dot.setLayoutParams(params);
            container.addView(dot);
        }
    }

    private void updatePagerDots(int dotsId, int active) {
        LinearLayout container = findViewById(dotsId);
        int size = getResources().getDimensionPixelSize(R.dimen.carousel_dot);
        int activeWidth = getResources().getDimensionPixelSize(R.dimen.carousel_dot_active);
        int gap = getResources().getDimensionPixelSize(R.dimen.carousel_dot_gap);
        for (int i = 0; i < container.getChildCount(); i++) {
            View dot = container.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dot.getLayoutParams();
            params.width = i == active ? activeWidth : size;
            params.height = size;
            params.setMargins(gap, 0, gap, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(i == active
                    ? R.drawable.bg_dot_indicator_active : R.drawable.bg_dot_indicator);
        }
    }

    private int nearestPosition(RecyclerView list, LinearLayoutManager layout) {
        int center = list.getWidth() / 2;
        int first = layout.findFirstVisibleItemPosition();
        int last = layout.findLastVisibleItemPosition();
        int best = first;
        int bestDistance = Integer.MAX_VALUE;
        for (int i = first; i <= last; i++) {
            View child = layout.findViewByPosition(i);
            if (child == null) {
                continue;
            }
            int childCenter = (child.getLeft() + child.getRight()) / 2;
            int distance = Math.abs(childCenter - center);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = i;
            }
        }
        return best;
    }

    private void loadAssetImage(ImageView target, String assetPath) {
        try {
            InputStream stream = getAssets().open(assetPath);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
            target.setImageBitmap(bitmap);
        } catch (IOException e) {
            target.setImageDrawable(null);
        }
    }

    private void showComingSoon(String title) {
        showMessage(getString(R.string.home_snackbar_tab, title));
    }

    private void showMessage(int resId) {
        Snackbar.make(findViewById(R.id.main), resId, Snackbar.LENGTH_SHORT).show();
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_SHORT).show();
    }

    private final class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

        private final List<HomeData.Recipe> recipes;

        RecipeAdapter(List<HomeData.Recipe> recipes) {
            this.recipes = recipes;
        }

        @NonNull
        @Override
        public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_home_recipe_card, parent, false);
            return new RecipeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
            HomeData.Recipe recipe = recipes.get(position);
            holder.title.setText(recipe.title);
            holder.time.setText(recipe.time);
            holder.likes.setText(recipe.likes);
            holder.icon.setImageResource(recipe.artRes);
            holder.card.setOnClickListener(v -> showMessage(R.string.home_snackbar_recipe));
        }

        @Override
        public int getItemCount() {
            return recipes.size();
        }

        final class RecipeViewHolder extends RecyclerView.ViewHolder {
            final View card;
            final TextView title;
            final TextView time;
            final TextView likes;
            final ImageView icon;

            RecipeViewHolder(@NonNull View itemView) {
                super(itemView);
                card = itemView;
                title = itemView.findViewById(R.id.recipeTitle);
                time = itemView.findViewById(R.id.recipeTime);
                likes = itemView.findViewById(R.id.recipeLikes);
                icon = itemView.findViewById(R.id.recipeIcon);
            }
        }
    }

    private final class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder> {

        private final List<HomeData.Suggestion> suggestions;

        SuggestionAdapter(List<HomeData.Suggestion> suggestions) {
            this.suggestions = suggestions;
        }

        @NonNull
        @Override
        public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_home_suggestion_card, parent, false);
            return new SuggestionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
            HomeData.Suggestion suggestion = suggestions.get(position);
            holder.title.setText(suggestion.title);
            holder.time.setText(suggestion.time);
            holder.icon.setImageResource(suggestion.artRes);
            holder.uses.setText(getResources().getQuantityString(
                    R.plurals.home_suggestion_uses, suggestion.ingredientCount, suggestion.ingredientCount));
            holder.card.setOnClickListener(v -> showMessage(R.string.home_snackbar_recipe));
        }

        @Override
        public int getItemCount() {
            return suggestions.size();
        }

        final class SuggestionViewHolder extends RecyclerView.ViewHolder {
            final View card;
            final TextView title;
            final TextView time;
            final TextView uses;
            final ImageView icon;

            SuggestionViewHolder(@NonNull View itemView) {
                super(itemView);
                card = itemView;
                title = itemView.findViewById(R.id.suggestionTitle);
                time = itemView.findViewById(R.id.suggestionTime);
                uses = itemView.findViewById(R.id.suggestionUses);
                icon = itemView.findViewById(R.id.suggestionIcon);
            }
        }
    }
}
