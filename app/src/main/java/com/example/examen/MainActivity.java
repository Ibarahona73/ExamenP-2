package com.example.examen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
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

import com.google.android.material.card.MaterialCardView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Save = findViewById(R.id.btnSave);
        btnCaptureVideo = findViewById(R.id.btnCaptureVideo);
        cardViewPreview = findViewById(R.id.cardViewPreview);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextLongitud = findViewById(R.id.editTextLongi);
        editTextLatitud = findViewById(R.id.editTextLatid);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            // Obtener la URI del video capturado
            Uri videoUri = data.getData();

            // Mostrar la vista previa del video en el CardView
            cardViewPreview.setVisibility(View.VISIBLE);

            // Mostrar el video en un VideoView dentro del CardView
            VideoView videoView = findViewById(R.id.videoViewPreview);
            videoView.setVideoURI(videoUri);


            // Añadir controles de reproducción al VideoView
            MediaController mediaController = new MediaController(this);
            videoView.setMediaController(mediaController);

            // Iniciar la reproducción del video
            videoView.start();
        }
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

}
