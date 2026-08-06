package com.example.lighture;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public final class HomeRecipeAdapter extends RecyclerView.Adapter<HomeRecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(HomeData.Recipe recipe);
    }

    private static final String ASSET_MOCK_DISH_IMAGE = "salad_image.png";

    private final List<HomeData.Recipe> recipes;
    private final OnRecipeClickListener listener;

    public HomeRecipeAdapter(List<HomeData.Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
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
        Context context = holder.itemView.getContext();
        holder.title.setText(recipe.title);
        holder.time.setText(recipe.time);
        holder.likes.setText(recipe.likes);
        ImageUtils.loadAssetImage(context, holder.icon, ASSET_MOCK_DISH_IMAGE);
        holder.tag.setText(recipe.tag);
        holder.tag.getBackground().mutate()
                .setTintList(ColorStateList.valueOf(context.getColor(recipe.tagBgRes)));
        holder.tag.setTextColor(context.getColor(recipe.tagTextRes));
        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static final class RecipeViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView time;
        final TextView likes;
        final TextView tag;
        final ImageView icon;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.recipeTitle);
            time = itemView.findViewById(R.id.recipeTime);
            likes = itemView.findViewById(R.id.recipeLikes);
            tag = itemView.findViewById(R.id.recipeTag);
            icon = itemView.findViewById(R.id.recipeIcon);
        }
    }
}
