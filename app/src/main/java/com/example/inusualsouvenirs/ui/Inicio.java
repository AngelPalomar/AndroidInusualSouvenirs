package com.example.inusualsouvenirs.ui;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.inusualsouvenirs.R;
import com.example.inusualsouvenirs.adapters.ListaProductosAdapter;
import com.example.inusualsouvenirs.utils.Producto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Inicio extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private String apiKey;
    private SharedPreferences prefs;

    private SwipeRefreshLayout swInicio;
    private ListView lvProductos;
    private List<Producto> productos;
    private ListaProductosAdapter adapter;
    private RequestQueue conexionServ;
    private StringRequest peticionServ;

    public Inicio() { }

    public static Inicio newInstance(String param1, String param2) {
        Inicio fragment = new Inicio();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Guardar vista principal
        final View rootView = inflater.inflate(R.layout.fragment_inicio, container, false);

        //Obtengo API KEY de la sesión
        prefs = getActivity().getSharedPreferences("inusualapp", getContext().MODE_PRIVATE);
        apiKey = prefs.getString("key", null);

        swInicio = rootView.findViewById(R.id.swp_inicio);
        lvProductos = rootView.findViewById(R.id.lv_productos);
        productos = new ArrayList<>();

        adapter = new ListaProductosAdapter(getActivity(), productos);
        lvProductos.setAdapter(adapter);

        conexionServ = Volley.newRequestQueue(getActivity());

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

        return rootView;
    }

    private void cargarListaProductos() {
        swInicio.setRefreshing(false);
        //Limpio lista
        productos.clear();
        adapter.notifyDataSetChanged();

        peticionServ = new StringRequest(
                Request.Method.GET,
                "http://dtai.uteq.edu.mx/~crupal192/AWOS/inusual-souvenirs/webServices/listaProductos?key=" + apiKey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getInt("response_code") == 200) {
                                JSONArray arrayProductos = jsonObject.getJSONArray("listaproductos");

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
                            new AlertDialog.Builder(getActivity())
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
                        new AlertDialog.Builder(getActivity())
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