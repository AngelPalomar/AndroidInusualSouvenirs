package com.example.inusualsouvenirs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    private EditText edNombre, edApellido, edCorreo, edTelefono, edContrasena,
            edConfirmarContrasena, edCalle, edNumero, edColonia, edCodigoPost;
    private Button btnRegistro;
    private ProgressBar pbRegistro;
    private LinearLayout lyRegistroFormulario;
    private AlertDialog.Builder alerta;

    private RequestQueue conexionServ;
    private StringRequest peticionServ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        alerta = new AlertDialog.Builder(this);
        conexionServ = Volley.newRequestQueue(Registro.this);

        edNombre = findViewById(R.id.ed_nombre);
        edApellido = findViewById(R.id.ed_apellido);
        edCorreo = findViewById(R.id.ed_correo);
        edTelefono = findViewById(R.id.ed_telefono);
        edContrasena = findViewById(R.id.ed_contrasena_registro);
        edConfirmarContrasena = findViewById(R.id.ed_confirmar_contrasena);
        edCalle = findViewById(R.id.ed_calle);
        edNumero = findViewById(R.id.ed_numero);
        edColonia = findViewById(R.id.ed_colonia);
        edCodigoPost = findViewById(R.id.ed_cp);
        btnRegistro = findViewById(R.id.btn_registro);
        pbRegistro = findViewById(R.id.pb_registro);
        lyRegistroFormulario = findViewById(R.id.ly_registro_formulario);

    }

    public void crearCuenta(View view) {

        //Validaciones si campos son vacíos
        if (edNombre.getText().toString().isEmpty() || edApellido.getText().toString().isEmpty() ||
                edCorreo.getText().toString().isEmpty() || edTelefono.getText().toString().isEmpty() ||
                edContrasena.getText().toString().isEmpty() || edConfirmarContrasena.getText().toString().isEmpty() ||
                edCalle.getText().toString().isEmpty() || edNumero.getText().toString().isEmpty() ||
                edColonia.getText().toString().isEmpty() || edCodigoPost.getText().toString().isEmpty()) {
            alerta.setTitle("Error")
                    .setMessage("Todos los campos son requeridos")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .setIcon(R.drawable.ic_baseline_info_24)
                    .show();

            return;
        }

        //Validación de teléfono
        if (edTelefono.getText().toString().length() < 10) {
            alerta.setTitle("Error")
                    .setMessage("El número de teléfono debe pertenecer al formato de 10 dígitos")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .setIcon(R.drawable.ic_baseline_info_24)
                    .show();

            return;
        }

        //Validación de contraseñas
        if (edContrasena.getText().toString().length() < 8) {
            alerta.setTitle("Error")
                    .setMessage("La contraseña debe tener al menos 8 caracteres")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .setIcon(R.drawable.ic_baseline_info_24)
                    .show();

            return;
        }

        if (!edContrasena.getText().toString().equals(edConfirmarContrasena.getText().toString())) {
            alerta.setTitle("Error")
                    .setMessage("Las contraseñas deben ser iguales")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", null)
                    .setIcon(R.drawable.ic_baseline_info_24)
                    .show();

            return;
        }

        btnRegistro.setEnabled(false);
        lyRegistroFormulario.setVisibility(View.GONE);
        pbRegistro.setVisibility(View.VISIBLE);

        peticionServ = new StringRequest(
                Request.Method.POST,
                "http://dtai.uteq.edu.mx/~crupal192/AWOS/inusual-souvenirs/index.php/seguridad/registro",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("OK")) {
                            alerta.setTitle("Registro completado")
                                    .setMessage("Inicie sesión para continuar")
                                    .setCancelable(false)
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(Registro.this, IniciarSesion.class));
                                        }
                                    })
                                    .setIcon(R.drawable.ic_baseline_check_24)
                                    .show();
                        } else {
                            alerta.setTitle("Error")
                                    .setMessage(response)
                                    .setCancelable(false)
                                    .setPositiveButton("Aceptar", null)
                                    .setIcon(R.drawable.ic_baseline_error_24)
                                    .show();
                        }

                        pbRegistro.setVisibility(View.GONE);
                        lyRegistroFormulario.setVisibility(View.VISIBLE);
                        btnRegistro.setEnabled(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        alerta.setTitle("Error inesperado")
                                .setMessage(error.toString())
                                .setCancelable(false)
                                .setPositiveButton("Aceptar", null)
                                .setIcon(R.drawable.ic_baseline_error_24)
                                .show();

                        pbRegistro.setVisibility(View.GONE);
                        lyRegistroFormulario.setVisibility(View.VISIBLE);
                        btnRegistro.setEnabled(true);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("password", edContrasena.getText().toString());
                params.put("email", edCorreo.getText().toString());
                params.put("phone", edTelefono.getText().toString());
                params.put("lastname", edApellido.getText().toString());
                params.put("calle", edCalle.getText().toString());
                params.put("cp", edCodigoPost.getText().toString());
                params.put("number", edNumero.getText().toString());
                params.put("colonia", edColonia.getText().toString());
                params.put("name", edNombre.getText().toString());
                params.put("id_municipio", String.valueOf(1));

                return params;
            }
        };

        conexionServ.add(peticionServ);
    }
}