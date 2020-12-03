package com.example.inusualsouvenirs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
    private String sesion;

    /*Elementos de conexión remota*/
    private RequestQueue conexionServ;
    private StringRequest peticionServ;

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

        //Inicializa la conexión
        conexionServ = Volley.newRequestQueue(TiendaPrincipal.this);

        //Inicializar el header del drawer para cambiar datos
        View headView = navigationView.getHeaderView(0);

        drawerHeader = headView.findViewById(R.id.drawer_header);
        drawerSubtitle = headView.findViewById(R.id.drawer_subtitle);

        prefs = getSharedPreferences("inusualapp", MODE_PRIVATE);
        nombreUsuario = prefs.getString("name", null) + " " + prefs.getString("lastname", null);
        correoUsuario = String.valueOf(prefs.getString("email", null));
        sesion = String.valueOf(prefs.getString("session", null));

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
        //Servicio para borrar la sesión actual
        peticionServ = new StringRequest(
                Request.Method.GET,
                "http://dtai.uteq.edu.mx/~crupal192/AWOS/inusual-souvenirs/index.php/" +
                        "seguridad/salir?key=" + sesion,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response.equals("OK")) {
                                Toast.makeText(TiendaPrincipal.this, "A cerrado sesión", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(TiendaPrincipal.this, "No se pudo cerrar sesión", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(TiendaPrincipal.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TiendaPrincipal.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        //Ejecuta la petición
        conexionServ.add(peticionServ);

        sharedPreferences = getSharedPreferences("inusualapp", MODE_PRIVATE);
        prefsEditor = sharedPreferences.edit();

        prefsEditor.clear();
        prefsEditor.commit();

        startActivity(new Intent(TiendaPrincipal.this, IniciarSesion.class));
    }
}