package com.example.lighture;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.List;

public final class FridgeFilterAdapter extends RecyclerView.Adapter<FridgeFilterAdapter.FilterViewHolder> {

    public interface OnFilterClickListener {
        void onFilterClick(CategoryAdapter.Category category);
    }

    private final List<CategoryAdapter.Category> categories;
    private final OnFilterClickListener listener;

    public FridgeFilterAdapter(List<CategoryAdapter.Category> categories, OnFilterClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fridge_filter_chip, parent, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        CategoryAdapter.Category category = categories.get(position);
        holder.chip.setText(category.name);
        holder.chip.setChipIconResource(category.iconRes);
        holder.chip.setChecked(category.isSelected);

        holder.chip.setOnClickListener(v -> {
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
        final Chip chip;

        FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = (Chip) itemView;
        }
    }
}
