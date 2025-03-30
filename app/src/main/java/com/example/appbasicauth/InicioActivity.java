package com.example.appbasicauth;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbasicauth.api.ApiClient;
import com.example.appbasicauth.api.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;

public class InicioActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private ApiClient apiClient;
    private TextView tvGreeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);

        // Inicializar API Client y Session Manager
        apiClient = ApiClient.getInstance(this);
        sessionManager = new SessionManager(this);

        // Verificar si el usuario ha iniciado sesión
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        // Configurar la barra de herramientas
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configurar el RecyclerView (puedes implementar esto más tarde)
        setupRecyclerView();

        // Configurar listeners de botones
        setupListeners();

        // Mostrar información del usuario
        setupUserInfo();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupUserInfo() {
        tvGreeting = findViewById(R.id.tv_greeting);
        TextView tvUserStatus = findViewById(R.id.tv_user_status);

        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();

        tvGreeting.setText("¡Hola, " + userName + "!");
        tvUserStatus.setText(userEmail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inicio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Toast.makeText(this, "Búsqueda seleccionada", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_notifications) {
            Toast.makeText(this, "Notificaciones seleccionadas", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_settings) {
            Toast.makeText(this, "Configuración seleccionada", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_help) {
            Toast.makeText(this, "Ayuda seleccionada", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_about) {
            Toast.makeText(this, "Acerca de seleccionado", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        // Aquí implementarías el adaptador para el RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_recent_activity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Ejemplo: recyclerView.setAdapter(new ActivityAdapter(getActivities()));
    }

    private void setupListeners() {
        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            // Implementar lógica de cierre de sesión
            apiClient.clearCredentials();
            sessionManager.logout();
            Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        });

        findViewById(R.id.card_action1).setOnClickListener(v -> {
            Toast.makeText(this, "Mi Perfil", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.card_action2).setOnClickListener(v -> {
            Toast.makeText(this, "Configuración", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.card_action3).setOnClickListener(v -> {
            Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.card_action4).setOnClickListener(v -> {
            Toast.makeText(this, "Ayuda", Toast.LENGTH_SHORT).show();
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(InicioActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}