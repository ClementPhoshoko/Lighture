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
import android.view.animation.LinearInterpolator;

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
        animateGlow(findViewById(R.id.splashGlow));
        animateContent(findViewById(R.id.splashContent));
        animateSparkles();
        animateDots();
        animateFood();

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
        ObjectAnimator fade = ObjectAnimator.ofFloat(logo, View.ALPHA, 0f, 1f);
        fade.setDuration(600L);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, View.SCALE_X, 0.90f, 1f);
        scaleX.setDuration(700L);

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, View.SCALE_Y, 0.90f, 1f);
        scaleY.setDuration(700L);

        AnimatorSet entry = new AnimatorSet();
        entry.playTogether(fade, scaleX, scaleY);
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

    private void animateGlow(View glow) {
        ObjectAnimator pulse = ObjectAnimator.ofFloat(glow, View.ALPHA, 0.55f, 1f);
        pulse.setDuration(1600L);
        pulse.setRepeatCount(ValueAnimator.INFINITE);
        pulse.setRepeatMode(ValueAnimator.REVERSE);
        pulse.setInterpolator(new AccelerateDecelerateInterpolator());
        pulse.start();
    }

    private void animateContent(View content) {
        ObjectAnimator fade = ObjectAnimator.ofFloat(content, View.ALPHA, 0f, 1f);
        fade.setDuration(700L);
        fade.setStartDelay(250L);
        fade.setInterpolator(new AccelerateDecelerateInterpolator());
        fade.start();
    }

    private void animateSparkles() {
        int[] ids = {R.id.sparkleTopLeft, R.id.sparkleTopRight, R.id.sparkleBottomRight};
        long[] delays = {100L, 280L, 460L};
        for (int i = 0; i < ids.length; i++) {
            View sparkle = findViewById(ids[i]);
            ObjectAnimator fade = ObjectAnimator.ofFloat(sparkle, View.ALPHA, 0f, 1f);
            fade.setDuration(1400L);
            fade.setStartDelay(delays[i]);
            fade.setRepeatCount(ValueAnimator.INFINITE);
            fade.setRepeatMode(ValueAnimator.REVERSE);
            fade.setInterpolator(new LinearInterpolator());

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(sparkle, View.SCALE_X, 0.6f, 1f);
            scaleX.setDuration(1400L);
            scaleX.setStartDelay(delays[i]);
            scaleX.setRepeatCount(ValueAnimator.INFINITE);
            scaleX.setRepeatMode(ValueAnimator.REVERSE);

            AnimatorSet set = new AnimatorSet();
            set.playTogether(fade, scaleX);
            set.start();
        }
    }

    private void animateDots() {
        int[] dotIds = {R.id.splashDot1, R.id.splashDot2, R.id.splashDot3};
        for (int i = 0; i < dotIds.length; i++) {
            View dot = findViewById(dotIds[i]);
            ObjectAnimator pulse = ObjectAnimator.ofFloat(dot, View.ALPHA, 0.25f, 1f);
            pulse.setDuration(500L);
            pulse.setStartDelay(i * 150L);
            pulse.setRepeatCount(ValueAnimator.INFINITE);
            pulse.setRepeatMode(ValueAnimator.REVERSE);
            pulse.setInterpolator(new LinearInterpolator());
            pulse.start();
        }
    }

    private void animateFood() {
        int[] ids = {R.id.foodTopLeft, R.id.foodTopRight, R.id.foodBottomLeft, R.id.foodBottomRight};
        long[] delays = {900L, 1200L, 1000L, 1400L};
        for (int i = 0; i < ids.length; i++) {
            View food = findViewById(ids[i]);
            ObjectAnimator drift = ObjectAnimator.ofFloat(food, View.TRANSLATION_Y, 0f, dp(6f));
            drift.setDuration(2600L);
            drift.setStartDelay(delays[i]);
            drift.setRepeatCount(ValueAnimator.INFINITE);
            drift.setRepeatMode(ValueAnimator.REVERSE);
            drift.setInterpolator(new AccelerateDecelerateInterpolator());
            drift.start();
        }
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
