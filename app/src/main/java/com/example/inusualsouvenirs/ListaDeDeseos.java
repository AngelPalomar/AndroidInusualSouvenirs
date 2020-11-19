package com.example.inusualsouvenirs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ListaDeDeseos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_deseos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.lista_deseos_toolbar);
        setSupportActionBar(toolbar);
    }

    public void volverTienda(View view) {
        startActivity(new Intent(ListaDeDeseos.this, TiendaPrincipal.class));
    }

    public void agregarDeseosCarrito(View view) {
        startActivity(new Intent(ListaDeDeseos.this, CarritoDeCompras.class));
    }
}