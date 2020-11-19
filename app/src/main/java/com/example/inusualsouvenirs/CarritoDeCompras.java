package com.example.inusualsouvenirs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CarritoDeCompras extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito_de_compras);

        Toolbar toolbar = (Toolbar) findViewById(R.id.carrito_toolbar);
        setSupportActionBar(toolbar);
    }

    public void verProducto(View view) {
        startActivity(new Intent(CarritoDeCompras.this, DetalleProducto.class));
    }

    public void confirmarCompra(View view) {
        startActivity(new Intent(CarritoDeCompras.this, CheckoutProcesoPago.class));
    }
}