package com.example.eyeboy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


public class tacker extends AppCompatActivity {


    public int side, aim, eyeclosed, enemynumber = 0;
    public String position;
    float coordy;
    public int blinks, blink, hp = 3;
    ImageView thirdpos, zeropos, firstpos, secpos;
    ImageView hp1, hp2, hp3;
    public String nhp = "hp";

    private static final String TAG = "tracker";
    //For looking logs
    ArrayAdapter adapter;
    ArrayList<String> list = new ArrayList<>();

    CameraSource cameraSource;
    public Handler handler;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {





        super.onCreate(savedInstanceState);

        setContentView(R.layout.tacker);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);





        Button exitbtn = (Button)findViewById(R.id.exitbtn);
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    Intent intent = new Intent(tacker.this, FullscreenActivity.class);
                    startActivity(intent);finish();
                } catch (Exception e){

                }
            }
        });




        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            Toast.makeText(this, "Grant Permission and restart app", Toast.LENGTH_SHORT).show();
        }
        else {


            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
            createCameraSource();
        }

        //visible layouts
        thirdpos = (ImageView) findViewById(R.id.thirdpos);
        zeropos = (ImageView) findViewById(R.id.zeropos);
        firstpos = (ImageView) findViewById(R.id.firstpos);
        secpos = (ImageView) findViewById(R.id.secpos);
        hp3 = (ImageView) findViewById(R.id.hp3);
        hp2 = (ImageView) findViewById(R.id.hp2);
        hp1 = (ImageView) findViewById(R.id.hp1);



    }




    public void setenemy() {
        new Thread(new Runnable() {
            public void run() {
                thirdpos.post(new Runnable() {
                    public void run() {
                        if (enemynumber == 0) {
                            Random r = new Random();
                            int i1 = r.nextInt(4);
                            Log.i(TAG, "number" + i1);
                            aim = i1;
                            if (i1 == 3) { //left enemy
                                thirdpos.setVisibility(VISIBLE);
                                enemynumber = 1;
                            }
                            if (i1 == 0) { //right enemy
                                zeropos.setVisibility(VISIBLE);
                                enemynumber = 1;
                            }

                            if (i1 == 1) { //middle left
                                firstpos.setVisibility(VISIBLE);
                                enemynumber = 1;
                            }

                            if (i1 == 2) { //middle right
                                secpos.setVisibility(VISIBLE);
                                enemynumber = 1;
                            }

                            if(hp == 2) {
                                hp3.setVisibility(INVISIBLE);
                            }
                            if(hp == 1){
                                hp2.setVisibility(INVISIBLE);
                            }
                            if (hp == 0){
                                hp1.setVisibility(INVISIBLE);
                                setContentView(R.layout.activity_fullscreen);
                            }
                        }
                    }
                });
            }
        }).start();
    }
    //This class will use google vision api to detect eyes
    private class EyesTracker extends Tracker<Face> {

        private final float THRESHOLD = 0.75f;

        private EyesTracker() {

        }

        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {


            if (enemynumber == 0) {
                setenemy();
            } else {
                Log.i(TAG, "have enemy");
            }
            if (face.getIsLeftEyeOpenProbability() > THRESHOLD || face.getIsRightEyeOpenProbability() > THRESHOLD) {

                blinks = 0;
                coordy = face.getEulerY();
                if(coordy > 21){
                    side = 3; //left
                } else if(21 > coordy && coordy > 14){
                    side = 2; //middle left
                } else if(14 > coordy && coordy > 7){
                    side = 1; //middle right
                } else if(7 > coordy){
                    side = 0; //right
                }
            }
            if ((face.getIsLeftEyeOpenProbability() > THRESHOLD || face.getIsRightEyeOpenProbability() > THRESHOLD) && eyeclosed == 1) {
             //choosing sides of shooting

                if (side == 3 && blink == 1) { //left shot
                    if(aim == side){
                        thirdpos.setVisibility(INVISIBLE);
                        enemynumber = 0;
                    }
                    blink = 0;
                    eyeclosed = 0;
                }



                if (side == 0 && blink == 1){  //right shot
                    if(aim == side){
                        zeropos.setVisibility(INVISIBLE);
                        enemynumber = 0;
                    }
                    blink = 0;
                    eyeclosed = 0;
                }


                if (side == 1 && blink == 1){  //right middle shot
                    if(aim == side){
                        firstpos.setVisibility(INVISIBLE);
                        enemynumber = 0;
                    }
                    blink = 0;
                    eyeclosed = 0;
                }



                if (side == 2 && blink == 1){  //left middle shot
                    if(aim == side){
                        secpos.setVisibility(INVISIBLE);
                        enemynumber = 0;
                    }
                    blink = 0;
                    eyeclosed = 0;
                }


                if(enemynumber == 1 && hp != 0){
                    hp -= 1;
                    firstpos.setVisibility(INVISIBLE);
                    secpos.setVisibility(INVISIBLE);
                    thirdpos.setVisibility(INVISIBLE);
                    zeropos.setVisibility(INVISIBLE);
                    enemynumber = 0;
                } else{

                }
            }
            if (!(face.getIsLeftEyeOpenProbability() > THRESHOLD || face.getIsRightEyeOpenProbability() > THRESHOLD) && eyeclosed == 0) {
                blinks = blinks + 1;  //separating blinks from closing eyes
                if (blinks > 8) {
                    blink += 1;
                    blinks = 0;
                    eyeclosed = 1;

                }

            }


        }



        @Override
        public void onDone() {
            super.onDone();
        }
    }




    private class FaceTrackerFactory implements MultiProcessor.Factory<Face> {

        @Override
        public Tracker<Face> create(Face face) {
            return new EyesTracker();
        }
    }

    public void createCameraSource() {
        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .setClassificationType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        detector.setProcessor(new MultiProcessor.Builder(new FaceTrackerFactory()).build());

        cameraSource = new CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(1024, 768)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraSource.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


}


