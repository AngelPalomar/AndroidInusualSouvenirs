package com.example.inusualsouvenirs.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inusualsouvenirs.DetalleProducto;
import com.example.inusualsouvenirs.R;
import com.example.inusualsouvenirs.utils.Producto;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class ListaProductosAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<Producto> productos;

    public ListaProductosAdapter(Context context, List<Producto> productos) {
        this.context = context;
        this.productos = productos;
        layoutInflater = LayoutInflater.from(context);
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
            view = layoutInflater.inflate(R.layout.item_producto, null);
            TextView tvNombreProducto = view.findViewById(R.id.tv_nombre_producto);
            TextView tvPrecio = view.findViewById(R.id.tv_precio);
            ImageView imgvProducto = view.findViewById(R.id.imgv_producto);
            Button btnAnadirCarrito = view.findViewById(R.id.btn_anadir_carrito);

            //Evento para visualizar el detalle del producto
            imgvProducto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, DetalleProducto.class)
                            .putExtra("Producto", (Serializable) productos.get(i))
                    );
                }
            });

            tvNombreProducto.setText(productos.get(i).getNombre());
            tvPrecio.setText(String.format("$%.2f", productos.get(i).getPrecio()));

            Picasso.with(context)
                    .load("http://dtai.uteq.edu.mx/~crupal192/AWOS/inusual-souvenirs/" +
                            productos.get(i).getImagen())
                    .into(imgvProducto);
        }

        return view;
    }
}
