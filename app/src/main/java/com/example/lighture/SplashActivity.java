package com.example.lighture;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Premium branded splash. Holds for a short branded beat, then hands off to
 * {@link MainActivity}. Uses core-splashscreen so the system splash and this
 * screen blend seamlessly on every API level (24+).
 */
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_HOLD_MS = 2600L;

    private View root;
    private final Runnable navigateRunnable = this::exitToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        splashScreen.setKeepOnScreenCondition(() -> false);
        setContentView(R.layout.activity_splash);

        root = findViewById(R.id.splashRoot);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        animateLogo(findViewById(R.id.splashLogo));

        root.postDelayed(navigateRunnable, SPLASH_HOLD_MS);
    }

    @Override
    protected void onDestroy() {
        if (root != null) {
            root.removeCallbacks(navigateRunnable);
        }
        super.onDestroy();
    }

    private void animateLogo(View logo) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, View.SCALE_X, 0.92f, 1f);
        scaleX.setDuration(600L);

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, View.SCALE_Y, 0.92f, 1f);
        scaleY.setDuration(600L);

        AnimatorSet entry = new AnimatorSet();
        entry.playTogether(scaleX, scaleY);
        entry.setInterpolator(new DecelerateInterpolator());
        entry.start();

        ObjectAnimator floatAnim = ObjectAnimator.ofFloat(logo, View.TRANSLATION_Y, 0f, -dp(10f));
        floatAnim.setDuration(2000L);
        floatAnim.setStartDelay(800L);
        floatAnim.setRepeatCount(ValueAnimator.INFINITE);
        floatAnim.setRepeatMode(ValueAnimator.REVERSE);
        floatAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnim.start();
    }

    private void exitToMain() {
        ObjectAnimator fade = ObjectAnimator.ofFloat(root, View.ALPHA, 1f, 0f);
        fade.setDuration(280L);
        fade.setInterpolator(new AccelerateDecelerateInterpolator());
        fade.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        fade.start();
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
