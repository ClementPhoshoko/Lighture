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

import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Main Activity: Host for the Single Activity Architecture.
 * Manages the BottomNavigationView and wires it to the NavHostFragment.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        applyInsets();
        wireNavigation();
    }

    private void applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomNav), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, 0, bars.right, bars.bottom);
            return insets;
        });
    }

    private void wireNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHostFragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
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
