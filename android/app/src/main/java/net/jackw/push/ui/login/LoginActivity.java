package net.jackw.push.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.jackw.push.databinding.ActivityLoginBinding;
import net.jackw.push.notifications.NotificationRegistrationManager;
import net.jackw.push.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView loginErrorText;
    private Button loginButton;
    private ProgressBar loadingSpinner;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        emailEditText = binding.email;
        passwordEditText = binding.password;
        loginErrorText = binding.loginError;
        loginButton = binding.login;
        loadingSpinner = binding.loading;

        setContentView(binding.getRoot());

        TextWatcher fieldUpdateWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence _s, int _start, int _count, int _after) {
                // Ignore
            }

            @Override
            public void onTextChanged(CharSequence _s, int _start, int _before, int _count) {
                // Ignore
            }

            @Override
            public void afterTextChanged(Editable _editable) {
                onFieldUpdate();
            }
        };
        emailEditText.addTextChangedListener(fieldUpdateWatcher);
        passwordEditText.addTextChangedListener(fieldUpdateWatcher);
        passwordEditText.setOnEditorActionListener((_v, actionId, _event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                logIn();
            }
            return false;
        });

        loginButton.setOnClickListener(_v -> logIn());
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            onLoggedIn();
        }
    }

    private void onFieldUpdate() {
        boolean canSubmit = emailEditText.getText().length() > 0 && passwordEditText.getText().length() > 0;
        loginButton.setEnabled(canSubmit);
        loginErrorText.setVisibility(View.GONE);
    }

    private void logIn() {
        loadingSpinner.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
        emailEditText.setEnabled(false);
        passwordEditText.setEnabled(false);

        auth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(task -> {
                    loadingSpinner.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        onLoggedIn();
                    } else {
                        loginErrorText.setVisibility(View.VISIBLE);
                        emailEditText.setEnabled(true);
                        passwordEditText.setEnabled(true);
                    }
                });
    }

    private void onLoggedIn() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        NotificationRegistrationManager.getInstance().updateStoredToken(this.getApplicationContext());

        setResult(Activity.RESULT_OK);
        finish();
    }
}