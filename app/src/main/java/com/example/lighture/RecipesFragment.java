package com.example.lighture;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class RecipesFragment extends Fragment {

    private final List<Recipe> fullRecipesList = new ArrayList<>();
    private RecipesAdapter adapter;
    private String currentCategory = RecipesData.CATEGORY_ALL;
    private String currentSearchQuery = "";
    private String currentSortMode = "Recommended";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        applyInsets(view);
        setupRecipesList(view);
        wireChips(view);
        wireHeaderActions(view);
        wireFilterRow(view);
        animateContentIn(view);
    }

    private void animateContentIn(View root) {
        root.findViewById(R.id.recipesTop).startAnimation(
                AnimationUtils.loadAnimation(requireContext(), R.anim.activity_content_in));
        root.findViewById(R.id.recipesStickyHeader).startAnimation(
                AnimationUtils.loadAnimation(requireContext(), R.anim.activity_content_in));
        root.findViewById(R.id.recipesList).startAnimation(
                AnimationUtils.loadAnimation(requireContext(), R.anim.activity_content_in));
    }

    private void applyInsets(View root) {
        View headerContainer = root.findViewById(R.id.recipesHeaderContainer);
        View recipesTop = root.findViewById(R.id.recipesTop);
        View recipesList = root.findViewById(R.id.recipesList);

        int topInitialPaddingStart = recipesTop.getPaddingStart();
        int topInitialPaddingEnd = recipesTop.getPaddingEnd();
        int topInitialPaddingTop = recipesTop.getPaddingTop();

        int listInitialPaddingStart = recipesList.getPaddingStart();
        int listInitialPaddingEnd = recipesList.getPaddingEnd();
        int listInitialPaddingBottom = recipesList.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(headerContainer, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            
            // Apply status bar padding to the internal top row
            recipesTop.setPadding(bars.left + topInitialPaddingStart, 
                               bars.top + topInitialPaddingTop, 
                               bars.right + topInitialPaddingEnd, 0);

            // In Strict Clipping mode, the list sits below the header. 
            // We only need to handle bottom navigation insets here.
            recipesList.setPadding(bars.left + listInitialPaddingStart, 
                                0, // No top padding needed as it's below header
                                bars.right + listInitialPaddingEnd, 
                                bars.bottom + listInitialPaddingBottom);

            return insets;
        });
    }

    private void setupRecipesList(View root) {
        fullRecipesList.clear();
        fullRecipesList.addAll(RecipesData.all());
        adapter = new RecipesAdapter(fullRecipesList, new RecipesAdapter.OnRecipeActionListener() {
            @Override
            public void onRecipeClick(Recipe recipe) {
                showMessage(R.string.home_snackbar_recipe);
            }

            @Override
            public void onGenerateRecipes() {
                showMessage(R.string.recipes_snackbar_generate);
            }
        });

        RecyclerView list = root.findViewById(R.id.recipesList);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list.setAdapter(adapter);
    }

    private void wireChips(View root) {
        List<CategoryAdapter.Category> categories = new ArrayList<>();
        categories.add(new CategoryAdapter.Category(RecipesData.CATEGORY_ALL, R.drawable.ic_grid, true));
        categories.add(new CategoryAdapter.Category(RecipesData.CATEGORY_QUICK, R.drawable.ic_clock, false));
        categories.add(new CategoryAdapter.Category(RecipesData.CATEGORY_VEGETARIAN, R.drawable.ic_leaf, false));
        categories.add(new CategoryAdapter.Category(RecipesData.CATEGORY_HIGH_PROTEIN, R.drawable.ic_dumbbell, false));
        categories.add(new CategoryAdapter.Category(RecipesData.CATEGORY_LOW_WASTE, R.drawable.ic_recycle, false));

        FilterChipAdapter chipAdapter = new FilterChipAdapter(categories, category -> filter(category.name));
        RecyclerView categoryList = root.findViewById(R.id.filterChipRecyclerView);
        categoryList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                int count = state.getItemCount();
                if (position < count - 1) {
                    outRect.right = getResources().getDimensionPixelSize(R.dimen.space_2);
                }
            }
        });
        categoryList.setAdapter(chipAdapter);
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

    private void wireHeaderActions(View root) {
        View filtersRoot = root.findViewById(R.id.recipesFiltersRoot);
        View sortContainer = root.findViewById(R.id.recipesSortContainer);
        View searchContainer = root.findViewById(R.id.recipesSearchBarContainer);
        EditText searchInput = root.findViewById(R.id.recipesSearchInput);
        ImageButton searchClose = root.findViewById(R.id.recipesSearchClose);

        root.findViewById(R.id.recipesSearchButton).setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition((ViewGroup) filtersRoot);
            sortContainer.setVisibility(View.GONE);
            searchContainer.setVisibility(View.VISIBLE);
            searchClose.setImageResource(R.drawable.ic_chevron_right);
            searchInput.requestFocus();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
        });

        searchClose.setOnClickListener(v -> {
            if (searchInput.getText().length() > 0) {
                searchInput.setText("");
            } else {
                TransitionManager.beginDelayedTransition((ViewGroup) filtersRoot);
                searchContainer.setVisibility(View.GONE);
                sortContainer.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
            }
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

                if (s.length() > 0) {
                    searchClose.setImageResource(R.drawable.ic_close);
                } else {
                    searchClose.setImageResource(R.drawable.ic_chevron_right);
                }
            }
        });
    }

    private void wireFilterRow(View root) {
        View sortButton = root.findViewById(R.id.recipesSortButton);
        TextView sortValueLabel = sortButton.findViewById(R.id.recipesSortValueLabel);
        
        sortButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireContext(), sortButton);
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

    private void showMessage(int resId) {
        View view = getView();
        if (view != null) {
            Snackbar.make(view, resId, Snackbar.LENGTH_SHORT).show();
        }
    }
}
