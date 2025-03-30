package com.example.appbasicauth;


import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.content.Intent;
import android.view.View;
import com.example.appbasicauth.api.ApiClient;
import com.example.appbasicauth.api.SessionManager;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnRegister;
    private ApiClient apiClient;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configurar Edge-to-Edge display para Material Design 3
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_main);

        // Inicializar API Client y Session Manager
        apiClient = ApiClient.getInstance(this);
        sessionManager = new SessionManager(this);

        // Verificar si ya hay una sesión activa
        if (sessionManager.isLoggedIn()) {
            navigateToHome();
            return;
        }

        // Inicializar vistas
        initViews();

        // Configurar listeners
        setupListeners();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            if (validateInputs()) {
                // Implementar autenticación con animación de carga
                btnLogin.setEnabled(false);
                btnLogin.setText("Iniciando sesión...");

                // Obtener el email como username para el login
                String username = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Llamar al API de login
                apiClient.login(username, password, new ApiClient.ApiResponseCallback() {
                    @Override
                    public void onSuccess(String message) {
                        // Guardar la sesión
                        sessionManager.saveUserSession("1", "Usuario", username, "", username, password);

                        // Navegar a la pantalla principal
                        navigateToHome();

                        // Mostrar mensaje de éxito
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            btnLogin.setEnabled(true);
                            btnLogin.setText("Iniciar sesión");
                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.tv_forgot_password).setOnClickListener(v -> {
            // Aquí navegarías a la pantalla de recuperación de contraseña
            Toast.makeText(MainActivity.this, "Recuperar contraseña", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validar email
        if (email.isEmpty()) {
            tilEmail.setError("Ingresa tu correo electrónico");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Ingresa un correo electrónico válido");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        // Validar contraseña
        if (password.isEmpty()) {
            tilPassword.setError("Ingresa tu contraseña");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("La contraseña debe tener al menos 6 caracteres");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        return isValid;
    }

    private void navigateToHome() {
        Intent intent = new Intent(MainActivity.this, InicioActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}