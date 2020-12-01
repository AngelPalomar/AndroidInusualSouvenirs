package com.example.inusualsouvenirs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.inusualsouvenirs.adapters.ListaProductosAdapter;
import com.example.inusualsouvenirs.utils.Producto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductosPorCategoria extends AppCompatActivity {

    private String categoria;
    private SwipeRefreshLayout swInicio;
    private ListView lvProductos;
    private List<Producto> productos;
    private ListaProductosAdapter adapter;
    private RequestQueue conexionServ;
    private StringRequest peticionServ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_por_categoria);

        Toolbar toolbar = (Toolbar) findViewById(R.id.producto_categoria_toolbar);
        setSupportActionBar(toolbar);

        //Obtengo la categoria que se seleccionó
        categoria = getIntent().getStringExtra("categoria");

        //Cambio título de la barra de navegación
        toolbar.setTitle(categoria);

        //Inicializo componentes
        productos = new ArrayList<>();
        swInicio = findViewById(R.id.swp_productos_categorias);
        lvProductos = findViewById(R.id.lv_productos);
        adapter = new ListaProductosAdapter(ProductosPorCategoria.this, productos);

        //Asigno adaptador de la lista
        lvProductos.setAdapter(adapter);

        //Inicio la petición Volley
        conexionServ = Volley.newRequestQueue(ProductosPorCategoria.this);

        //Recarga de la lista
        swInicio.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cargarListaProductos();
                swInicio.setRefreshing(false);
            }
        });

        swInicio.post(new Runnable() {
            @Override
            public void run() {
                swInicio.setRefreshing(true);
                cargarListaProductos();
            }
        });
    }

    private void cargarListaProductos() {
        swInicio.setRefreshing(false);
        //Limpio lista
        productos.clear();
        adapter.notifyDataSetChanged();

        peticionServ = new StringRequest(
                Request.Method.GET,
                "http://dtai.uteq.edu.mx/~crupal192/AWOS/inusual-souvenirs/productos/" +
                        "listaProductos?category=" + categoria,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getInt("response_code") == 200) {
                                JSONArray arrayProductos = jsonObject.getJSONArray("productos_categoria");

                                for (int i = 0; i < arrayProductos.length(); i++) {
                                    //JSONObject producto = arrayProductos.getJSONObject(i);
                                    Producto producto = new Producto();

                                    producto.setNombre(arrayProductos.getJSONObject(i).getString("name"));
                                    producto.setPrecio(Float.parseFloat(arrayProductos.getJSONObject(i).getString("price")));
                                    producto.setImagen(arrayProductos.getJSONObject(i).getString("pathImage"));
                                    producto.setCategoria(arrayProductos.getJSONObject(i).getString("category"));
                                    producto.setExistencias(Integer.parseInt(arrayProductos.getJSONObject(i).getString("stock")));
                                    producto.setDescripcion(arrayProductos.getJSONObject(i).getString("description"));
                                    producto.setInformacion(arrayProductos.getJSONObject(i).getString("information"));
                                    producto.setId(arrayProductos.getJSONObject(i).getString("id_product"));

                                    productos.add(producto);
                                }

                                //Actualiza controlador
                                adapter.notifyDataSetChanged();

                            }
                        } catch (Exception e) {
                            swInicio.setRefreshing(false);
                            new AlertDialog.Builder(ProductosPorCategoria.this)
                                    .setTitle("Error inesperado")
                                    .setMessage("Verifique su conexión a internet")
                                    .setPositiveButton("Aceptar", null)
                                    .setIcon(R.drawable.ic_baseline_error_24)
                                    .show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swInicio.setRefreshing(false);
                        new AlertDialog.Builder(ProductosPorCategoria.this)
                                .setTitle("Error inesperado")
                                .setMessage("Error del servidor, vuevla a intentarlo.")
                                .setPositiveButton("Aceptar", null)
                                .setIcon(R.drawable.ic_baseline_error_24)
                                .show();
                    }
                }
        );

        conexionServ.add(peticionServ);
    }
}