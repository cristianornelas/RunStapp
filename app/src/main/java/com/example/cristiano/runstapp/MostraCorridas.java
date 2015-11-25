package com.example.cristiano.runstapp;

import android.app.ActionBar;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class MostraCorridas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostra_corridas);

        File[] files = getFilesDir().listFiles();

        LinearLayout ll = (LinearLayout) findViewById(R.id.listaCorrida);
        int j=0;

        for (int i=0; i<files.length; i++) {
            if(files[i].getName().startsWith("*")){
                Button btn = new Button(this);
                btn.setText(files[i].getName());
                btn.setId(j);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //DESENHA O MAPA AQUI E INFORMACOES DA CORRIDA
                    }
                });
                j++;
                ll.addView(btn);
            }
        }
    }
}
