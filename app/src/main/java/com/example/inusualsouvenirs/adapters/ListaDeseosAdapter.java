package com.example.inusualsouvenirs.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.inusualsouvenirs.DetalleProducto;
import com.example.inusualsouvenirs.R;
import com.example.inusualsouvenirs.utils.Producto;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class ListaDeseosAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<Producto> productos;

    //Servicios para eliminar
    private String idUsuario;
    private RequestQueue conexionServ;
    private StringRequest peticionServ;

    public ListaDeseosAdapter(Context context, List<Producto> productos, String idUsuario) {
        this.context = context;
        this.productos = productos;
        layoutInflater = LayoutInflater.from(context);
        this.idUsuario = idUsuario;
    }

    @Override
    public int getCount() {
        return productos.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_lista_deseos, null);
            final String apiKey;
            SharedPreferences prefs;

            prefs = context.getSharedPreferences("inusualapp", context.MODE_PRIVATE);
            apiKey = prefs.getString("key", null);

            ImageView imgProducto = view.findViewById(R.id.imgv_producto);
            TextView tvNombreProducto = view.findViewById(R.id.tv_nombre_producto);
            TextView tvPrecio = view.findViewById(R.id.tv_precio);
            TextView tvExist = view.findViewById(R.id.tv_existencias);
            ImageButton btnEliminar = view.findViewById(R.id.btn_eliminar_item_deseos);

            //Muestra de los datos
            Picasso.with(context)
                    .load("http://dtai.uteq.edu.mx/~crupal192/AWOS/inusual-souvenirs/" +
                            productos.get(i).getImagen())
                    .into(imgProducto);
            tvNombreProducto.setText(productos.get(i).getNombre());
            tvPrecio.setText(String.format("$%.2f", productos.get(i).getPrecio()));
            tvExist.setText(String.valueOf(productos.get(i).getExistencias()));

            //Evento para visualizar el detalle del producto
            imgProducto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, DetalleProducto.class)
                            .putExtra("Producto", (Serializable) productos.get(i))
                    );
                }
            });

            //Acción para eliminar item
            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    conexionServ = Volley.newRequestQueue(context);

                    peticionServ = new StringRequest(
                            Request.Method.GET,
                            "http://dtai.uteq.edu.mx/~crupal192/AWOS/inusual-souvenirs/webServices/" +
                                    "deleteItemWishListApp?user=" + idUsuario + "&product=" + productos.get(i).getId() +
                            "&key=" + apiKey,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject data = new JSONObject(response);

                                        if (data.getInt("response_code") == 200) {
                                            new AlertDialog.Builder(context)
                                                    .setTitle("Producto eliminado")
                                                    .setMessage(data.getString("message"))
                                                    .setIcon(R.drawable.ic_baseline_check_24)
                                                    .setPositiveButton("Aceptar", null)
                                                    .show();
                                        } else {
                                            new AlertDialog.Builder(context)
                                                    .setTitle("Aviso")
                                                    .setMessage(data.getString("message"))
                                                    .setIcon(R.drawable.ic_baseline_info_24)
                                                    .setPositiveButton("Aceptar", null)
                                                    .show();
                                        }
                                    } catch (Exception e) {
                                        new AlertDialog.Builder(context)
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
                                    new AlertDialog.Builder(context)
                                            .setTitle("Error inesperado")
                                            .setMessage(error.toString())
                                            .setIcon(R.drawable.ic_baseline_error_24)
                                            .setPositiveButton("Aceptar", null)
                                            .show();
                                }
                            }
                    );

                    //Ejecuto la petición
                    conexionServ.add(peticionServ);
                }
            });
        }

        return view;
    }
}
