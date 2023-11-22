package com.example.examen;

import android.util.Base64;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.util.Log;
import android.widget.MediaController;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_VIDEO_CAPTURE = 101;
    private static final int REQUEST_LOCATION_PERMISSION = 102;
    private LocationManager locationManager;
    private Button btnCaptureVideo;
    private Button Save;
    private MaterialCardView cardViewPreview;
    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextLatitud;
    private EditText editTextLongitud;
    private static final String SERVER_URL = RestApiMethods.EndpointPost;
    private RequestQueue requestQueue;
    private String videoBase64;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar la cola de solicitudes Volley
        requestQueue = Volley.newRequestQueue(this);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Save = findViewById(R.id.btnSave);
        btnCaptureVideo = findViewById(R.id.btnCaptureVideo);
        cardViewPreview = findViewById(R.id.cardViewPreview);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextLongitud = findViewById(R.id.editTextLongi);
        editTextLatitud = findViewById(R.id.editTextLatid);
        videoView = findViewById(R.id.videoViewPreview);

        getLocation();

        btnCaptureVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionsAndCaptureVideo();

            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los datos del formulario
                String nombre = editTextName.getText().toString();
                String telefono = editTextPhone.getText().toString();
                String latitud = editTextLatitud.getText().toString();
                String longitud = editTextLongitud.getText().toString();

                // Validar que se hayan ingresado los datos necesarios
                if (nombre.isEmpty() || telefono.isEmpty() || latitud.isEmpty() || longitud.isEmpty() /*|| videoBase64 == null*/) {
                    Toast.makeText(MainActivity.this, "Completa todos los campos y captura un video", Toast.LENGTH_SHORT).show();
                } else {
                    // Enviar datos al servidor
                    sendDataToServer(nombre, telefono, latitud, longitud, videoBase64);
                }
            }
        });
    }

    private void checkPermissionsAndCaptureVideo() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO}, REQUEST_VIDEO_CAPTURE);
        } else {
            captureVideo();
        }
    }

    private void captureVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }



    private String convertVideoToBase64(Uri videoUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(videoUri);
            byte[] bytes;
            if (inputStream != null) {
                bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                inputStream.close();

                // Convertir a base64
                byte[] base64Bytes = Base64.encode(bytes, Base64.DEFAULT);
                return new String(base64Bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();

            // Convertir el video a base64
            videoBase64 = convertVideoToBase64(videoUri);

            // Mostrar la vista previa del video
            showVideoPreview(videoUri);
        }
    }

    private void showVideoPreview(Uri videoUri) {
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(videoUri);

        // Añadir controles de reproducción al VideoView
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);

        // Iniciar la reproducción del video
        videoView.start();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_VIDEO_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureVideo();
            }
        }

    }


    //UBICACION

    private void getLocation() {
        // Verificar si los permisos de ubicación están otorgados
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Obtener la última ubicación conocida
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // Si hay una ubicación conocida, actualizar la ubicación en los campos de texto
            if (lastKnownLocation != null) {
                editTextLatitud.setText(String.valueOf(lastKnownLocation.getLatitude()));
                editTextLongitud.setText(String.valueOf(lastKnownLocation.getLongitude()));
            } else {
                // Si no hay una ubicación conocida, solicitar una actualización única de la ubicación
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, singleLocationListener, null);
            }

        } else {

            // Solicitar permisos de ubicación si no están otorgados
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);

        }
    }


    // LocationListener para manejar una única actualización de ubicación
    private final LocationListener singleLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // Obtener la latitud y longitud
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            // Actualizar los campos de texto con la latitud y longitud
            editTextLatitud.setText(String.valueOf(latitude));
            editTextLongitud.setText(String.valueOf(longitude));

            // Detener las actualizaciones de ubicación después de obtener la ubicación
            if (locationManager != null) {
                locationManager.removeUpdates(singleLocationListener);
            }
        }

        // Otros métodos del LocationListener que puedes implementar según tus necesidades

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Puedes manejar cambios en el estado de la ubicación si es necesario
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Puedes realizar acciones cuando el proveedor de ubicación está habilitado
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Puedes realizar acciones cuando el proveedor de ubicación está deshabilitado
        }
    };

    @Override
    protected void onDestroy() {
        // Detener las actualizaciones de ubicación cuando la actividad se destruye
        if (locationManager != null) {
            locationManager.removeUpdates(singleLocationListener);
        }
        super.onDestroy();
    }

    //Enviar datos al Servidor

    private void sendDataToServer(String nombre, String telefono, String latitud, String longitud, String videoBase64) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("name", nombre);
            postData.put("phone", telefono);
            postData.put("latitude", latitud);
            postData.put("longitude", longitud);
            postData.put("video", videoBase64);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Tener como limite 15s como espera ya despues de eso pues que se corte la conexion.
        int MY_TIMEOUT_MS = 15000; // 15 segundos
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SERVER_URL, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());
                        Toast.makeText(MainActivity.this, "Datos enviados con éxito", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            Log.e("Error", "TimeoutError: " + error.toString());
                            Toast.makeText(MainActivity.this, "Tiempo de espera agotado", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Error", "Error en la solicitud al servidor: " + error.toString());
                            Toast.makeText(MainActivity.this, "Error al enviar datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Establecer el tiempo de espera
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        //Enviar los datos al adaptador del ListView
        Intent intent = new Intent(MainActivity.this, ListaActivity.class);
        intent.putExtra("name", nombre);
        intent.putExtra("phone", telefono);
        intent.putExtra("latitude", latitud);
        intent.putExtra("longitude", longitud);
        intent.putExtra("video", videoBase64);
        startActivity(intent);

        requestQueue.add(jsonObjectRequest);

    }
}
