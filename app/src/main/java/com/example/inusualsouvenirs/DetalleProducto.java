package com.example.inusualsouvenirs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.inusualsouvenirs.ui.ListaDeDeseos;
import com.example.inusualsouvenirs.utils.Producto;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class DetalleProducto extends AppCompatActivity {

    private TextView tvNombre, tvPrecio, tvExistencias, tvDescripcion, tvInformacion;
    private ImageView imgvProducto;
    private Button btnAnadirListaDeseos;
    private Producto producto;
    private String idUsuario;
    private SharedPreferences prefs;
    private RequestQueue conexionServ;
    private StringRequest peticionServ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detalle_producto_toolbar);
        setSupportActionBar(toolbar);

        tvNombre = findViewById(R.id.tv_nombre_producto_detalle);
        tvPrecio = findViewById(R.id.tv_precio_detalle);
        tvExistencias = findViewById(R.id.tv_existencias);
        tvDescripcion = findViewById(R.id.tv_descripcion_producto);
        tvInformacion = findViewById(R.id.tv_informacion_producto);
        imgvProducto = findViewById(R.id.imgv_producto_detalle);
        btnAnadirListaDeseos = findViewById(R.id.btn_anadir_deseos);

        //Obtengo datos locales
        prefs = getSharedPreferences("inusualapp", MODE_PRIVATE);
        idUsuario = prefs.getString("user_id", null);

        //Obtengo el producto seleccionado desde la lista de productos
        producto = (Producto) getIntent().getSerializableExtra("Producto");

        //Muestro la imagen
        Picasso.with(DetalleProducto.this)
                .load("http://dtai.uteq.edu.mx/~crupal192/AWOS/inusual-souvenirs/" +
                        producto.getImagen())
                .into(imgvProducto);

        //Muestro los datos
        tvNombre.setText(producto.getNombre());
        tvPrecio.setText(String.format("$%.2f", producto.getPrecio()));
        tvExistencias.setText(String.valueOf(producto.getExistencias()));
        tvDescripcion.setText(producto.getDescripcion());
        tvInformacion.setText(producto.getInformacion());
    }

    public void agregarAListaDeDeseos (View view) {
        conexionServ = Volley.newRequestQueue(DetalleProducto.this);

        peticionServ = new StringRequest(
                Request.Method.GET,
                "http://dtai.uteq.edu.mx/~crupal192/AWOS/inusual-souvenirs/productos/" +
                        "addProductWishListApp?user=" + idUsuario + "&product=" + producto.getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("response_code") == 200) {
                                new AlertDialog.Builder(DetalleProducto.this)
                                        .setTitle("Producto agregado")
                                        .setMessage(data.getString("message"))
                                        .setIcon(R.drawable.ic_baseline_check_24)
                                        .setPositiveButton("Aceptar", null)
                                        .show();
                            } else {
                                new AlertDialog.Builder(DetalleProducto.this)
                                        .setTitle("Aviso")
                                        .setMessage(data.getString("message"))
                                        .setIcon(R.drawable.ic_baseline_info_24)
                                        .setPositiveButton("Aceptar", null)
                                        .show();
                            }
                        } catch (Exception e) {
                            new AlertDialog.Builder(DetalleProducto.this)
                                    .setTitle("Error inesperado")
                                    .setMessage(e.toString())
                                    .setIcon(R.drawable.ic_baseline_error_24)
                                    .setPositiveButton("Aceptar", null)
                                    .show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        new AlertDialog.Builder(DetalleProducto.this)
                                .setTitle("Error inesperado")
                                .setMessage(error.toString())
                                .setIcon(R.drawable.ic_baseline_error_24)
                                .setPositiveButton("Aceptar", null)
                                .show();
                    }
                }
        );

        //ejecuto la conexión
        conexionServ.add(peticionServ);
    }

    public void agregarAlCarritoProductoDetalle(View view) {
        startActivity(new Intent(DetalleProducto.this, CarritoDeCompras.class));
    }
}