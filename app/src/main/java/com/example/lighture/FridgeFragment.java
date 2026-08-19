package com.example.lighture;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Fridge screen: dashboard of tracked ingredients. Follows the Home/Recipes
 * fragment pattern — static data from {@link FridgeData}, freshness filters
 * via the shared CategoryAdapter, and a nested RecyclerView for the list.
 */
public class FridgeFragment extends Fragment {

    private final List<FridgeItem> fullItemsList = new ArrayList<>();
    private FridgeItemsAdapter itemsAdapter;
    private String currentFilter = FridgeData.FILTER_ALL;
    private HeaderViewModel headerViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fridge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        headerViewModel = new ViewModelProvider(requireActivity()).get(HeaderViewModel.class);

        applyInsets(view);
        setupHeader();
        bindOverview(view);
        wireFilters(view);
        setupItemsList(view);
        wireWasteCard(view);
        wireHistory(view);
        animateContentIn(view);
    }

    private void setupHeader() {
        headerViewModel.updateState(new HeaderViewModel.HeaderState(
                getString(R.string.fridge_title),
                getString(R.string.fridge_subtitle),
                true,
                R.drawable.ic_camera,
                getString(R.string.fridge_scan),
                false
        ));

        headerViewModel.actionClicked.observe(getViewLifecycleOwner(), clicked -> {
            if (clicked != null && clicked) {
                showMessage(R.string.fridge_snackbar_scan);
                headerViewModel.consumeActionClick();
            }
        });
    }

    private void animateContentIn(View root) {
        root.findViewById(R.id.fridgeScroll).startAnimation(
                AnimationUtils.loadAnimation(requireContext(), R.anim.activity_content_in));
    }

    private void applyInsets(View root) {
        View scroll = root.findViewById(R.id.fridgeScroll);
        ViewCompat.setOnApplyWindowInsetsListener(scroll, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, 0, bars.right, bars.bottom);
            return insets;
        });
    }



    private void bindOverview(View root) {
        FridgeData.FridgeStats stats = FridgeData.stats();
        bindStat(root, R.id.fridgeStatItemsValue, stats.items);
        bindStat(root, R.id.fridgeStatFreshValue, stats.fresh);
        bindStat(root, R.id.fridgeStatExpiringValue, stats.expiring);
        bindStat(root, R.id.fridgeStatSavedValue, stats.saved);

        ImageUtils.loadAssetImage(requireContext(), root.findViewById(R.id.fridgeOverviewIllustration), "robot_chef.png");
    }

    private void bindStat(View root, int valueId, String value) {
        ((TextView) root.findViewById(valueId)).setText(value);
    }

    private void wireFilters(View root) {
        List<CategoryAdapter.Category> categories = new ArrayList<>();
        categories.add(new CategoryAdapter.Category(FridgeData.FILTER_ALL, R.drawable.ic_grid, true));
        categories.add(new CategoryAdapter.Category(FridgeData.FILTER_FRESH, R.drawable.ic_outline_leaf, false));
        categories.add(new CategoryAdapter.Category(FridgeData.FILTER_EXPIRING, R.drawable.ic_outline_clock, false));
        categories.add(new CategoryAdapter.Category(FridgeData.FILTER_EXPIRED, R.drawable.ic_outline_trash, false));

        FilterChipAdapter filterAdapter = new FilterChipAdapter(categories, category -> {
            currentFilter = category.name;
            applyFilter();
        });
        RecyclerView filterList = root.findViewById(R.id.filterChipRecyclerView);
        filterList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        filterList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                int count = state.getItemCount();
                if (position < count - 1) {
                    outRect.right = getResources().getDimensionPixelSize(R.dimen.space_2);
                }
            }
        });
        filterList.setAdapter(filterAdapter);
    }

    private void setupItemsList(View root) {
        fullItemsList.clear();
        fullItemsList.addAll(FridgeData.all());
        itemsAdapter = new FridgeItemsAdapter(item -> showMessage(R.string.fridge_snackbar_item));
        RecyclerView list = root.findViewById(R.id.fridgeItemsList);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list.setAdapter(itemsAdapter);
        applyFilter();
    }

    private void applyFilter() {
        List<FridgeItem> filtered = new ArrayList<>();
        for (FridgeItem item : fullItemsList) {
            if (FridgeData.matchesFilter(item, currentFilter)) {
                filtered.add(item);
            }
        }
        itemsAdapter.setItems(filtered);
    }

    private void wireWasteCard(View root) {
        TextView wasteBody = root.findViewById(R.id.fridgeWasteBody);
        int expiringCount = Integer.parseInt(FridgeData.stats().expiring);
        wasteBody.setText(getString(R.string.fridge_waste_body, expiringCount));

        root.findViewById(R.id.fridgeWasteAction).setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.navRecipes);
        });
    }

    private void wireHistory(View root) {
        View header = root.findViewById(R.id.fridgeHistoryHeader);
        TextView title = header.findViewById(R.id.sectionTitle);
        title.setText(R.string.fridge_history_title);
        TextView action = header.findViewById(R.id.sectionAction);
        action.setText(R.string.fridge_history_action);
        action.setOnClickListener(v -> showMessage(R.string.fridge_snackbar_history));

        TextView detected = root.findViewById(R.id.fridgeHistoryItemDetected);
        detected.setText(getString(R.string.fridge_history_item_detected, FridgeData.all().size()));
    }

    private void showMessage(int resId) {
        View view = getView();
        if (view != null) {
            Snackbar.make(view, resId, Snackbar.LENGTH_SHORT).show();
        }
    }
}
