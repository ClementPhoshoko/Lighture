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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        applyInsets(view);
        wireGreeting(view);
        wireHeaderActions(view);
        wireHeroActions(view);
        wireSeeAllLinks(view);
        setupSuggestions(view);
        bindOverview(view);
        setupCarousel(view);
        startAdvisoryCycle(view);
    }

    @Override
    public void onDestroyView() {
        advisoryHandler.removeCallbacks(advisoryRunnable);
        super.onDestroyView();
    }

    private void applyInsets(View root) {
        ViewCompat.setOnApplyWindowInsetsListener(root.findViewById(R.id.homeTop), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int px = getResources().getDimensionPixelSize(R.dimen.page_padding_x);
            v.setPadding(bars.left + px, bars.top, bars.right + px, 0);
            return insets;
        });
        ViewCompat.setOnApplyWindowInsetsListener(root.findViewById(R.id.homeScroll), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, 0, bars.right, 0);
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

    private void wireGreeting(View root) {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int greetingRes = hour < 12 ? R.string.home_greeting_morning
                : hour < 17 ? R.string.home_greeting_afternoon
                : R.string.home_greeting_evening;
        TextView greeting = root.findViewById(R.id.homeGreeting);
        greeting.setText(getString(greetingRes) + " " + getString(R.string.home_greeting_name));
    }

    private void wireHeaderActions(View root) {
        root.findViewById(R.id.notificationButton).setOnClickListener(v ->
                showMessage(R.string.home_snackbar_notifications));
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
        ((ImageView) root.findViewById(R.id.advisoryIcon)).setImageResource(advisory.iconRes);
        ((TextView) root.findViewById(R.id.advisoryTitle)).setText(advisory.title);
        ((TextView) root.findViewById(R.id.advisoryBody)).setText(advisory.body);
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
        bindOverviewCard(root, R.id.statItemsValue, R.id.statItemsLabel, stats.get(0));
        bindOverviewCard(root, R.id.statExpiringValue, R.id.statExpiringLabel, stats.get(1));
        bindOverviewCard(root, R.id.statWasteValue, R.id.statWasteLabel, stats.get(2));
    }

    private void bindOverviewCard(View root, int valueId, int labelId, HomeData.OverviewStat stat) {
        ((TextView) root.findViewById(valueId)).setText(stat.value);
        ((TextView) root.findViewById(labelId)).setText(stat.labelRes);
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
                int count = parent.getAdapter() != null ? parent.getAdapter().getItemCount() : 0;
                outRect.left = position == 0 ? 0 : gap;
                outRect.right = position == count - 1 ? 0 : gap;
            }
        });
        list.setAdapter(adapter);
        new LinearSnapHelper().attachToRecyclerView(list);

        buildPagerDots(root, dotsId, itemCount);
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                updatePagerDots(root, dotsId, nearestPosition(list, layout));
            }
        });
        list.post(() -> updatePagerDots(root, dotsId, nearestPosition(list, layout)));
    }

    private void buildPagerDots(View root, int dotsId, int count) {
        LinearLayout container = root.findViewById(dotsId);
        container.removeAllViews();
        int size = getResources().getDimensionPixelSize(R.dimen.carousel_dot);
        int gap = getResources().getDimensionPixelSize(R.dimen.carousel_dot_gap);
        for (int i = 0; i < count; i++) {
            View dot = new View(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(gap, 0, gap, 0);
            dot.setLayoutParams(params);
            container.addView(dot);
        }
    }

    private void updatePagerDots(View root, int dotsId, int active) {
        LinearLayout container = root.findViewById(dotsId);
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
            if (child == null) continue;
            int childCenter = (child.getLeft() + child.getRight()) / 2;
            int distance = Math.abs(childCenter - center);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = i;
            }
        }
        return best;
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
