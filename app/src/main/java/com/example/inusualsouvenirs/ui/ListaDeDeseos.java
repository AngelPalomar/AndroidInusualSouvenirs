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
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.inusualsouvenirs.R;
import com.example.inusualsouvenirs.adapters.ListaDeseosAdapter;
import com.example.inusualsouvenirs.adapters.ListaProductosAdapter;
import com.example.inusualsouvenirs.utils.Producto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListaDeDeseos extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private String idUsuario, apiKey;
    private SharedPreferences prefs;
    private SwipeRefreshLayout swpListaDeseos;
    private ListView lvListaDeseos;
    private List<Producto> productoList;
    private ListaDeseosAdapter adapter;
    private RequestQueue conexionServ;
    private StringRequest peticionServ;

    public ListaDeDeseos() { }

    public static ListaDeDeseos newInstance(String param1, String param2) {
        ListaDeDeseos fragment = new ListaDeDeseos();
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
        final View rootView = inflater.inflate(R.layout.fragment_lista_de_deseos, container, false);

        //Obtengo el id de usuario
        prefs = getActivity().getSharedPreferences("inusualapp", getContext().MODE_PRIVATE);
        idUsuario = prefs.getString("user_id", null);
        apiKey = prefs.getString("key", null);

        //Conecto las vistas con los controladores
        swpListaDeseos = rootView.findViewById(R.id.swp_lista_deseos);
        lvListaDeseos = rootView.findViewById(R.id.lv_lista_deseos);
        productoList = new ArrayList<>();

        //Asigno adaptador a la lista
        adapter = new ListaDeseosAdapter(getActivity(), productoList, idUsuario);
        lvListaDeseos.setAdapter(adapter);

        //Inicializo la petición Volley
        conexionServ = Volley.newRequestQueue(getActivity());

        //Inicio la carga desde el servicio
        swpListaDeseos.post(new Runnable() {
            @Override
            public void run() {
                cargarListaDeseos();
                swpListaDeseos.setRefreshing(false);
            }
        });

        //asigno acción cuando se recargue la vista
        swpListaDeseos.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cargarListaDeseos();
                swpListaDeseos.setRefreshing(false);
            }
        });

        return rootView;
    }

    public void cargarListaDeseos() {
        //Limpio la lista
        productoList.clear();
        //Paro la carga (animación)
        swpListaDeseos.setRefreshing(false);
        //actualizo los datos en la vista
        adapter.notifyDataSetChanged();

        //Petición para obtener la lista
        peticionServ = new StringRequest(
                Request.Method.GET,
                "http://dtai.uteq.edu.mx/~crupal192/AWOS/inusual-souvenirs/webServices/" +
                        "getWishListApp?user=" + idUsuario + "&key=" + apiKey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);

                            if (data.getInt("response_code") == 200) {
                                JSONArray listaDeDeseos = data.getJSONArray("listaDeDeseos");

                                //Recorro el arreglo del JSON
                                for (int i = 0; i < listaDeDeseos.length(); i++) {
                                    Producto prd = new Producto();

                                    prd.setId(listaDeDeseos.getJSONObject(i).getString("id"));
                                    prd.setNombre(listaDeDeseos.getJSONObject(i).getString("name"));
                                    prd.setImagen(listaDeDeseos.getJSONObject(i).getString("pathImage"));
                                    prd.setPrecio(Float.parseFloat(listaDeDeseos.getJSONObject(i).getString("price")));
                                    prd.setExistencias(Integer.parseInt(listaDeDeseos.getJSONObject(i).getString("stock")));
                                    prd.setDescripcion(listaDeDeseos.getJSONObject(i).getString("description"));
                                    prd.setInformacion(listaDeDeseos.getJSONObject(i).getString("information"));

                                    //Añado el objeto a la lista
                                    productoList.add(prd);
                                }

                                //Actualizo los datos en el adaptador
                                adapter.notifyDataSetChanged();

                            } else {
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Error inesperado")
                                        .setMessage(data.getString("message"))
                                        .setIcon(R.drawable.ic_baseline_info_24)
                                        .setPositiveButton("Aceptar", null)
                                        .show();
                            }

                        } catch (Exception e) {
                            new AlertDialog.Builder(getActivity())
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
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Error inesperado")
                                .setMessage(error.toString())
                                .setIcon(R.drawable.ic_baseline_error_24)
                                .setPositiveButton("Aceptar", null)
                                .show();
                    }
                }
        );

        //ejecuto la petición
        conexionServ.add(peticionServ);
    }
}