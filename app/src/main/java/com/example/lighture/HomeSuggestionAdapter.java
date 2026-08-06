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

public final class HomeSuggestionAdapter extends RecyclerView.Adapter<HomeSuggestionAdapter.SuggestionViewHolder> {

    public interface OnSuggestionClickListener {
        void onSuggestionClick(HomeData.Suggestion suggestion);
    }

    private static final String ASSET_MOCK_DISH_IMAGE = "salad_image.png";

    private final List<HomeData.Suggestion> suggestions;
    private final OnSuggestionClickListener listener;

    public HomeSuggestionAdapter(List<HomeData.Suggestion> suggestions, OnSuggestionClickListener listener) {
        this.suggestions = suggestions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_home_suggestion_card, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        HomeData.Suggestion suggestion = suggestions.get(position);
        Context context = holder.itemView.getContext();
        holder.title.setText(suggestion.title);
        holder.time.setText(suggestion.time);
        ImageUtils.loadAssetImage(context, holder.icon, ASSET_MOCK_DISH_IMAGE);
        holder.uses.setText(context.getResources().getQuantityString(
                R.plurals.home_suggestion_ingredients,
                suggestion.ingredientCount, suggestion.ingredientCount));
        holder.uses.getBackground().mutate()
                .setTintList(ColorStateList.valueOf(context.getColor(R.color.tag_easy_background)));
        holder.uses.setTextColor(context.getColor(R.color.tag_easy_text));
        holder.itemView.setOnClickListener(v -> listener.onSuggestionClick(suggestion));
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    static final class SuggestionViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView time;
        final TextView uses;
        final ImageView icon;

        SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.suggestionTitle);
            time = itemView.findViewById(R.id.suggestionTime);
            uses = itemView.findViewById(R.id.suggestionUses);
            icon = itemView.findViewById(R.id.suggestionIcon);
        }
    }
}
