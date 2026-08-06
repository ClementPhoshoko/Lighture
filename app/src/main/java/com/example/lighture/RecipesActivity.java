package com.example.lighture;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Recipes screen: category chips, filter/sort row and the recipe list over the
 * shared bottom navigation. Chip selection filters the catalogue; search,
 * filter and sort remain stubs until their pipelines land.
 */
public class RecipesActivity extends AppCompatActivity {

    private final List<Recipe> recipes = new ArrayList<>();
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
    }

    private void applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recipesTop), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int px = getResources().getDimensionPixelSize(R.dimen.page_padding_x);
            v.setPadding(bars.left + px, bars.top, bars.right + px, 0);
            return insets;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recipesScroll), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, 0, bars.right, 0);
            return insets;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomNav), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, 0, bars.right, bars.bottom);
            return insets;
        });
    }

    private void setupRecipesList() {
        recipes.addAll(RecipesData.all());
        adapter = new RecipesAdapter(recipes, recipe ->
                showMessage(getString(R.string.home_snackbar_recipe)));

        RecyclerView list = findViewById(R.id.recipesList);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }

    private void wireChips() {
        ChipGroup chips = findViewById(R.id.recipesCategoryChips);
        chips.setOnCheckedStateChangeListener((group, checkedIds) -> {
            String category = RecipesData.CATEGORY_ALL;
            if (!checkedIds.isEmpty()) {
                Chip chip = group.findViewById(checkedIds.get(0));
                if (chip != null) {
                    category = chip.getText().toString();
                }
            }
            filter(category);
        });
    }

    private void filter(String category) {
        List<Recipe> filtered = new ArrayList<>();
        for (Recipe recipe : recipes) {
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
