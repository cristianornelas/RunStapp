package com.example.cristiano.runstapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class IniciarCorrida extends Activity implements LocationListener {

    private static final long MIN_TIME_BW_UPDATES = 3000;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 3;
    private static final int ALTITUDE = 25;
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
            Toast.makeText(getApplicationContext(), "Impossível carregar mapa.", Toast.LENGTH_SHORT).show();
        } else {
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            if (location == null)
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location == null) {
                Toast.makeText(getApplicationContext(), "LastKnowLocation é null.", Toast.LENGTH_LONG).show();
            }
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

        TextView lat = (TextView) findViewById(R.id.latitude);
        TextView lon = (TextView) findViewById(R.id.longitude);
        TextView dis = (TextView) findViewById(R.id.distancia);
        TextView vel = (TextView) findViewById(R.id.velocidade);

        lat.setText(String.valueOf(latitude));
        lon.setText(String.valueOf(longitude));
        dis.setText(String.valueOf(distanciaAcumulada));
        vel.setText(String.valueOf(location.getSpeed()));

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
