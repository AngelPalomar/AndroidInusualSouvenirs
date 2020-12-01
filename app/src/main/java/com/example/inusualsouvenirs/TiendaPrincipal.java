package com.example.inusualsouvenirs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TiendaPrincipal extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefsEditor;
    private AppBarConfiguration mAppBarConfiguration;
    private TextView drawerHeader;
    private TextView drawerSubtitle;
    private SharedPreferences prefs;
    private String nombreUsuario;
    private String correoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tienda_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_categorias, R.id.nav_cuenta, R.id.nav_lista_deseos, R.id.nav_mis_compras)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Inicializar el header del drawer para cambiar datos
        View headView = navigationView.getHeaderView(0);

        drawerHeader = headView.findViewById(R.id.drawer_header);
        drawerSubtitle = headView.findViewById(R.id.drawer_subtitle);

        prefs = getSharedPreferences("inusualapp", MODE_PRIVATE);
        nombreUsuario = prefs.getString("name", null) + " " + prefs.getString("lastname", null);
        correoUsuario = String.valueOf(prefs.getString("email", null));

        drawerHeader.setText(nombreUsuario);
        drawerSubtitle.setText(correoUsuario);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_toolbar, menu);
        return true;
    }

    //Obtiene el ID de los items y ejecuta acciones
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.to_carrito_compras:
                startActivity(new Intent(TiendaPrincipal.this, CarritoDeCompras.class));
                return true;

            case R.id.cerrar_sesion:

                //Cerrar sesión
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() { }

    public void logout() {
        sharedPreferences = getSharedPreferences("inusualapp", MODE_PRIVATE);
        prefsEditor = sharedPreferences.edit();

        prefsEditor.clear();
        prefsEditor.commit();

        startActivity(new Intent(TiendaPrincipal.this, IniciarSesion.class));
    }
}