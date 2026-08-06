package com.example.lighture;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Binds the recipe catalogue to the Recipes screen list. Ingredients render as
 * up to three compact pills with a "+N" overflow pill; the count badge reuses
 * the shared "Uses N ingredients" plural.
 */
public final class RecipesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnRecipeActionListener {
        void onRecipeClick(Recipe recipe);

        void onGenerateRecipes();
    }

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_RECIPE = 1;
    private static final int TYPE_EMPTY = 2;

    private static final int MAX_INGREDIENT_PILLS = 3;

    private static final String ASSET_RECIPE_IMAGE = "salad_image.png";
    private static final String ASSET_EMPTY_IMAGE = "robot_chef.png";

    private final List<Recipe> recipes;
    private final OnRecipeActionListener listener;

    public RecipesAdapter(List<Recipe> recipes, OnRecipeActionListener listener) {
        this.recipes = new ArrayList<>(recipes);
        this.listener = listener;
    }

    @NonNull
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return recipes.isEmpty() ? TYPE_EMPTY : TYPE_RECIPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_recipes_ai_card, parent, false);
            view.findViewById(R.id.recipesAiGenerateButton)
                    .setOnClickListener(v -> listener.onGenerateRecipes());
            return new HeaderViewHolder(view);
        }
        if (viewType == TYPE_EMPTY) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_recipes_empty_state, parent, false);
            return new EmptyViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_recipe_list_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_HEADER) {
            return;
        }
        if (type == TYPE_EMPTY) {
            EmptyViewHolder emptyHolder = (EmptyViewHolder) holder;
            ImageUtils.loadAssetImage(emptyHolder.itemView.getContext(),
                    emptyHolder.image, ASSET_EMPTY_IMAGE);
            return;
        }
        Recipe recipe = recipes.get(position - 1);
        RecipeViewHolder recipeHolder = (RecipeViewHolder) holder;
        Context context = recipeHolder.itemView.getContext();
        recipeHolder.title.setText(recipe.title);
        recipeHolder.description.setText(recipe.description);
        recipeHolder.time.setText(recipe.cookingTime);
        ImageUtils.loadAssetImage(context, recipeHolder.image, ASSET_RECIPE_IMAGE);

        int count = recipe.ingredients.size();
        recipeHolder.count.setText(context.getResources()
                .getQuantityString(R.plurals.home_suggestion_uses, count, count));
        recipeHolder.count.getBackground().mutate().setTintList(ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.tag_easy_background)));
        recipeHolder.count.setTextColor(ContextCompat.getColor(context, R.color.tag_easy_text));

        bindIngredients(recipeHolder.ingredients, recipe);
        bindFavorite(recipeHolder.heart, recipeHolder.heartIcon, recipe);

        recipeHolder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
        recipeHolder.arrow.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    private void bindIngredients(LinearLayout container, Recipe recipe) {
        container.removeAllViews();
        int visible = Math.min(recipe.ingredients.size(), MAX_INGREDIENT_PILLS);
        for (int i = 0; i < visible; i++) {
            container.addView(buildIngredientPill(container.getContext(), recipe.ingredients.get(i)));
        }
        int remaining = recipe.ingredients.size() - visible;
        if (remaining > 0) {
            container.addView(buildIngredientPill(container.getContext(),
                    container.getContext().getString(R.string.recipes_more_ingredients, remaining)));
        }
    }

    private TextView buildIngredientPill(Context context, String text) {
        TextView pill = new TextView(context);
        pill.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        pill.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_tag_pill));
        pill.getBackground().mutate().setTintList(ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.chip_background)));
        pill.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
        pill.setTextSize(11);
        pill.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        pill.setText(text);
        pill.setPadding(dp(context, 6), dp(context, 2), dp(context, 6), dp(context, 2));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMarginEnd(dp(context, 6));
        pill.setLayoutParams(params);
        return pill;
    }

    private int dp(Context context, int value) {
        return Math.round(context.getResources().getDisplayMetrics().density * value);
    }

    private void bindFavorite(View heartButton, ImageView heartIcon, Recipe recipe) {
        heartButton.setOnClickListener(v -> {
            recipe.isFavorite = !recipe.isFavorite;
            bindFavoriteIcon(heartIcon, recipe);
        });
        bindFavoriteIcon(heartIcon, recipe);
    }

    private void bindFavoriteIcon(ImageView heartIcon, Recipe recipe) {
        heartIcon.setImageResource(recipe.isFavorite ? R.drawable.ic_heart : R.drawable.ic_heart_outline);
        heartIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(
                heartIcon.getContext(),
                recipe.isFavorite ? R.color.brand_primary : R.color.icon_secondary)));
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes.clear();
        this.recipes.addAll(recipes);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return recipes.isEmpty() ? 2 : recipes.size() + 1;
    }

    static final class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static final class EmptyViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;

        EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.recipesEmptyImage);
        }
    }

    static final class RecipeViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView description;
        final TextView time;
        final TextView count;
        final ImageView image;
        final ImageView heartIcon;
        final View heart;
        final View arrow;
        final LinearLayout ingredients;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.recipeItemTitle);
            description = itemView.findViewById(R.id.recipeItemDescription);
            time = itemView.findViewById(R.id.recipeItemTime);
            count = itemView.findViewById(R.id.recipeItemCount);
            image = itemView.findViewById(R.id.recipeItemImage);
            heartIcon = itemView.findViewById(R.id.recipeItemHeartIcon);
            heart = itemView.findViewById(R.id.recipeItemHeart);
            arrow = itemView.findViewById(R.id.recipeItemArrow);
            ingredients = itemView.findViewById(R.id.recipeItemIngredients);
        }
    }
}
