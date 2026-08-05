package com.example.lighture;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Reusable auth frame. One activity hosts the login, register,
 * forgot-password, OTP and new-password content over a stable fruit-icon
 * background; mode switching reuses this single activity instance via
 * {@code onNewIntent}.
 */
public class AuthActivity extends AppCompatActivity {

    public static final String EXTRA_MODE = "extra_auth_mode";
    public static final String EXTRA_EMAIL = "extra_auth_email";
    public static final int MODE_LOGIN = 0;
    public static final int MODE_REGISTER = 1;
    public static final int MODE_FORGOT = 2;
    public static final int MODE_OTP = 3;
    public static final int MODE_NEW_PASSWORD = 4;
    public static final int MODE_SUCCESS = 5;

    private static final String DEMO_OTP = "123456";
    private static final String ASSET_SUCCESS_AVATAR = "success_with_fresh_salad.png";
    private static final String ASSET_FAILURE_AVATAR = "failure_with_fresh_salad.png";
    private static final long OTP_COUNTDOWN_MILLIS = 5 * 60 * 1000L;
    private static final long OTP_COUNTDOWN_TICK = 1000L;

    private View root;
    private CountDownTimer resetTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);

        root = findViewById(R.id.authRoot);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.authContent), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        buildContent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        buildContent();
    }

    private void buildContent() {
        cancelResetTimer();
        FrameLayout container = findViewById(R.id.authContent);
        container.removeAllViews();
        int mode = getIntent().getIntExtra(EXTRA_MODE, MODE_LOGIN);
        int layout = mode == MODE_REGISTER ? R.layout.auth_register
                : mode == MODE_FORGOT ? R.layout.auth_forgot
                : mode == MODE_OTP ? R.layout.auth_otp
                : mode == MODE_NEW_PASSWORD ? R.layout.auth_new_password
                : mode == MODE_SUCCESS ? R.layout.auth_success
                : R.layout.auth_login;
        LayoutInflater.from(this).inflate(layout, container, true);
        wire(mode);
    }

    private void wire(int mode) {
        switch (mode) {
            case MODE_REGISTER:
                wireRegister();
                break;
            case MODE_FORGOT:
                wireForgot();
                break;
            case MODE_OTP:
                wireOtp();
                break;
            case MODE_NEW_PASSWORD:
                wireNewPassword();
                break;
            case MODE_SUCCESS:
                wireSuccess();
                break;
            case MODE_LOGIN:
            default:
                wireLogin();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        cancelResetTimer();
        super.onDestroy();
    }

    private void cancelResetTimer() {
        if (resetTimer != null) {
            resetTimer.cancel();
            resetTimer = null;
        }
    }

    private void wireLogin() {
        EditText email = findViewById(R.id.emailInput);
        EditText password = findViewById(R.id.passwordInput);
        wireFieldFocus(email, findViewById(R.id.emailField));
        wireFieldFocus(password, findViewById(R.id.passwordField));
        wirePasswordToggle(password, findViewById(R.id.togglePassword));

        findViewById(R.id.forgotPassword).setOnClickListener(v -> switchMode(MODE_FORGOT));
        findViewById(R.id.signUpLink).setOnClickListener(v -> switchMode(MODE_REGISTER));

        wireSocial(findViewById(R.id.socialGoogle), R.string.auth_social_google);
        wireSocial(findViewById(R.id.socialApple), R.string.auth_social_apple);
        wireSocial(findViewById(R.id.socialFacebook), R.string.auth_social_facebook);

        Button login = findViewById(R.id.loginButton);
        password.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login.performClick();
                return true;
            }
            return false;
        });
        login.setOnClickListener(v -> {
            if (!validateEmailAndPassword(email, password)) {
                return;
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void wireRegister() {
        EditText name = findViewById(R.id.nameInput);
        EditText email = findViewById(R.id.registerEmailInput);
        EditText password = findViewById(R.id.registerPasswordInput);
        EditText confirm = findViewById(R.id.registerConfirmInput);
        wireFieldFocus(name, findViewById(R.id.nameField));
        wireFieldFocus(email, findViewById(R.id.registerEmailField));
        wireFieldFocus(password, findViewById(R.id.registerPasswordField));
        wireFieldFocus(confirm, findViewById(R.id.registerConfirmField));
        wirePasswordToggle(password, findViewById(R.id.toggleRegisterPassword));
        wirePasswordToggle(confirm, findViewById(R.id.toggleRegisterConfirm));
        wirePasswordStrength(password,
                findViewById(R.id.registerPasswordStrengthRow),
                findViewById(R.id.registerStrengthSegment1),
                findViewById(R.id.registerStrengthSegment2),
                findViewById(R.id.registerStrengthSegment3),
                findViewById(R.id.registerPasswordStrengthLabel));

        findViewById(R.id.loginLink).setOnClickListener(v -> switchMode(MODE_LOGIN));
        TextView agreement = findViewById(R.id.registerAgreement);
        agreement.setText(buildRegisterAgreement());
        agreement.setMovementMethod(LinkMovementMethod.getInstance());
        agreement.setHighlightColor(Color.TRANSPARENT);
        wireSocial(findViewById(R.id.registerSocialGoogle), R.string.auth_social_google);
        wireSocial(findViewById(R.id.registerSocialApple), R.string.auth_social_apple);
        wireSocial(findViewById(R.id.registerSocialFacebook), R.string.auth_social_facebook);

        Button signUp = findViewById(R.id.registerButton);
        confirm.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                signUp.performClick();
                return true;
            }
            return false;
        });
        signUp.setOnClickListener(v -> {
            if (TextUtils.isEmpty(name.getText())) {
                showMessage(R.string.auth_snackbar_fill);
                return;
            }
            if (!validateEmailAndPassword(email, password)) {
                return;
            }
            if (!isStrongPassword(password.getText().toString())) {
                showMessage(R.string.auth_snackbar_password_weak);
                return;
            }
            if (!TextUtils.equals(password.getText(), confirm.getText())) {
                showMessage(R.string.auth_snackbar_password_mismatch);
                return;
            }
            showMessage(R.string.auth_snackbar_register_demo);
            switchMode(MODE_LOGIN);
        });
    }

    private void wireForgot() {
        EditText email = findViewById(R.id.forgotEmailInput);
        wireFieldFocus(email, findViewById(R.id.forgotEmailField));

        findViewById(R.id.backToLogin).setOnClickListener(v -> switchMode(MODE_LOGIN));

        Button send = findViewById(R.id.forgotButton);
        email.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                send.performClick();
                return true;
            }
            return false;
        });
        send.setOnClickListener(v -> {
            if (TextUtils.isEmpty(email.getText())) {
                showMessage(R.string.auth_snackbar_fill);
                return;
            }
            String address = email.getText().toString();
            if (!isValidEmail(address)) {
                showMessage(R.string.auth_snackbar_invalid_email);
                return;
            }
            showMessage(getString(R.string.auth_otp_sent, address));
            switchMode(MODE_OTP, address);
        });
    }

    private void wireOtp() {
        String email = getIntent().getStringExtra(EXTRA_EMAIL);
        String target = email == null || email.trim().isEmpty()
                ? getString(R.string.auth_otp_your_email) : email;
        ((TextView) findViewById(R.id.otpSubtitle))
                .setText(getString(R.string.auth_otp_subtitle, target));

        final EditText[] boxes = new EditText[]{
                findViewById(R.id.otpDigit1),
                findViewById(R.id.otpDigit2),
                findViewById(R.id.otpDigit3),
                findViewById(R.id.otpDigit4),
                findViewById(R.id.otpDigit5),
                findViewById(R.id.otpDigit6)
        };
        final TextView error = findViewById(R.id.otpError);
        final ImageView errorAvatar = findViewById(R.id.otpErrorAvatar);
        final Button verify = findViewById(R.id.otpVerifyButton);
        final TextView resendLink = findViewById(R.id.resendLink);

        InputFilter digitsOnly = (source, start, end, dest, dstart, dend) -> {
            StringBuilder filtered = new StringBuilder(end - start);
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (Character.isDigit(c)) {
                    filtered.append(c);
                }
            }
            return filtered.toString();
        };

        final Runnable refreshState = () -> {
            boolean filled = true;
            for (EditText box : boxes) {
                if (TextUtils.isEmpty(box.getText())) {
                    filled = false;
                    break;
                }
            }
            verify.setEnabled(filled);
            verify.setAlpha(filled ? 1f : 0.45f);
        };

        for (int i = 0; i < boxes.length; i++) {
            final int index = i;
            EditText box = boxes[index];
            box.setContentDescription(getString(R.string.auth_otp_digit_description, index + 1));
            box.setFilters(new InputFilter[]{digitsOnly});
            box.setOnFocusChangeListener((v, hasFocus) -> v.setBackgroundResource(
                    hasFocus ? R.drawable.bg_otp_box_focused : R.drawable.bg_otp_box));
            box.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    error.setVisibility(View.GONE);
                    errorAvatar.setVisibility(View.GONE);
                    if (s.length() == 1 && index < boxes.length - 1) {
                        boxes[index + 1].requestFocus();
                    } else if (s.length() == 0 && index > 0) {
                        boxes[index - 1].requestFocus();
                    }
                    refreshState.run();
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {
                }
            });
        }

        boxes[boxes.length - 1].setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                verify.performClick();
                return true;
            }
            return false;
        });

        startResetTimer();

        verify.setOnClickListener(v -> {
            StringBuilder code = new StringBuilder();
            for (EditText box : boxes) {
                code.append(box.getText());
            }
            if (!DEMO_OTP.equals(code.toString())) {
                for (EditText box : boxes) {
                    box.setBackgroundResource(R.drawable.bg_otp_box_error);
                    box.setText("");
                }
                loadAssetImage(errorAvatar, ASSET_FAILURE_AVATAR);
                errorAvatar.setVisibility(View.VISIBLE);
                error.setVisibility(View.VISIBLE);
                boxes[0].requestFocus();
                refreshState.run();
                return;
            }
            switchMode(MODE_NEW_PASSWORD, target);
        });

        resendLink.setOnClickListener(v -> {
            showMessage(R.string.auth_otp_resent);
            for (EditText box : boxes) {
                box.setBackgroundResource(R.drawable.bg_otp_box);
                box.setText("");
            }
            error.setVisibility(View.GONE);
            errorAvatar.setVisibility(View.GONE);
            refreshState.run();
            boxes[0].requestFocus();
            startResetTimer();
        });
    }

    private void startResetTimer() {
        cancelResetTimer();
        final TextView countdown = findViewById(R.id.otpCountdown);
        final TextView resendLink = findViewById(R.id.resendLink);
        resendLink.setEnabled(false);
        resendLink.setAlpha(0.45f);
        resetTimer = new CountDownTimer(OTP_COUNTDOWN_MILLIS, OTP_COUNTDOWN_TICK) {
            @Override
            public void onTick(long millisUntilFinished) {
                long totalSeconds = millisUntilFinished / 1000;
                String stamp = String.format(Locale.US, "%02d:%02d",
                        totalSeconds / 60, totalSeconds % 60);
                countdown.setText(getString(R.string.auth_otp_countdown, stamp));
            }

            @Override
            public void onFinish() {
                countdown.setText(R.string.auth_otp_expired);
                resendLink.setEnabled(true);
                resendLink.setAlpha(1f);
            }
        }.start();
    }

    private void wireNewPassword() {
        EditText password = findViewById(R.id.newPasswordInput);
        EditText confirm = findViewById(R.id.newConfirmInput);
        wireFieldFocus(password, findViewById(R.id.newPasswordField));
        wireFieldFocus(confirm, findViewById(R.id.newConfirmField));
        wirePasswordToggle(password, findViewById(R.id.toggleNewPassword));
        wirePasswordToggle(confirm, findViewById(R.id.toggleNewConfirm));
        wirePasswordStrength(password,
                findViewById(R.id.newPasswordStrengthRow),
                findViewById(R.id.newPasswordStrengthSegment1),
                findViewById(R.id.newPasswordStrengthSegment2),
                findViewById(R.id.newPasswordStrengthSegment3),
                findViewById(R.id.newPasswordStrengthLabel));

        findViewById(R.id.backToLoginLink).setOnClickListener(v -> switchMode(MODE_LOGIN));

        Button save = findViewById(R.id.savePasswordButton);
        confirm.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                save.performClick();
                return true;
            }
            return false;
        });
        save.setOnClickListener(v -> {
            if (!isStrongPassword(password.getText().toString())) {
                showMessage(R.string.auth_snackbar_password_weak);
                return;
            }
            if (!TextUtils.equals(password.getText(), confirm.getText())) {
                showMessage(R.string.auth_snackbar_password_mismatch);
                return;
            }
            switchMode(MODE_SUCCESS);
        });
    }

    private void wireSuccess() {
        loadAssetImage((ImageView) findViewById(R.id.successAvatar), ASSET_SUCCESS_AVATAR);
        findViewById(R.id.successButton).setOnClickListener(v -> switchMode(MODE_LOGIN));
    }

    private void loadAssetImage(ImageView target, String assetPath) {
        try {
            InputStream stream = getAssets().open(assetPath);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
            target.setImageBitmap(bitmap);
        } catch (IOException e) {
            target.setImageDrawable(null);
        }
    }

    private boolean validateEmailAndPassword(EditText email, EditText password) {
        if (TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(password.getText())) {
            showMessage(R.string.auth_snackbar_empty);
            return false;
        }
        if (!isValidEmail(email.getText().toString())) {
            showMessage(R.string.auth_snackbar_invalid_email);
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String value) {
        return Patterns.EMAIL_ADDRESS.matcher(value).matches();
    }

    private boolean isStrongPassword(String value) {
        if (value.length() < 8) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasSymbol = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                hasSymbol = true;
            }
        }
        return hasLetter && hasDigit && hasSymbol;
    }

    private void wireFieldFocus(EditText input, View card) {
        input.setOnFocusChangeListener((v, hasFocus) -> card.setBackgroundResource(
                hasFocus ? R.drawable.bg_auth_field_focused : R.drawable.bg_auth_field));
    }

    private void wirePasswordStrength(EditText input, View row, View seg1, View seg2,
                                      View seg3, TextView label) {
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    row.setVisibility(View.GONE);
                    return;
                }
                row.setVisibility(View.VISIBLE);
                int strength = passwordStrength(s.toString());
                switch (strength) {
                    case 2:
                        label.setText(R.string.auth_password_strength_strong);
                        label.setTextColor(getColor(R.color.color_success));
                        break;
                    case 1:
                        label.setText(R.string.auth_password_strength_fair);
                        label.setTextColor(getColor(R.color.color_warning));
                        break;
                    default:
                        label.setText(R.string.auth_password_strength_weak);
                        label.setTextColor(getColor(R.color.color_danger));
                        break;
                }
                View[] segments = {seg1, seg2, seg3};
                int fillColor = strength == 2 ? getColor(R.color.color_success)
                        : strength == 1 ? getColor(R.color.color_warning)
                        : getColor(R.color.color_danger);
                int trackColor = getColor(R.color.neutral_200);
                for (int i = 0; i < segments.length; i++) {
                    segments[i].setBackgroundTintList(
                            ColorStateList.valueOf(i < strength + 1 ? fillColor : trackColor));
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });
    }

    private int passwordStrength(String value) {
        if (value.length() < 8) {
            return 0;
        }
        return isStrongPassword(value) ? 2 : 1;
    }

    private void wirePasswordToggle(EditText input, ImageButton toggle) {
        final boolean[] visible = {false};
        toggle.setOnClickListener(v -> {
            visible[0] = !visible[0];
            int selection = Math.max(input.getSelectionStart(), 0);
            input.setInputType(visible[0]
                    ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            input.setSelection(selection);
            toggle.setImageResource(visible[0] ? R.drawable.ic_eye_off : R.drawable.ic_eye);
            toggle.setContentDescription(getString(
                    visible[0] ? R.string.auth_hide_password : R.string.auth_show_password));
        });
    }

    private void wireSocial(View button, int brandNameRes) {
        button.setOnClickListener(v -> showMessage(
                getString(R.string.auth_snackbar_social, getString(brandNameRes))));
    }

    private CharSequence buildRegisterAgreement() {
        String terms = getString(R.string.auth_register_terms);
        String privacy = getString(R.string.auth_register_privacy);
        SpannableString text = new SpannableString(
                getString(R.string.auth_register_agreement, terms, privacy));
        int termsStart = text.toString().indexOf(terms);
        if (termsStart >= 0) {
            text.setSpan(makeAgreementLink(R.string.auth_snackbar_terms_demo),
                    termsStart, termsStart + terms.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        int privacyStart = text.toString().indexOf(privacy);
        if (privacyStart >= 0) {
            text.setSpan(makeAgreementLink(R.string.auth_snackbar_privacy_demo),
                    privacyStart, privacyStart + privacy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return text;
    }

    private ClickableSpan makeAgreementLink(int messageResId) {
        return new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                showMessage(messageResId);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getColor(R.color.text_brand));
                ds.setUnderlineText(false);
            }
        };
    }

    private void switchMode(int mode) {
        switchMode(mode, null);
    }

    private void switchMode(int mode, String email) {
        Intent intent = new Intent(this, AuthActivity.class)
                .putExtra(EXTRA_MODE, mode)
                .putExtra(EXTRA_EMAIL, email)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showMessage(int resId) {
        Snackbar.make(root, resId, Snackbar.LENGTH_SHORT).show();
    }

    private void showMessage(String message) {
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
    }
}
