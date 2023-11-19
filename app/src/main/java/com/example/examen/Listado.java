package com.example.examen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Listado extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        listView= findViewById(R.id.Listar);

        // Obtener datos del Intent
        Intent intent = getIntent();
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
        dataList.add("Video Base64: " + videoBase64);

        // Crear un ArrayAdapter y configurarlo en el ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        }
    }


