package com.example.lighture;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Reusable adapter for horizontal filter chip bars.
 * Used by Recipes and Fridge screens for category filtering.
 */
public final class FilterChipAdapter extends RecyclerView.Adapter<FilterChipAdapter.FilterViewHolder> {

    public interface OnFilterClickListener {
        void onFilterClick(CategoryAdapter.Category category);
    }

    private final List<CategoryAdapter.Category> categories;
    private final OnFilterClickListener listener;

    public FilterChipAdapter(List<CategoryAdapter.Category> categories, OnFilterClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter_chip, parent, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        CategoryAdapter.Category category = categories.get(position);
        holder.label.setText(category.name);
        
        if (category.isSelected) {
            holder.label.setTypeface(null, Typeface.BOLD);
            holder.label.setTextColor(holder.itemView.getContext().getColor(R.color.brand_primary));
            holder.indicator.setVisibility(View.VISIBLE);
        } else {
            holder.label.setTypeface(null, Typeface.NORMAL);
            holder.label.setTextColor(holder.itemView.getContext().getColor(R.color.text_secondary));
            holder.indicator.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            for (CategoryAdapter.Category c : categories) {
                c.isSelected = false;
            }
            category.isSelected = true;
            notifyDataSetChanged();
            listener.onFilterClick(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static final class FilterViewHolder extends RecyclerView.ViewHolder {
        final TextView label;
        final View indicator;

        FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.filterLabel);
            indicator = itemView.findViewById(R.id.filterIndicator);
        }
    }
}
