package com.example.eyeboy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.widget.AppCompatEditText;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;


import java.io.IOException;
import java.util.ArrayList;


public class tacker extends AppCompatActivity {
    public int side;
    public int eyeclosed;
    public int blinks, blink, coord;

    private static final String TAG = "tracker";


    //For looking logs
    ArrayAdapter adapter;
    ArrayList<String> list = new ArrayList<>();

    CameraSource cameraSource;

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
                //exit btn, back to menu

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
    }


    //This class will use google vision api to detect eyes
    private class EyesTracker extends Tracker<Face> {

        private final float THRESHOLD = 0.75f;

        private EyesTracker() {

        }

        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {
            if (face.getIsLeftEyeOpenProbability() > THRESHOLD || face.getIsRightEyeOpenProbability() > THRESHOLD) {
                //Log.i(TAG, "onUpdate: Eyes Detected");
                blinks = 0;
                coord = (int) face.getEulerY();
                if (coord > 14) {
                    side = 1;
                } else{
                    side = 0;
                }
            }
            if ((face.getIsLeftEyeOpenProbability() > THRESHOLD || face.getIsRightEyeOpenProbability() > THRESHOLD) && eyeclosed == 1) {

                //Log.i(TAG, "eyeclosed:"+ String.valueOf(eyeclosed));
                //Log.i(TAG, "side:"+ String.valueOf(side));
                //Log.i(TAG, "blinks:"+ String.valueOf(blinks));
                if (side == 1 && blink == 1) {
                    Log.i(TAG, " Left shot! ");
                    blink = 0;
                    eyeclosed = 0;

                }
                if (side == 0 && blink == 1){
                    Log.i(TAG, " Right Shot! ");
                    blink = 0;
                    eyeclosed = 0;
                }
            }
            if (!(face.getIsLeftEyeOpenProbability() > THRESHOLD || face.getIsRightEyeOpenProbability() > THRESHOLD) && eyeclosed == 0) {
                //Log.i(TAG, "eyeclosed:"+ String.valueOf(eyeclosed));
                blinks = blinks + 1;
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
                .setRequestedFps(60.0f)
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


