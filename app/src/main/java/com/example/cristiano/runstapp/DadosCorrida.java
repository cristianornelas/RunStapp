package com.example.cristiano.runstapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.camera2.params.Face;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DadosCorrida extends AppCompatActivity {

    private Polyline linha;
    private GoogleMap googleMap;
    private ArrayList<LatLng> pontos;
    private ArrayList<Float> velocidades;
    private static final long MIN_TIME_BW_UPDATES = 3000;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 3;
    private static final int ALTITUDE = 18;
    float [] resultados;
    ShareButton shareButton;
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_corrida);

        Bundle extras = getIntent().getExtras();
        if (googleMap == null)
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_dados)).getMap();

        pontos = new ArrayList<LatLng>();
        velocidades = new ArrayList<Float>();


        String[] aux;
        String[] tmpLat;
        String[] tmpLog;
        String[] tmpVel;


        String content = extras.getString("INFO");

        if (content != null) {
            aux = content.split("=");


            aux[0] = aux[0].replaceAll("\\[", " ");
            aux[0] = aux[0].replaceAll("]", " ");
            tmpLat = aux[0].split(",");


            aux[1] = aux[1].replaceAll("\\[", "");
            aux[1] = aux[1].replaceAll("]", "");
            tmpLog = aux[1].split(",");

            aux[2] = aux[2].replaceAll("\\[", "");
            aux[2] = aux[2].replaceAll("]", "");
            tmpVel = aux[2].split(",");

            int i;
            for (i = 0; i < tmpLat.length; i++) {
                LatLng latLng = new LatLng(Double.parseDouble(tmpLat[i]), Double.parseDouble(tmpLog[i]));
                pontos.add(latLng);
                velocidades.add(Float.parseFloat(tmpVel[i]));

            }


            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(tmpLat[--i]), Double.parseDouble(tmpLog[--i]))).zoom(ALTITUDE).build();

            googleMap.setMapType(googleMap.MAP_TYPE_NORMAL);
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            //googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            redesenhaLinha();


            TextView dis = (TextView) findViewById(R.id.distancia_dados);
            dis.setText(aux[3] + " Metros");

            List<String> permissoes = Arrays.asList("publish_actions");
            loginManager = LoginManager.getInstance();
            loginManager.logInWithPublishPermissions(this, permissoes);

            //Cria um botao compartilhar
            Button compartilhar = (Button) findViewById(R.id.facebookShare);
            compartilhar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View rootView = findViewById(R.id.map_dados).getRootView();
                    rootView.setDrawingCacheEnabled(true);
                    bitmap = rootView.getDrawingCache();

                    FacebookSdk.sdkInitialize(getApplicationContext());
                    callbackManager = CallbackManager.Factory.create();


                    loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            compartilhaFoto();
                        }

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onError(FacebookException error) {

                        }
                    });

                }
            });



        }

    }

    private void compartilhaFoto()
    {
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .setCaption("BLA BLA BLA")
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, null);
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
}
