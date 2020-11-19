package com.example.inusualsouvenirs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DetalleProducto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detalle_producto_toolbar);
        setSupportActionBar(toolbar);
    }

    public void agregarAListaDeDeseos (View view) {
        startActivity(new Intent(DetalleProducto.this, ListaDeDeseos.class));
    }

    public void comprarAhora(View view) {
        startActivity(new Intent(DetalleProducto.this, CheckoutProcesoPago.class));
    }

    public void agregarAlCarritoProductoDetalle(View view) {
        startActivity(new Intent(DetalleProducto.this, CarritoDeCompras.class));
    }
}