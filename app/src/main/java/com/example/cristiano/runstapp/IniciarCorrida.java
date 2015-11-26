package com.example.cristiano.runstapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class IniciarCorrida extends Activity implements LocationListener {

    private static final long MIN_TIME_BW_UPDATES = 1000;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final int ALTITUDE = 20;
    GoogleMap googleMap;
    LocationManager lm;
    Location location;
    double latitude = 0.0;
    double longitude = 0.0;
    double lastLatitude = 0.0;
    double lastLongitude = 0.0;
    float distanciaAcumulada = 0;
    private ArrayList<LatLng> pontos;
    private ArrayList<Float> velocidades;
    private ArrayList<Double> latitudes;
    private ArrayList<Double> longitudes;
    private Polyline linha;
    private boolean canGetLocation = false;
    float [] resultados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_iniciar_corrida);

        pontos = new ArrayList<LatLng>();
        velocidades = new ArrayList<Float>();
        resultados = new float[3];

        if (googleMap == null)
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        if (googleMap == null) {
            //Toast.makeText(getApplicationContext(), "Impossível carregar mapa.", Toast.LENGTH_SHORT).show();
        } else {
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            if (location == null)
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            iniciaMapa();
        }
    }

    public void iniciaMapa() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastLatitude = latitude;
        lastLongitude = longitude;

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(ALTITUDE).build();

        googleMap.setMapType(googleMap.MAP_TYPE_NORMAL);
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {

        lastLatitude = latitude;
        lastLongitude = longitude;

        latitude = location.getLatitude();
        longitude = location.getLongitude();


        location.distanceBetween(lastLatitude, lastLongitude, latitude, longitude, resultados);
        distanciaAcumulada += resultados[0];


        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(ALTITUDE).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        TextView dis = (TextView) findViewById(R.id.distancia);
        TextView vel = (TextView) findViewById(R.id.velocidade);

        dis.setText(String.valueOf(distanciaAcumulada) + " Metros");
        vel.setText(String.valueOf(location.getSpeed()) + " m/s");

        LatLng latLng = new LatLng(latitude,longitude);

        pontos.add(latLng);
        velocidades.add(location.getSpeed());

        redesenhaLinha();
    }

    private void redesenhaLinha(){

        googleMap.clear();

        PolylineOptions opcoes = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);

        for (int i=0 ; i< pontos.size(); i++)
        {
            LatLng ponto = pontos.get(i);
            opcoes.add(ponto);
        }

        linha = googleMap.addPolyline(opcoes);

    }

    public void voltaTelaInicial(View view){
        Intent intent = new Intent(IniciarCorrida.this, MainActivity.class);
        startActivity(intent);
    }

    public void salvaCorrida(View view) throws IOException {
        Intent intent = new Intent(IniciarCorrida.this, MainActivity.class);
        Date date = new Date(System.currentTimeMillis());

        String fileName = "*" + date.toString();

        fileName.concat(fileName);

        FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);

        latitudes = new ArrayList<Double>();
        longitudes = new ArrayList<Double>();

        for (int i=0; i < pontos.size(); i++)
        {
            latitudes.add(pontos.get(i).latitude);
            longitudes.add(pontos.get(i).longitude);
        }

        fos.write(latitudes.toString().getBytes());
        fos.write("=".getBytes());
        fos.write(longitudes.toString().getBytes());
        fos.write("=".getBytes());
        fos.write(velocidades.toString().getBytes());
        fos.write("=".getBytes());
        fos.write(Float.toString(distanciaAcumulada).getBytes());

        fos.close();

        startActivity(intent);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
