package com.example.lighture;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Binds the ingredient list on the Fridge screen. The status pill uses the
 * shared freshness colour tokens tinted onto bg_tag_pill, matching how the
 * recipe count badge is styled in RecipesAdapter.
 */
public final class FridgeItemsAdapter extends RecyclerView.Adapter<FridgeItemsAdapter.ItemViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(FridgeItem item);
    }

    private final List<FridgeItem> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public FridgeItemsAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_fridge_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        FridgeItem item = items.get(position);
        Context context = holder.itemView.getContext();
        holder.name.setText(item.name);
        holder.quantity.setText(item.quantity);

        int iconRes = item.imageRes != 0 ? item.imageRes : ProductIconMapper.getIconFor(item.name);
        holder.image.setImageResource(iconRes);

        int statusColor = ContextCompat.getColor(context, FridgeItem.statusColorRes(item.status));
        holder.status.setText(context.getString(FridgeItem.statusLabelRes(item.status)));
        holder.status.setTextColor(statusColor);
        holder.status.getBackground().mutate().setTintList(ColorStateList.valueOf(
                ContextCompat.getColor(context, FridgeItem.statusBackgroundRes(item.status))));

        holder.expiry.setText(item.expiryText);
        TextViewCompat.setCompoundDrawableTintList(holder.expiry, ColorStateList.valueOf(statusColor));

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<FridgeItem> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    static final class ItemViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView quantity;
        final ImageView image;
        final TextView status;
        final TextView expiry;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.fridgeItemName);
            quantity = itemView.findViewById(R.id.fridgeItemQuantity);
            image = itemView.findViewById(R.id.fridgeItemImage);
            status = itemView.findViewById(R.id.fridgeItemStatus);
            expiry = itemView.findViewById(R.id.fridgeItemExpiry);
        }
    }
}
