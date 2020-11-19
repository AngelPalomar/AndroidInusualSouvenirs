package com.example.inusualsouvenirs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MiPerfil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_perfil);

        Toolbar toolbar = (Toolbar) findViewById(R.id.perfil_toolbar); //Crea la toolbar
        setSupportActionBar(toolbar); //Establece a ins_toolbar como toolbar
    }

    public void verProductoDesdeHistorial (View view) {
        startActivity(new Intent(MiPerfil.this, DetalleProducto.class));
    }
}