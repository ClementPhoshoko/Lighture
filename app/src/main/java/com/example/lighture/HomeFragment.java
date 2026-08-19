package com.example.lighture;

import android.content.Intent;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

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
    private HeaderViewModel headerViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        headerViewModel = new ViewModelProvider(requireActivity()).get(HeaderViewModel.class);

        applyInsets(view);
        setupHeader();
        wireHeroActions(view);
        wireSeeAllLinks(view);
        setupSuggestions(view);
        bindOverview(view);
        setupCarousel(view);
        startAdvisoryCycle(view);
    }

    private void setupHeader() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int greetingRes = hour < 12 ? R.string.home_greeting_morning
                : hour < 17 ? R.string.home_greeting_afternoon
                : R.string.home_greeting_evening;
        String title = getString(greetingRes) + " " + getString(R.string.home_greeting_name);
        String subtitle = getString(R.string.home_subtitle);

        headerViewModel.updateState(new HeaderViewModel.HeaderState(
                title,
                subtitle,
                true,
                R.drawable.ic_bell,
                null,
                true
        ));

        headerViewModel.actionClicked.observe(getViewLifecycleOwner(), clicked -> {
            if (clicked != null && clicked) {
                showMessage(R.string.home_snackbar_notifications);
                headerViewModel.consumeActionClick();
            }
        });
    }

    @Override
    public void onDestroyView() {
        advisoryHandler.removeCallbacks(advisoryRunnable);
        super.onDestroyView();
    }

    private void applyInsets(View root) {
        View scroll = root.findViewById(R.id.homeScroll);
        int initialPaddingStart = scroll.getPaddingStart();
        int initialPaddingEnd = scroll.getPaddingEnd();
        int initialPaddingBottom = scroll.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(scroll, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left + initialPaddingStart,
                        0,
                        bars.right + initialPaddingEnd,
                        bars.bottom + initialPaddingBottom);
            return insets;
        });
    }

    private void wireHeroActions(View root) {
        root.findViewById(R.id.heroTakePhoto).setOnClickListener(v ->
                showMessage(R.string.home_snackbar_photo));
        root.findViewById(R.id.heroLiveScan).setOnClickListener(v ->
                showMessage(R.string.home_snackbar_scan));
        ImageUtils.loadAssetImage(requireContext(), root.findViewById(R.id.heroImage), ASSET_HERO_IMAGE);
    }



    private void setupSuggestions(View root) {
        List<HomeData.Suggestion> suggestions = HomeData.suggestions();
        wireHorizontalPager(root, R.id.suggestionsList, R.id.suggestionsDots,
                new HomeSuggestionAdapter(suggestions, suggestion -> showMessage(R.string.home_snackbar_recipe)),
                suggestions.size());
    }

    private void startAdvisoryCycle(View root) {
        advisories = HomeData.advisories();
        bindAdvisory(root, 0);
        advisoryHandler.removeCallbacks(advisoryRunnable);
        advisoryHandler.postDelayed(advisoryRunnable, ADVISORY_INTERVAL_MS);
    }

    private void cycleAdvisory(int index) {
        View view = getView();
        if (view == null) return;
        View row = view.findViewById(R.id.advisoryContentRow);
        if (row == null) return;
        Animation out = buildAdvisorySlide(false);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                advisoryIndex = index;
                bindAdvisory(view, index);
                row.startAnimation(buildAdvisorySlide(true));
            }
        });
        row.startAnimation(out);
    }

    private void bindAdvisory(View root, int index) {
        if (advisories == null || index >= advisories.size()) return;
        HomeData.Advisory advisory = advisories.get(index);
        View card = root.findViewById(R.id.homeAdvisory);
        ((ImageView) card.findViewById(R.id.advisoryIcon)).setImageResource(advisory.iconRes);
        ((TextView) card.findViewById(R.id.advisoryTitle)).setText(advisory.title);
        ((TextView) card.findViewById(R.id.advisoryBody)).setText(advisory.body);
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

    private void wireSeeAllLinks(View root) {
        wireSeeAll(root, R.id.recipesHeader, R.string.home_recipes_title);
        wireSeeAll(root, R.id.overviewHeader, R.string.home_overview_title);
        wireSeeAll(root, R.id.carouselHeader, R.string.home_carousel_title);
    }

    private void wireSeeAll(View root, int headerId, int titleRes) {
        View header = root.findViewById(headerId);
        if (header == null) return;
        
        TextView title = header.findViewById(R.id.sectionTitle);
        if (title != null) {
            title.setText(titleRes);
        }
        String sectionName = getString(titleRes);
        header.findViewById(R.id.sectionAction).setOnClickListener(v -> showComingSoon(sectionName));
    }

    private void bindOverview(View root) {
        List<HomeData.OverviewStat> stats = HomeData.overview();

        bindGlassStatCard(root.findViewById(R.id.statItemsCard),
                stats.get(0),
                R.color.fresh_color,
                R.color.glass_tint_success,
                R.color.glass_blob_success,
                R.drawable.ic_outline_basket,
                R.string.home_overview_badge_items);

        bindGlassStatCard(root.findViewById(R.id.statExpiringCard),
                stats.get(1),
                R.color.expiring_color,
                R.color.glass_tint_warning,
                R.color.glass_blob_warning,
                R.drawable.ic_outline_clock,
                R.string.home_overview_badge_expiring);

        bindGlassStatCard(root.findViewById(R.id.statWasteCard),
                stats.get(2),
                R.color.expired_color,
                R.color.glass_tint_danger,
                R.color.glass_blob_danger,
                R.drawable.ic_outline_trash,
                R.string.home_overview_badge_waste);
    }

    private void bindGlassStatCard(View cardRoot,
                                  HomeData.OverviewStat stat,
                                  int accentColorRes,
                                  int tintColorRes,
                                  int blobColorRes,
                                  int iconRes,
                                  int pillTextRes) {
        if (cardRoot == null) return;

        int accentColor = getResources().getColor(accentColorRes, null);
        int tintColor = getResources().getColor(tintColorRes, null);
        int blobColor = getResources().getColor(blobColorRes, null);

        // Background and decorative blob
        View bg = cardRoot.findViewById(R.id.cardBackground);
        if (bg != null) bg.setBackgroundTintList(android.content.res.ColorStateList.valueOf(tintColor));
        
        ImageView blob = cardRoot.findViewById(R.id.decorativeBlob);
        if (blob != null) blob.setColorFilter(blobColor);

        ImageView glow = cardRoot.findViewById(R.id.ambientGlow);
        if (glow != null) glow.setColorFilter(blobColor);

        // Icon and Menu
        ImageView icon = cardRoot.findViewById(R.id.statIcon);
        if (icon != null) {
            icon.setImageResource(iconRes);
            icon.setColorFilter(accentColor);
        }
        
        TextView menu = cardRoot.findViewById(R.id.menuIndicator);
        if (menu != null) menu.setTextColor(accentColor);

        // Stats
        TextView value = cardRoot.findViewById(R.id.statValue);
        if (value != null) value.setText(stat.value);
        
        TextView label = cardRoot.findViewById(R.id.statLabel);
        if (label != null) label.setText(stat.labelRes);

        // Pill
        TextView pText = cardRoot.findViewById(R.id.pillText);
        if (pText != null) {
            pText.setText(pillTextRes);
            pText.setTextColor(accentColor);
        }

        cardRoot.setOnClickListener(v -> showComingSoon(getString(stat.labelRes)));
    }

    private void setupCarousel(View root) {
        List<HomeData.Recipe> recipes = HomeData.recipes();
        wireHorizontalPager(root, R.id.carouselList, R.id.pagerDots,
                new HomeRecipeAdapter(recipes, recipe -> showMessage(R.string.home_snackbar_recipe)),
                recipes.size());
    }

    private void wireHorizontalPager(View root, int listId, int dotsId,
                                     RecyclerView.Adapter<?> adapter, int itemCount) {
        RecyclerView list = root.findViewById(listId);
        LinearLayoutManager layout =
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        list.setLayoutManager(layout);
        list.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int gap = getResources().getDimensionPixelSize(R.dimen.space_2);
                int position = parent.getChildAdapterPosition(view);
                outRect.left = position == 0 ? 0 : gap;
                outRect.right = 0;
            }
        });
        list.setAdapter(adapter);
        new LinearSnapHelper().attachToRecyclerView(list);

        final LinearLayout dotsContainer = root.findViewById(dotsId);

        buildPagerDots(root, dotsId, itemCount);
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                updateActiveDotAlpha(rv, dotsContainer, itemCount);
            }
        });
        // Ensure initial state is correct
        list.post(() -> updateActiveDotAlpha(list, dotsContainer, itemCount));
    }

    private void buildPagerDots(View root, int dotsId, int count) {
        LinearLayout container = root.findViewById(dotsId);
        if (container == null) return;
        container.removeAllViews();
        int size = getResources().getDimensionPixelSize(R.dimen.carousel_dot);
        int gap = getResources().getDimensionPixelSize(R.dimen.carousel_dot_gap);
        for (int i = 0; i < count; i++) {
            FrameLayout dotFrame = new FrameLayout(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(gap, 0, gap, 0);
            dotFrame.setLayoutParams(params);

            // 1. Inactive hollow dot
            View inactive = new View(requireContext());
            inactive.setLayoutParams(new FrameLayout.LayoutParams(size, size));
            inactive.setBackgroundResource(R.drawable.bg_dot_indicator);
            dotFrame.addView(inactive);

            // 2. Active solid dot (initially invisible)
            View active = new View(requireContext());
            active.setLayoutParams(new FrameLayout.LayoutParams(size, size));
            active.setBackgroundResource(R.drawable.bg_dot_indicator_active);
            active.setAlpha(0f);
            active.setTag("active_layer");
            dotFrame.addView(active);

            container.addView(dotFrame);
        }
    }

    private void updateActiveDotAlpha(RecyclerView rv, LinearLayout container, int count) {
        if (container == null || count <= 1) return;

        int itemWidth = getResources().getDimensionPixelSize(R.dimen.recipe_card_width);
        int gap = getResources().getDimensionPixelSize(R.dimen.space_2);
        int totalPageWidth = itemWidth + gap;

        int scrollOffset = rv.computeHorizontalScrollOffset();
        float progress = (float) scrollOffset / totalPageWidth;

        for (int i = 0; i < container.getChildCount(); i++) {
            View dotFrame = container.getChildAt(i);
            if (dotFrame instanceof ViewGroup) {
                View activeLayer = dotFrame.findViewWithTag("active_layer");
                if (activeLayer != null) {
                    // Calculate alpha based on proximity to the current page
                    float distance = Math.abs(progress - i);
                    float alpha = Math.max(0, 1f - distance);
                    activeLayer.setAlpha(alpha);
                }
            }
        }
    }

    private void showComingSoon(String title) {
        showMessage(getString(R.string.home_snackbar_tab, title));
    }

    private void showMessage(int resId) {
        View view = getView();
        if (view != null) {
            Snackbar.make(view, resId, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showMessage(String message) {
        View view = getView();
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
