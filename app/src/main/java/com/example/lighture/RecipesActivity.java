package com.example.lighture;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Recipes screen: category chips, filter/sort row and the recipe list over the
 * shared bottom navigation. Chip selection filters the catalogue; search,
 * filter and sort remain stubs until their pipelines land.
 */
public class RecipesActivity extends AppCompatActivity {

    private final List<Recipe> fullRecipesList = new ArrayList<>();
    private RecipesAdapter adapter;
    private String currentCategory = RecipesData.CATEGORY_ALL;
    private String currentSearchQuery = "";
    private String currentSortMode = "Recommended";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipes);

        applyInsets();
        setupRecipesList();
        wireChips();
        wireHeaderActions();
        wireFilterRow();
        wireBottomNavigation();
        animateContentIn();
    }

    private void animateContentIn() {
        findViewById(R.id.recipesTop).startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.activity_content_in));
        findViewById(R.id.recipesStickyHeader).startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.activity_content_in));
        findViewById(R.id.recipesList).startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.activity_content_in));
    }

    private void applyInsets() {
        View recipesTop = findViewById(R.id.recipesTop);
        int topInitialPaddingStart = recipesTop.getPaddingStart();
        int topInitialPaddingEnd = recipesTop.getPaddingEnd();
        int topInitialPaddingTop = recipesTop.getPaddingTop();

        ViewCompat.setOnApplyWindowInsetsListener(recipesTop, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left + topInitialPaddingStart, bars.top + topInitialPaddingTop, bars.right + topInitialPaddingEnd, 0);
            return insets;
        });

        View stickyHeader = findViewById(R.id.recipesStickyHeader);
        int stickyInitialPaddingStart = stickyHeader.getPaddingStart();
        int stickyInitialPaddingEnd = stickyHeader.getPaddingEnd();

        ViewCompat.setOnApplyWindowInsetsListener(stickyHeader, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left + stickyInitialPaddingStart, v.getPaddingTop(), bars.right + stickyInitialPaddingEnd, 0);
            return insets;
        });

        View recipesList = findViewById(R.id.recipesList);
        int listInitialPaddingStart = recipesList.getPaddingStart();
        int listInitialPaddingEnd = recipesList.getPaddingEnd();
        int listInitialPaddingBottom = recipesList.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(recipesList, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left + listInitialPaddingStart, v.getPaddingTop(), bars.right + listInitialPaddingEnd, bars.bottom + listInitialPaddingBottom);
            return insets;
        });

        View bottomNav = findViewById(R.id.bottomNav);
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, 0, bars.right, bars.bottom);
            return insets;
        });
    }

    private void setupRecipesList() {
        fullRecipesList.addAll(RecipesData.all());
        adapter = new RecipesAdapter(fullRecipesList, new RecipesAdapter.OnRecipeActionListener() {
            @Override
            public void onRecipeClick(Recipe recipe) {
                showMessage(getString(R.string.home_snackbar_recipe));
            }

            @Override
            public void onGenerateRecipes() {
                showMessage(getString(R.string.recipes_snackbar_generate));
            }
        });

        RecyclerView list = findViewById(R.id.recipesList);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }

    private void wireChips() {
        List<CategoryAdapter.Category> categories = new ArrayList<>();
        categories.add(new CategoryAdapter.Category(RecipesData.CATEGORY_ALL, R.drawable.ic_grid, true));
        categories.add(new CategoryAdapter.Category(RecipesData.CATEGORY_QUICK, R.drawable.ic_clock, false));
        categories.add(new CategoryAdapter.Category(RecipesData.CATEGORY_VEGETARIAN, R.drawable.ic_leaf, false));
        categories.add(new CategoryAdapter.Category(RecipesData.CATEGORY_HIGH_PROTEIN, R.drawable.ic_dumbbell, false));
        categories.add(new CategoryAdapter.Category(RecipesData.CATEGORY_LOW_WASTE, R.drawable.ic_recycle, false));

        CategoryAdapter categoryAdapter = new CategoryAdapter(categories, category -> filter(category.name));
        RecyclerView categoryList = findViewById(R.id.recipesCategoryList);
        categoryList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryList.setAdapter(categoryAdapter);
    }

    private void filter(String category) {
        currentCategory = category;
        applyFiltersAndSort();
    }

    private void applyFiltersAndSort() {
        List<Recipe> filtered = new ArrayList<>();
        for (Recipe recipe : fullRecipesList) {
            boolean matchesCategory = RecipesData.CATEGORY_ALL.equals(currentCategory) || recipe.category.equals(currentCategory);
            boolean matchesSearch = currentSearchQuery.isEmpty() || 
                    recipe.title.toLowerCase().contains(currentSearchQuery) || 
                    recipe.description.toLowerCase().contains(currentSearchQuery);
            
            if (matchesCategory && matchesSearch) {
                filtered.add(recipe);
            }
        }

        // Apply Sorting
        if (getString(R.string.recipes_sort_fastest).equals(currentSortMode)) {
            filtered.sort((r1, r2) -> {
                int t1 = extractMinutes(r1.cookingTime);
                int t2 = extractMinutes(r2.cookingTime);
                return Integer.compare(t1, t2);
            });
        } else if (getString(R.string.recipes_sort_newest).equals(currentSortMode)) {
            // Placeholder: newest means reverse order of initial list
            List<Recipe> reversed = new ArrayList<>();
            for (int i = filtered.size() - 1; i >= 0; i--) {
                reversed.add(filtered.get(i));
            }
            filtered = reversed;
        }

        adapter.setRecipes(filtered);
    }

    private int extractMinutes(String time) {
        try {
            return Integer.parseInt(time.split(" ")[0]);
        } catch (Exception e) {
            return 999;
        }
    }

    private void wireHeaderActions() {
        View root = findViewById(R.id.recipesHeaderRoot);
        View titleContainer = findViewById(R.id.recipesTitleContainer);
        View searchContainer = findViewById(R.id.recipesSearchBarContainer);
        EditText searchInput = findViewById(R.id.recipesSearchInput);

        findViewById(R.id.recipesSearchButton).setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition((ViewGroup) root);
            titleContainer.setVisibility(View.GONE);
            searchContainer.setVisibility(View.VISIBLE);
            searchInput.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
        });

        findViewById(R.id.recipesSearchClose).setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition((ViewGroup) root);
            searchContainer.setVisibility(View.GONE);
            titleContainer.setVisibility(View.VISIBLE);
            searchInput.setText("");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                currentSearchQuery = s.toString().toLowerCase().trim();
                applyFiltersAndSort();
            }
        });
    }

    private void wireFilterRow() {
        findViewById(R.id.recipesFilterButton)
                .setOnClickListener(v -> showMessage(getString(R.string.recipes_snackbar_filter)));
        
        View sortButton = findViewById(R.id.recipesSortButton);
        TextView sortValueLabel = sortButton.findViewById(R.id.recipesSortValueLabel);
        
        sortButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, sortButton);
            popup.getMenu().add(getString(R.string.recipes_sort_recommended));
            popup.getMenu().add(getString(R.string.recipes_sort_fastest));
            popup.getMenu().add(getString(R.string.recipes_sort_newest));
            
            popup.setOnMenuItemClickListener(item -> {
                currentSortMode = item.getTitle().toString();
                sortValueLabel.setText(currentSortMode);
                applyFiltersAndSort();
                return true;
            });
            popup.show();
        });
    }

    private void wireBottomNavigation() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navRecipes) {
                return true;
            }
            if (itemId == R.id.navHome) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            showMessage(getString(R.string.home_snackbar_tab, item.getTitle().toString()));
            return false;
        });
        nav.setSelectedItemId(R.id.navRecipes);
    }

    private void showMessage(String message) {
        View root = findViewById(R.id.recipesRoot);
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
    }
}
