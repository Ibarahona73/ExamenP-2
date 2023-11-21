package com.example.examen;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class ListaActivity extends AppCompatActivity {
    private Button btnBack, btnDelete, btnUpdate;

    private ListView listContactos;
    private int valSelected=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listContactos = findViewById(R.id.listContactos);
        Intent intent = getIntent();
        btnDelete = findViewById(R.id.btnDelete);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnBack = findViewById(R.id.btnAtras);
        String nombre = intent.getStringExtra("nombre");
        String telefono = intent.getStringExtra("telefono");
        String latitud = intent.getStringExtra("latitud");
        String longitud = intent.getStringExtra("longitud");
        String videoBase64 = intent.getStringExtra("videoBase64");

        // Crear un ArrayList para almacenar los datos
        ArrayList<String> dataList = new ArrayList<>();
        dataList.add("Nombre: " + nombre);
        dataList.add("Teléfono: " + telefono);
        dataList.add("Latitud: " + latitud);
        dataList.add("Longitud: " + longitud);
        dataList.add("VideoBase64: " + videoBase64);

        // Crear un ArrayAdapter y configurarlo en el ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listContactos.setAdapter(adapter);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    //Se Planeaba hacer un modal que tomara el id que ingresara el usuario y mediante la sentencia php hacer un delete mediante el ID

    /*
    private void eliminarContacto() {
        if(idCont != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmación")
                    .setMessage("¿Desea eliminar el contacto?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            RequestQueue queue = Volley.newRequestQueue(ListaActivity.this);
                            //--->>String a la APiDelete falta no esta en metodos

                            String url = RestApiMethods.ApiDeleteUrl + idCont;

                            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(getApplicationContext(), "Se elimino el contacto ", Toast.LENGTH_SHORT).show();
                                    //limpiarLista();

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "Error al eliminar contacto", Toast.LENGTH_SHORT).show();
                                }
                            });
                            queue.add(stringRequest);

                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getApplicationContext(), "CANCELADO", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
        }
    }
    */


    //Alerta de confirmacion
    private void mostrarDialogoSiNo(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmación");
        builder.setMessage("¿Estás seguro de " + text + "?");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (text.equals("Eliminar")) {
                    String selectedText = (String) listContactos.getItemAtPosition(valSelected).toString();
                    String[] vals = selectedText.split("-");
                    String part1 = vals[0];
                    DeleteContacto(part1);
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Acciones a realizar si se selecciona "No"
                // Aquí puedes agregar el código que se ejecutará cuando se seleccione "No"
            }
        });

        builder.show();
    }


    private void DeleteContacto(String id){
        String[] argWhere={String.valueOf(id)};

    }

}
