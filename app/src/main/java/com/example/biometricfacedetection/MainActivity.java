package com.example.biometricfacedetection;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;


import com.example.biometricfacedetection.camera.BestFaceView;
import com.example.biometricfacedetection.camera.CameraSurfaceView;
import com.example.biometricfacedetection.imageutil.FaceRecognizerSingleton;
import com.example.biometricfacedetection.service.LockScreenService;
import com.example.biometricfacedetection.service.RequestUserPermission;

import com.google.android.gms.vision.CameraSource;
import com.google.android.material.circularreveal.CircularRevealLinearLayout;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_face;
import org.bytedeco.javacpp.opencv_objdetect;

import java.io.File;
import java.nio.IntBuffer;
import java.util.Objects;


import static android.widget.Toast.*;

import static androidx.annotation.InspectableProperty.ValueType.COLOR;

import static com.example.biometricfacedetection.R.id.takePhotoButton;
import static com.example.biometricfacedetection.R.id.textview1;
import static com.example.biometricfacedetection.imageutil.FaceRecognizerSingleton.*;
import static com.github.johnpersano.supertoasts.library.Style.DURATION_LONG;
import static org.bytedeco.javacpp.opencv_imgproc.cvEqualizeHist;


public class MainActivity extends AppCompatActivity implements
        NumberPicker.OnValueChangeListener, CameraSource.PictureCallback, CameraSource.ShutterCallback, View.OnClickListener {

    boolean iscolor = true;
    private Button serviceBtn = null;
    private boolean isServiceRunFlag = false;

    public opencv_objdetect.CascadeClassifier faceDetector;
    private BestFaceView baseFaceView;
    private CameraSurfaceView preview;

    private RelativeLayout layout;


    private opencv_face.FaceRecognizer faceRecognizer = null;
    Toast t = null;
    private int takeNum = 15;
    private IntBuffer labelsBuf = null;
    private opencv_core.MatVector trainImages = null;
    private opencv_core.Mat trainLabel = null;
    int counter = 0;
    private SharedPreferences settings;
    private View view;
    SharedPreferences sp;

    private SurfaceView cameraPreview;
    private RelativeLayout overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);












        sp = getSharedPreferences("login",MODE_PRIVATE);

        if(sp.getBoolean("logged",false)){
            goToMainActivity();
        }











        Log.d("", "onCreate: Main");
        faceRecognizer = getInstance(this);
        RequestUserPermission requestUserPermission = new RequestUserPermission(this);
        requestUserPermission.verifyCameraPermissions();

        ImageView imageView = findViewById(R.id.imageview);
        final TextView textView = findViewById(textview1);
        final Button button2 = findViewById(R.id.registerButton);
        final Button button1 = findViewById(takePhotoButton);


        RelativeLayout relativeLayout=(RelativeLayout) findViewById(R.id.relativelayout);

