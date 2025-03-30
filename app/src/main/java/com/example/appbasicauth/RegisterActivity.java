package com.example.appbasicauth;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appbasicauth.api.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilEmail, tilPhone, tilPassword, tilConfirmPassword;
    private TextInputEditText etName, etEmail, etPhone, etPassword, etConfirmPassword;
    private MaterialCheckBox cbTerms;
    private MaterialButton btnCreateAccount;
    private ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar API Client
        apiClient = ApiClient.getInstance(this);

        // Inicializar vistas
        initViews();

        // Configurar listeners
        setupListeners();

        // Configurar validaciones en tiempo real
        setupTextWatchers();
    }

    private void initViews() {
        tilName = findViewById(R.id.til_name);
        tilEmail = findViewById(R.id.til_email);
        tilPhone = findViewById(R.id.til_phone);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        cbTerms = findViewById(R.id.cb_terms);
        btnCreateAccount = findViewById(R.id.btn_create_account);

        // Inicialmente deshabilitar el botón hasta que se completen todos los campos
        btnCreateAccount.setEnabled(false);
    }

    private void setupListeners() {
        // Casting explícito a MaterialToolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        cbTerms.setOnCheckedChangeListener((buttonView, isChecked) -> validateForm());

        btnCreateAccount.setOnClickListener(v -> {
            if (validateForm()) {
                // Mostrar animación de carga
                btnCreateAccount.setEnabled(false);
                btnCreateAccount.setText("Creando cuenta...");

                // Obtener datos del formulario
                String nombre = Objects.requireNonNull(etName.getText()).toString().trim();
                String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
                String telefono = Objects.requireNonNull(etPhone.getText()).toString().trim();
                String password = Objects.requireNonNull(etPassword.getText()).toString();

                // Llamar al API de registro
                apiClient.register(nombre, email, telefono, email, password, new ApiClient.ApiResponseCallback() {
                    @Override
                    public void onSuccess(String message) {
                        runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                            finish(); // Regresar a la pantalla de login
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            btnCreateAccount.setEnabled(true);
                            btnCreateAccount.setText("Crear cuenta");
                            Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        });
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateForm();
            }
        };

        etName.addTextChangedListener(textWatcher);
        etEmail.addTextChangedListener(textWatcher);
        etPhone.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);
        etConfirmPassword.addTextChangedListener(textWatcher);
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Validar nombre
        String name = Objects.requireNonNull(etName.getText()).toString().trim();
        if (name.isEmpty()) {
            tilName.setError("Ingresa tu nombre completo");
            isValid = false;
        } else if (name.length() < 3) {
            tilName.setError("El nombre debe tener al menos 3 caracteres");
            isValid = false;
        } else {
            tilName.setError(null);
        }

        // Validar email
        String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
        if (email.isEmpty()) {
            tilEmail.setError("Ingresa tu correo electrónico");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Ingresa un correo electrónico válido");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        // Validar teléfono
        String phone = Objects.requireNonNull(etPhone.getText()).toString().trim();
        if (phone.isEmpty()) {
            tilPhone.setError("Ingresa tu número telefónico");
            isValid = false;
        } else if (phone.length() != 10 || !Pattern.matches("[0-9]+", phone)) {
            tilPhone.setError("Ingresa un número telefónico válido de 10 dígitos");
            isValid = false;
        } else {
            tilPhone.setError(null);
        }

        // Validar contraseña
        String password = Objects.requireNonNull(etPassword.getText()).toString();
        if (password.isEmpty()) {
            tilPassword.setError("Ingresa una contraseña");
            isValid = false;
        } else if (password.length() < 8) {
            tilPassword.setError("La contraseña debe tener al menos 8 caracteres");
            isValid = false;
        } else if (!Pattern.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}").matcher(password).matches()) {
            tilPassword.setError("La contraseña debe contener al menos una letra mayúscula, una minúscula y un número");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        // Validar confirmación de contraseña
        String confirmPassword = Objects.requireNonNull(etConfirmPassword.getText()).toString();
        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Confirma tu contraseña");
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            tilConfirmPassword.setError("Las contraseñas no coinciden");
            isValid = false;
        } else {
            tilConfirmPassword.setError(null);
        }

        // Validar términos y condiciones
        if (!cbTerms.isChecked()) {
            isValid = false;
        }

        // Habilitar o deshabilitar botón según validación
        btnCreateAccount.setEnabled(isValid);

        return isValid;
    }
}