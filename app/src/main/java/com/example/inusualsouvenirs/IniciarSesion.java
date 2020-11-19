package com.example.inusualsouvenirs;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class IniciarSesion extends AppCompatActivity {

    private EditText edCredencial, edContrasena;
    private Button btnIngresar, btnIrARegistro, btnRegistroFacebook, btnRegistroGoogle;
    private LinearLayout lyLoginFormulario;
    private ProgressBar pbLogin;
    private AlertDialog.Builder alerta;

    /*Elementos de conexión remota*/
    private RequestQueue conexionServ;
    private StringRequest peticionServ;

    /*Variable de preferencias locales*/
    private SharedPreferences prefs;

    /*Editar las preferencias locales*/
    private SharedPreferences.Editor prefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        /*Ligamos los elementos*/
        edCredencial = findViewById(R.id.ed_credencial);
        edContrasena = findViewById(R.id.ed_contrasena);
        lyLoginFormulario = findViewById(R.id.ly_login_formulario);
        pbLogin = findViewById(R.id.pb_login);
        btnIngresar = findViewById(R.id.btn_ingresar);
        btnIrARegistro = findViewById(R.id.btn_ir_a_registro);
        btnRegistroFacebook = findViewById(R.id.btn_registro_facebook);
        btnRegistroGoogle = findViewById(R.id.btn_registro_google);

        alerta = new AlertDialog.Builder(IniciarSesion.this);
        //Petición y conexión
        conexionServ = Volley.newRequestQueue(IniciarSesion.this);

        /*Siya existe algun valor enlas preferencias locales lo enviamos al home*/
        prefs = getSharedPreferences("inusualapp", MODE_PRIVATE);

        /*Si no tenemos el valor de las preferencias, debemos indicar cual será el valor defecto*/
        String usuarioId = prefs.getString("user_id", null);
        if(usuarioId != null) {
            startActivity(new Intent(
                    IniciarSesion.this,
                    TiendaPrincipal.class
            ));
        }
    }

    public void iniciarSesion(View view) {
        //Validaciones
        if (edCredencial.getText().toString().isEmpty() || edContrasena.getText().toString().isEmpty()) {
            alerta.setTitle("Error")
                    .setMessage("Todos los campos son requeridos.")
                    .setCancelable(false)
                    .setNeutralButton("Aceptar", null)
                    .setIcon(R.drawable.ic_baseline_info_24)
                    .show();

            return;
        }

        //Inhabilita cosas
        lyLoginFormulario.setVisibility(view.GONE);
        pbLogin.setVisibility(view.VISIBLE);

        btnIngresar.setEnabled(false);
        btnIrARegistro.setEnabled(false);
        btnRegistroFacebook.setEnabled(false);
        btnRegistroGoogle.setEnabled(false);

        /*Inicializamos la peticion
         * 1.- Tipo de envio
         * 2.- URl dle servicio
         * 3.- Metodo de respuesta
         * 4.- Metodo de error
         * 5.- (OPCIONAL) parametros de envio POST*/

        peticionServ = new StringRequest(
                Request.Method.POST,
                "http://dtai.uteq.edu.mx/~crupal192/AWOS/inusual-souvenirs/index.php/seguridad/LonginUS",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject respuesta = new JSONObject(response);

                            if (respuesta.getInt("response_code") == 200) {
                                JSONObject datosUsuario = respuesta.getJSONObject("datos_usuario");
                                prefs = getSharedPreferences("inusualapp", MODE_PRIVATE);
                                prefsEditor = prefs.edit();

                                //Agregamos las variables locales
                                prefsEditor.putString("user_id", datosUsuario.getString("id"));
                                prefsEditor.putString("name", datosUsuario.getString("name"));
                                prefsEditor.putString("lastname", datosUsuario.getString("lastname"));
                                prefsEditor.putString("phone", datosUsuario.getString("phone"));
                                prefsEditor.putString("email", datosUsuario.getString("email"));

                                //Guardo las variables
                                prefsEditor.commit();
                                
                                startActivity(new Intent(IniciarSesion.this, TiendaPrincipal.class));
                            }

                            if (respuesta.getInt("response_code") == 404) {
                                alerta.setTitle("Error inesperado")
                                        .setMessage("Usuario o contraseña incorrectos")
                                        .setCancelable(false)
                                        .setNeutralButton("Aceptar", null)
                                        .setIcon(R.drawable.ic_baseline_error_24)
                                        .show();

                                //Limpiar campos
                                edCredencial.setText("");
                                edContrasena.setText("");
                            }

                            if (respuesta.getInt("response_code") == 400) {
                                alerta.setTitle("Error inesperado")
                                        .setMessage(respuesta.getString("errors"))
                                        .setCancelable(false)
                                        .setNeutralButton("Aceptar", null)
                                        .setIcon(R.drawable.ic_baseline_error_24)
                                        .show();
                            }

                        } catch (Exception e) {
                            alerta.setTitle("Error inesperado")
                                    .setMessage(e.getMessage())
                                    .setCancelable(false)
                                    .setNeutralButton("Aceptar", null)
                                    .setIcon(R.drawable.ic_baseline_error_24)
                                    .show();
                        }

                        pbLogin.setVisibility(View.GONE);
                        lyLoginFormulario.setVisibility(View.VISIBLE);

                        btnIngresar.setEnabled(true);
                        btnIrARegistro.setEnabled(true);
                        btnRegistroFacebook.setEnabled(true);
                        btnRegistroGoogle.setEnabled(true);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        alerta.setTitle("Error inesperado")
                                .setMessage(error.toString())
                                .setCancelable(false)
                                .setNeutralButton("Aceptar", null)
                                .setIcon(R.drawable.borrar)
                                .show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", edCredencial.getText().toString());
                params.put("password", Helper.MD5_Hash(edContrasena.getText().toString()));
                return params;
            }
        };

        conexionServ.add(peticionServ);

    }

    public void registrarse(View view) {
        startActivity(new Intent(IniciarSesion.this, Registro.class));
    }
}