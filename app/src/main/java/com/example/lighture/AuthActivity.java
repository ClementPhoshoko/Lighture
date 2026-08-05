package com.example.lighture;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

/**
 * Reusable auth frame. One activity hosts the login, register and
 * forgot-password content over a stable fruit-icon background; mode switching
 * reuses this single activity instance via {@code onNewIntent}.
 */
public class AuthActivity extends AppCompatActivity {

    public static final String EXTRA_MODE = "extra_auth_mode";
    public static final int MODE_LOGIN = 0;
    public static final int MODE_REGISTER = 1;
    public static final int MODE_FORGOT = 2;

    private View root;

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
        FrameLayout container = findViewById(R.id.authContent);
        container.removeAllViews();
        int mode = getIntent().getIntExtra(EXTRA_MODE, MODE_LOGIN);
        int layout = mode == MODE_REGISTER ? R.layout.auth_register
                : mode == MODE_FORGOT ? R.layout.auth_forgot : R.layout.auth_login;
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
            case MODE_LOGIN:
            default:
                wireLogin();
                break;
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

        findViewById(R.id.loginLink).setOnClickListener(v -> switchMode(MODE_LOGIN));
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
            if (!isValidEmail(email.getText().toString())) {
                showMessage(R.string.auth_snackbar_invalid_email);
                return;
            }
            showMessage(R.string.auth_snackbar_forgot_demo);
            switchMode(MODE_LOGIN);
        });
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

    private void wireFieldFocus(EditText input, View card) {
        input.setOnFocusChangeListener((v, hasFocus) -> card.setBackgroundResource(
                hasFocus ? R.drawable.bg_auth_field_focused : R.drawable.bg_auth_field));
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

    private void switchMode(int mode) {
        Intent intent = new Intent(this, AuthActivity.class)
                .putExtra(EXTRA_MODE, mode)
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
