package com.example.lighture;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public final class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public static final class Category {
        public final String name;
        public final int iconRes;
        public boolean isSelected;

        public Category(String name, int iconRes, boolean isSelected) {
            this.name = name;
            this.iconRes = iconRes;
            this.isSelected = isSelected;
        }
    }

    private final List<Category> categories;
    private final OnCategoryClickListener listener;

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.name.setText(category.name);
        holder.icon.setImageResource(category.iconRes);
        holder.iconContainer.setSelected(category.isSelected);
        holder.name.setSelected(category.isSelected);

        holder.itemView.setOnClickListener(v -> {
            for (Category c : categories) {
                c.isSelected = false;
            }
            category.isSelected = true;
            notifyDataSetChanged();
            listener.onCategoryClick(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static final class CategoryViewHolder extends RecyclerView.ViewHolder {
        final View iconContainer;
        final ImageView icon;
        final TextView name;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            iconContainer = itemView.findViewById(R.id.categoryIconContainer);
            icon = itemView.findViewById(R.id.categoryIcon);
            name = itemView.findViewById(R.id.categoryName);
        }
    }
}
