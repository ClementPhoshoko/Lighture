package com.example.lighture;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recipesTop), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int px = getResources().getDimensionPixelSize(R.dimen.page_padding_x);
            v.setPadding(bars.left + px, bars.top, bars.right + px, 0);
            return insets;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recipesStickyHeader), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int px = getResources().getDimensionPixelSize(R.dimen.page_padding_x);
            v.setPadding(bars.left + px, v.getPaddingTop(), bars.right + px, 0);
            return insets;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recipesList), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int px = getResources().getDimensionPixelSize(R.dimen.page_padding_x);
            v.setPadding(bars.left + px, v.getPaddingTop(), bars.right + px, bars.bottom);
            return insets;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomNav), (v, insets) -> {
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
        List<Recipe> filtered = new ArrayList<>();
        for (Recipe recipe : fullRecipesList) {
            if (RecipesData.CATEGORY_ALL.equals(category) || recipe.category.equals(category)) {
                filtered.add(recipe);
            }
        }
        adapter.setRecipes(filtered);
    }

    private void wireHeaderActions() {
        findViewById(R.id.recipesSearchButton)
                .setOnClickListener(v -> showMessage(getString(R.string.recipes_snackbar_search)));
    }

    private void wireFilterRow() {
        findViewById(R.id.recipesFilterButton)
                .setOnClickListener(v -> showMessage(getString(R.string.recipes_snackbar_filter)));
        findViewById(R.id.recipesSortButton)
                .setOnClickListener(v -> showMessage(getString(R.string.recipes_snackbar_sort)));
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
