package com.example.cristiano.runstapp;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class MostraCorridas extends AppCompatActivity {

    File[] files;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostra_corridas);

        String name;
        files = getFilesDir().listFiles();

        LinearLayout ll = (LinearLayout) findViewById(R.id.listaCorrida);
        Button [] btn = new Button[files.length];
        int j=0;

        for (i=0; i<files.length; i++) {
            if(files[i].getName().startsWith("*")){

                btn[j] = new Button(this.getApplicationContext());

                name  = files[i].getName();

                btn[j].setText(files[i].getName());
                btn[j].setId(j);
                btn[j].setOnClickListener(handleOnClick(btn[j]));

                ll.addView(btn[j]);

                j++;


            }
        }
    }

    View.OnClickListener handleOnClick(final Button button) {
        return new View.OnClickListener() {
            public void onClick(View v) {

                String line = "";


                //Toast.makeText(getApplicationContext(), button.getText().toString(), Toast.LENGTH_LONG).show();
                try {
                    FileInputStream fis = openFileInput(button.getText().toString());
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();


                    while ((line = br.readLine()) != null) {


                        sb.append(line);

                    }

                    //DESENHA O MAPA AQUI E INFORMACOES DA CORRIDA
                    Intent intent = new Intent(getApplicationContext(), DadosCorrida.class);
                    intent.putExtra("INFO", sb.toString());

                    startActivity(intent);

                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        };
    }

}
