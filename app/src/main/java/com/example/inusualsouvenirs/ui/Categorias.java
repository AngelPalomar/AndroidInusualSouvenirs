package com.example.inusualsouvenirs.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.inusualsouvenirs.ProductosPorCategoria;
import com.example.inusualsouvenirs.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Categorias extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private String apiKey;
    private SharedPreferences prefs;

    private SwipeRefreshLayout swpCategorias;
    private ListView lvCategorias;
    private List<String> categList;
    private ArrayAdapter<String> adapter;
    private RequestQueue conexionServ;
    private StringRequest peticionServ;

    public Categorias() { }

    public static Categorias newInstance(String param1, String param2) {
        Categorias fragment = new Categorias();
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
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_categorias, container, false);

        //Obtengo API KEY de la sesión
        prefs = getActivity().getSharedPreferences("inusualapp", getContext().MODE_PRIVATE);
        apiKey = prefs.getString("key", null);

        conexionServ = Volley.newRequestQueue(getActivity());
        lvCategorias = rootView.findViewById(R.id.lv_categorias);
        swpCategorias = rootView.findViewById(R.id.swp_categorias);
        categList = new ArrayList<>();

        //Asigno el adaptador tipo String hacia un layout nativo de android
        adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                categList
        );

        //Asigno adaptador a la lista
        lvCategorias.setAdapter(adapter);

        //Animación de cargando con el Swipe mientras carga las categorías desde el servicio
        swpCategorias.post(new Runnable() {
            @Override
            public void run() {
                swpCategorias.setRefreshing(true);
                cargarCategorias();
            }
        });

        //Método de evento de recarga de lista
        swpCategorias.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cargarCategorias();
                swpCategorias.setRefreshing(false);
            }
        });

        //Método para ver los productos de categoria seleccionada
        lvCategorias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String categValor = lvCategorias.getItemAtPosition(i).toString();
                getContext().startActivity(new Intent(getActivity(), ProductosPorCategoria.class)
                        .putExtra("categoria", categValor)
                );
            }
        });

        //retorna la vista que se haya seleccionado desde el drawer
        return rootView;
    }

    public void cargarCategorias() {
        //Limpio lista por si se recarga la pantala
        categList.clear();
        //El cargando se desactiva
        swpCategorias.setRefreshing(false);

        //Petición GET al servicio
        peticionServ = new StringRequest(
                Request.Method.GET,
                "http://dtai.uteq.edu.mx/~crupal192/AWOS/inusual-souvenirs/webServices/getCategories?key=" + apiKey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getInt("response_code") == 200) {
                                JSONArray categorias = jsonObject.getJSONArray("categorias");

                                for (int i = 0; i < categorias.length(); i++) {
                                    categList.add(categorias.getJSONObject(i).getString("name"));
                                }

                                //Actualiza controlador
                                adapter.notifyDataSetChanged();

                            } else {
                                //Error 404 o 500
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Error inesperado")
                                        .setMessage("Error del servidor: "
                                                + jsonObject.getString("message"))
                                        .setPositiveButton("Aceptar", null)
                                        .setIcon(R.drawable.ic_baseline_error_24)
                                        .show();
                            }
                        } catch (Exception e) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Error inesperado")
                                    .setMessage(e.toString())
                                    .setPositiveButton("Aceptar", null)
                                    .setIcon(R.drawable.ic_baseline_error_24)
                                    .show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        conexionServ.add(peticionServ);
    }
}