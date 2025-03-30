package com.example.appbasicauth.api;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final String BASE_URL = "http://107.22.154.13/api/auth/";

    private static ApiClient instance;
    private RequestQueue requestQueue;
    private final Context context;

    private String username;
    private String password;

    private ApiClient(Context context) {
        this.context = context;
        this.requestQueue = getRequestQueue();
    }

    public static synchronized ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void clearCredentials() {
        this.username = null;
        this.password = null;
    }

    private String getAuthHeader() {
        String credentials = username + ":" + password;
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }

    public void register(String nombre, String email, String telefono, String username, String password,
                         final ApiResponseCallback callback) {
        String url = BASE_URL + "register.php";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nombre", nombre);
            jsonBody.put("email", email);
            jsonBody.put("telefono", telefono);
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON body", e);
            callback.onError("Error al crear la solicitud: " + e.getMessage());
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");

                            if (success) {
                                callback.onSuccess(message);
                            } else {
                                callback.onError(message);
                            }
                        } catch (JSONException e) {
                            callback.onError("Error al procesar la respuesta: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleVolleyError(error, callback);
                    }
                });

        requestQueue.add(request);
    }

    public void login(final String username, final String password, final ApiResponseCallback callback) {
        String url = BASE_URL + "verify.php";

        // Guardar credenciales para los encabezados de autenticación
        setCredentials(username, password);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");

                            if (success) {
                                JSONObject user = response.getJSONObject("user");
                                callback.onSuccess("Inicio de sesión exitoso");
                            } else {
                                callback.onError(message);
                            }
                        } catch (JSONException e) {
                            callback.onError("Error al procesar la respuesta: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleVolleyError(error, callback);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", getAuthHeader());
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private void handleVolleyError(VolleyError error, ApiResponseCallback callback) {
        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;

            switch (statusCode) {
                case 400:
                    callback.onError("Datos incorrectos");
                    break;
                case 401:
                    callback.onError("Credenciales incorrectas");
                    break;
                case 404:
                    callback.onError("Recurso no encontrado");
                    break;
                case 409:
                    callback.onError("Usuario o email ya existe");
                    break;
                case 503:
                    callback.onError("Servicio no disponible");
                    break;
                default:
                    callback.onError("Error de red: " + statusCode);
            }
        } else {
            callback.onError("Error de conexión");
        }
    }

    public interface ApiResponseCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}