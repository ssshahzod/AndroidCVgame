package com.example.eyeboy;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

public class Gameover extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameover);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        final MediaPlayer ring = MediaPlayer.create(Gameover.this, R.raw.gamuover);
        ring.start();

        Button buttonrestart = (Button)findViewById(R.id.restartbtn);
        buttonrestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                try{
                    Intent intent = new Intent(Gameover.this, tacker.class);
                    startActivity(intent);finish();
                    ring.stop();
                } catch (Exception e) {

                }
            }

        });


        Button buttoninmenu = (Button)findViewById(R.id.inmenubtn);
        buttoninmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                try{
                    Intent intent = new Intent(Gameover.this, FullscreenActivity.class);
                    startActivity(intent);finish();
                    ring.stop();
                } catch (Exception e) {

                }
            }

        });
    }
}
