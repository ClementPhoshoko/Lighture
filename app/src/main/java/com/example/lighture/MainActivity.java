package com.example.lighture;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.transition.TransitionManager;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import androidx.lifecycle.ViewModelProvider;

/**
 * Main Activity: Host for the Single Activity Architecture.
 * Manages the BottomNavigationView and wires it to the NavHostFragment.
 */
public class MainActivity extends AppCompatActivity {

    private HeaderViewModel headerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);

        applyInsets();
        wireNavigation();
        observeHeader();
    }

    private void applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainHeader), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, bars.top, 0, 0);
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomNav), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, 0, bars.right, bars.bottom);
            return insets;
        });
    }

    private void observeHeader() {
        View headerView = findViewById(R.id.mainHeader);
        TextView titleView = headerView.findViewById(R.id.headerTitle);
        TextView subtitleView = headerView.findViewById(R.id.headerSubtitle);
        View actionContainer = headerView.findViewById(R.id.headerActionContainer);
        ImageButton actionButton = headerView.findViewById(R.id.headerActionButton);
        View actionBadge = headerView.findViewById(R.id.headerActionBadge);
        MaterialButton textButton = headerView.findViewById(R.id.headerActionTextButton);

        actionButton.setOnClickListener(v -> headerViewModel.onActionClick());
        textButton.setOnClickListener(v -> headerViewModel.onActionClick());

        headerViewModel.state.observe(this, state -> {
            if (state.title == null) {
                headerView.setVisibility(View.GONE);
                return;
            }
            headerView.setVisibility(View.VISIBLE);
            TransitionManager.beginDelayedTransition((ViewGroup) headerView);
            titleView.setText(state.title);
            subtitleView.setText(state.subtitle);

            actionContainer.setVisibility(state.showAction ? View.VISIBLE : View.GONE);
            if (state.showAction) {
                if (state.actionText != null) {
                    textButton.setVisibility(View.VISIBLE);
                    actionButton.setVisibility(View.GONE);
                    textButton.setText(state.actionText);
                    if (state.actionIconRes != null) {
                        textButton.setIconResource(state.actionIconRes);
                    }
                } else {
                    textButton.setVisibility(View.GONE);
                    actionButton.setVisibility(View.VISIBLE);
                    if (state.actionIconRes != null) {
                        actionButton.setImageResource(state.actionIconRes);
                    }
                    actionBadge.setVisibility(state.showBadge ? View.VISIBLE : View.GONE);
                }
            }
        });
    }

    private void wireNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHostFragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
            bottomNav.setClipToOutline(true);
            NavigationUI.setupWithNavController(bottomNav, navController);

            bottomNav.setOnItemSelectedListener(item -> {
                // 1. Trigger haptic feedback (subtle click)
                bottomNav.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                // 2. Animate the active icon with a Spring/Overshoot effect
                View itemView = bottomNav.findViewById(item.getItemId());
                if (itemView != null) {
                    View foundIcon = itemView.findViewById(com.google.android.material.R.id.navigation_bar_item_icon_view);
                    if (foundIcon == null) {
                        foundIcon = itemView.findViewById(com.google.android.material.R.id.icon);
                    }
                    if (foundIcon != null) {
                        final View icon = foundIcon;
                        icon.animate()
                                .scaleX(1.2f)
                                .scaleY(1.2f)
                                .setDuration(200)
                                .setInterpolator(new OvershootInterpolator(2f))
                                .withEndAction(() -> icon.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                                .start();
                    }
                }

                // 3. Maintain standard NavigationUI behavior
                return NavigationUI.onNavDestinationSelected(item, navController);
            });
        }
    }
}
