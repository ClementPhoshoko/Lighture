package com.example.lighture;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ImageView profileImage;
    private HeaderViewModel headerViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null && profileImage != null) {
                profileImage.setImageURI(uri);
                profileImage.setPadding(0, 0, 0, 0); // Remove padding when real image is set
                profileImage.setImageTintList(null); // Remove placeholder tint
                profileImage.setBackground(null); // Remove placeholder background
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        headerViewModel = new ViewModelProvider(requireActivity()).get(HeaderViewModel.class);

        applyInsets(view);
        setupHeader();
        wireProfileImage(view);
        wireMotivation(view);
        wireSettings(view);
        wirePreferences(view);
        wireAccount(view);
        wireLogout(view);
        observeProfile(view);
    }

    private void setupHeader() {
        headerViewModel.updateState(new HeaderViewModel.HeaderState(
                getString(R.string.profile_title),
                getString(R.string.profile_subtitle),
                true,
                R.drawable.ic_settings,
                null,
                false
        ));

        headerViewModel.actionClicked.observe(getViewLifecycleOwner(), clicked -> {
            if (clicked != null && clicked) {
                showMessage("Settings are coming soon.");
                headerViewModel.consumeActionClick();
            }
        });
    }

    private void applyInsets(View root) {
        View scroll = root.findViewById(R.id.profileScroll);
        ViewCompat.setOnApplyWindowInsetsListener(scroll, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, 0, bars.right, bars.bottom);
            return insets;
        });
    }

    private void wireProfileImage(View root) {
        profileImage = root.findViewById(R.id.profileImage);
        View container = root.findViewById(R.id.profileImageContainer);
        View editBtn = root.findViewById(R.id.editAvatarButton);

        View.OnClickListener clickListener = v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        };

        container.setOnClickListener(clickListener);
        editBtn.setOnClickListener(clickListener);
    }

    private void wireMotivation(View root) {
        View card = root.findViewById(R.id.motivationCard);
        ((ImageView) card.findViewById(R.id.advisoryIcon)).setImageResource(R.drawable.ic_outline_trend_up);
        ((TextView) card.findViewById(R.id.advisoryTitle)).setText(R.string.profile_motivation_title);
        ((TextView) card.findViewById(R.id.advisoryBody)).setText(R.string.profile_motivation_body);
    }

    private void wireSettings(View root) {
        root.findViewById(R.id.profileSummaryCard).setOnClickListener(v ->
                showMessage("Profile details are coming soon."));
    }

    private void wirePreferences(View root) {
        setupSettingItem(root.findViewById(R.id.itemDietary), R.drawable.ic_leaf, R.string.profile_pref_dietary, "Vegetarian");
        setupSettingItem(root.findViewById(R.id.itemAllergies), R.drawable.ic_shield, R.string.profile_pref_allergies, "None");
        setupSettingItem(root.findViewById(R.id.itemSkill), R.drawable.ic_chef_hat, R.string.profile_pref_skill, "Intermediate");
        setupSettingItem(root.findViewById(R.id.itemAppliances), R.drawable.ic_grid, R.string.profile_pref_appliances, "Air Fryer, Oven...");
        setupSettingItem(root.findViewById(R.id.itemUnits), R.drawable.ic_recycle, R.string.profile_pref_units, "Metric (kg, °C)");
    }

    private void wireAccount(View root) {
        setupSettingItem(root.findViewById(R.id.itemAccountSettings), R.drawable.ic_lock, R.string.profile_account_settings, null);
        setupSettingItem(root.findViewById(R.id.itemNotifications), R.drawable.ic_bell, R.string.profile_notification_settings, null);
        setupSettingItem(root.findViewById(R.id.itemHelp), R.drawable.ic_search, R.string.profile_help_faq, null);
        setupSettingItem(root.findViewById(R.id.itemContact), R.drawable.ic_email, R.string.profile_contact_us, null);
        setupSettingItem(root.findViewById(R.id.itemAbout), R.drawable.ic_premium, R.string.profile_about, null);
    }

    private void setupSettingItem(View item, int iconRes, int titleRes, String value) {
        ((ImageView) item.findViewById(R.id.settingIcon)).setImageResource(iconRes);
        ((TextView) item.findViewById(R.id.settingTitle)).setText(titleRes);
        TextView valueText = item.findViewById(R.id.settingValue);
        if (value != null) {
            valueText.setText(value);
            valueText.setVisibility(View.VISIBLE);
        } else {
            valueText.setVisibility(View.GONE);
        }
        item.setOnClickListener(v -> showMessage(getString(titleRes) + " details soon."));
    }

    private void wireLogout(View root) {
        root.findViewById(R.id.logoutButton).setOnClickListener(v ->
                showMessage("Log out confirmed."));
    }

    private void observeProfile(View root) {
        viewModel.getProfile().observe(getViewLifecycleOwner(), data -> {
            ((TextView) root.findViewById(R.id.profileName)).setText(data.name);
            ((TextView) root.findViewById(R.id.profileEmail)).setText(data.email);
            root.findViewById(R.id.premiumBadge).setVisibility(data.isPremium ? View.VISIBLE : View.GONE);
            ((TextView) root.findViewById(R.id.statRecipesValue)).setText(data.recipesTried);
            ((TextView) root.findViewById(R.id.statItemsValue)).setText(data.itemsSaved);
            ((TextView) root.findViewById(R.id.statMoneyValue)).setText(data.moneySaved);
            ((TextView) root.findViewById(R.id.statWasteValue)).setText(data.wasteAvoided);
        });
    }

    private void showMessage(String message) {
        View view = getView();
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