imageView.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.GONE);
        button2.setOnClickListener(MainActivity.this);
        button1.setOnClickListener(MainActivity.this);

    }


    private void destoryCamereView() {
        CardView layout = (CardView) findViewById(R.id.relative);
        layout.removeView(preview);
        layout.removeView(baseFaceView);



    }












    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    protected void onDestroy() {
        super.onDestroy();
        Log.d("", "onDestroy: Main");

    }

    boolean doublebackpress = false;

    @SuppressLint("ResourceType")
    @Override
    public void onBackPressed() {

        super.onBackPressed();
        ActivityCompat.finishAffinity(MainActivity.this);


        super.onBackPressed();
        finishAffinity();


    }



    @SuppressLint({"WrongViewCast", "ResourceAsColor"})
    private void createCameraView() {







        RelativeLayout relativeLayout=(RelativeLayout) findViewById(R.id.relativelayout);
        relativeLayout.setVisibility(View.VISIBLE);




        CardView layout = (CardView) findViewById(R.id.relative);
        baseFaceView = new BestFaceView(this);
        preview = new CameraSurfaceView(this, baseFaceView);



        layout.addView(preview);
        layout.addView(baseFaceView);



    }



    @Override
    public void onPictureTaken(byte[] bytes) {

    }

    @Override
    public void onShutter() {

    }


    public void goToMainActivity(){
        Intent i = new Intent(this,MainActivity1.class);
        startActivity(i);
    }



    private void toastFactory(String str) {
        if (t != null) t.cancel();
        t = makeText(this, str, LENGTH_SHORT);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
    }

    final CameraSource.PictureCallback mPicture = new CameraSource.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes) {

        }
    };


    // @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint({"WrongConstant", "ResourceType"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onClick(View view) {

        //     ((Button) findViewById(R.id.takePhotoButton)).setVisibility(View.VISIBLE);

        switch (view.getId()) {

            case takePhotoButton:
                opencv_core.IplImage facex = Objects.requireNonNull(baseFaceView).captureFace();
                if (facex != null) {
                    // videoPreview.getCamera().takePicture(this, null, this);
                    if (takeNum >= 1) {
                        final Button button1 = findViewById(R.id.takePhotoButton);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                button1.performClick();


                            }
                        }, 100);




                        takeNum--;

                        if (takeNum > 0) {
                            if (takeNum > 0 && takeNum < 5) {
                                toastFactory("1");


                                ImageView imageView = findViewById(R.id.imageview);

                                imageView.setVisibility(View.VISIBLE);

                                imageView.setAlpha(190);
                                ((Animatable) imageView.getDrawable()).start();





                            } else if (takeNum >= 5 && takeNum < 10) {
                                toastFactory("2");
                            } else if (takeNum <= 15 && takeNum >= 10) {
                                toastFactory("3");
                            }


                            // toastFactory("Remaining " + takeNum+ " Photo");

                        }

                        cvEqualizeHist(facex, facex);
                        trainImages.put(counter, new opencv_core.Mat(facex));
                        labelsBuf.put(counter, 1);
                        counter++;

                    }
                    if (takeNum == 0) {
                        sp.edit().putBoolean("logged",true).apply();
                       this.destoryCamereView();









                        Thread timer = new Thread() {
                            public void run() {
                                try {
                                    sleep(500);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } finally {
                                    Intent open = new Intent(MainActivity.this, MainActivity1.class);
                                    startActivity(open);



                                }
                            }

                        };
                        timer.start();

                        faceRecognizer.train(trainImages, trainLabel);

                        // check train data is exist
                        File tmp = new File(this.getFilesDir() + getSaveFileName());
                        if (tmp.exists()) {
                            tmp.delete();
                        }
                        faceRecognizer.save(this.getFilesDir() + getSaveFileName());
                    }
                } else if (facex == null) {
                    toastFactory("Look at the  Camera and click on capture my photo");
                }
                break;
            case R.id.registerButton:
                final ImageView imageView = findViewById(R.id.Imageview2);
                final TextView textView = findViewById(textview1);
                final Button button2 = findViewById(R.id.registerButton);
                final Button button1 = findViewById(takePhotoButton);

                CardView cardView=findViewById(R.id.cardview1);
                cardView.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                button2.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                button1.setVisibility(View.VISIBLE);
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.activitymain);
               //layout.setBackgroundColor(Color.BLACK);

                layout.setBackgroundResource(R.drawable.background);
              // setContentView(R.layout.activity_register);

                createCameraView();

                if (takeNum > 0) {


                    trainImages = new opencv_core.MatVector(takeNum);

                    trainLabel = new opencv_core.Mat(takeNum, 1, opencv_core.CV_32SC1);
                    labelsBuf = trainLabel.createBuffer();


                    counter = 0;

                    break;


                }




        }
    }


    Camera.FaceDetectionListener faceDetectionListener = new Camera.FaceDetectionListener() {
        private boolean processing = false;

        public void setProcessing(boolean processing) {
            this.processing = processing;
        }

        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            if (processing) return;
            if (faces.length == 0) {
                makeText(getApplicationContext(), "no face detected", LENGTH_LONG).show();


            } else {
                try {
                    Camera.PictureCallback shutter = null;
                    Camera.PictureCallback raw = null;
                    Camera.PictureCallback jpeg = null;
                    camera.takePicture((Camera.ShutterCallback) shutter, raw, jpeg);
                } catch (Exception e) {

                }
            }


        }
    };





}








