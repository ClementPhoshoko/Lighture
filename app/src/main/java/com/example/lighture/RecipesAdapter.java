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

import java.util.List;

/**
 * Binds the recipe catalogue to the Recipes screen list. Ingredients render as
 * up to three compact pills with a "+N" overflow pill; the count badge reuses
 * the shared "Uses N ingredients" plural.
 */
public final class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {

    public interface OnRecipeActionListener {
        void onRecipeClick(Recipe recipe);
    }

    private static final int MAX_INGREDIENT_PILLS = 3;

    private final List<Recipe> recipes;
    private final OnRecipeActionListener listener;

    public RecipesAdapter(List<Recipe> recipes, OnRecipeActionListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_recipe_list_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        Context context = holder.itemView.getContext();
        holder.title.setText(recipe.title);
        holder.description.setText(recipe.description);
        holder.time.setText(recipe.cookingTime);
        holder.image.setImageResource(recipe.imageRes);

        int count = recipe.ingredients.size();
        holder.count.setText(context.getResources()
                .getQuantityString(R.plurals.home_suggestion_uses, count, count));
        holder.count.getBackground().mutate().setTintList(ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.tag_easy_background)));
        holder.count.setTextColor(ContextCompat.getColor(context, R.color.tag_easy_text));

        bindIngredients(holder.ingredients, recipe);
        bindFavorite(holder.heart, holder.heartIcon, recipe);

        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
        holder.arrow.setOnClickListener(v -> listener.onRecipeClick(recipe));
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
        return recipes.size();
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
