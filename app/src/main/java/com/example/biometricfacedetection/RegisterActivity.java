package com.example.biometricfacedetection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.hardware.Camera;
import android.graphics.drawable.Animatable;
import android.hardware.Camera;
import android.hardware.camera2.params.Face;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import com.example.biometricfacedetection.camera.BestFaceView;
import com.example.biometricfacedetection.camera.CameraSurfaceView;
import com.example.biometricfacedetection.imageutil.FaceRecognizerSingleton;
import com.example.biometricfacedetection.imageutil.StorageHelper;
import com.google.android.gms.vision.CameraSource;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_face;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_objdetect;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.ResourceBundle;

import static android.media.MediaRecorder.VideoSource.CAMERA;
import static com.example.biometricfacedetection.R.id.takePhotoButton;

import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.LINE_8;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvEqualizeHist;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;


@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class RegisterActivity extends AppCompatActivity implements
        CameraSource.PictureCallback, CameraSource.ShutterCallback, View.OnClickListener {

    private Camera camera;
    public int absoluteFaceSize = 0;
    public opencv_objdetect.CascadeClassifier faceDetector;
    boolean isRecognizing = false;
    protected KProgressHUD progressLoader;
    private RelativeLayout layout;
    private BestFaceView baseFaceView;
    private CameraSurfaceView preview;
    private final static String TAG = "";
    private FaceRecognizer faceRecognizer = null;
    private MatVector trainImages = null;
    private Mat trainLabel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_register);
        faceRecognizer = FaceRecognizerSingleton.getInstance(this);

        Button registerbutton = findViewById(R.id.registerButton);
        registerbutton.setOnClickListener(RegisterActivity.this);


    }


    protected Camera getCameraInstance() {

        Camera c = null;
        try {
            c = Camera.open();

        } catch (Exception e) {


        }
        return c;
    }


    private int takeNum = 15;
    private IntBuffer labelsBuf = null;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;

    }


    public void agoRegisterCamera(View view) {
        setContentView(R.layout.activity_register);
        createCameraView();

        if (takeNum > 0) {


            trainImages = new opencv_core.MatVector(takeNum);

            trainLabel = new opencv_core.Mat(takeNum, 1, opencv_core.CV_32SC1);
            labelsBuf = trainLabel.createBuffer();


            counter = 0;


        }


    }


    int counter = 0;
    Toast t = null;

    private boolean processing = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClick(View view) {


        camera.takePicture(null, null, (Camera.PictureCallback) trainImages);
        ((Button) findViewById(R.id.takePhotoButton)).setVisibility(View.VISIBLE);


        IplImage facex = Objects.requireNonNull(baseFaceView).captureFace();
        if (facex != null) {
            // videoPreview.getCamera().takePicture(this, null, this);
            if (takeNum >= 1) {
                //      final Button button1 = findViewById(takePhotoButton);
                //    new Handler().postDelayed(new Runnable() {
                //      @Override
                //    public void run() {
                //      button1.performClick();
                // }
                //}, 50);
                //   videoPreview.getCamera().takePicture(this, null, this);


                takeNum--;
                if (takeNum > 0) {
                    toastFactory("Remaining " + takeNum + " Photo");
                }
                cvEqualizeHist(facex, facex);
                trainImages.put(counter, new Mat(facex));
                labelsBuf.put(counter, 1);
                counter++;
            }
            if (takeNum == 0) {
                this.destoryCamereView();
                setContentView(R.layout.activity_register2);

                Thread timer = new Thread() {
                    public void run() {
                        try {
                            sleep(500);


                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {

                            Intent open = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(open);


                        }
                    }

                };
                timer.start();


                faceRecognizer.train(trainImages, trainLabel);

                // check train data is exist
                File tmp = new File(this.getFilesDir() + FaceRecognizerSingleton.getSaveFileName());
                if (tmp.exists()) {
                    tmp.delete();
                }
                faceRecognizer.save(this.getFilesDir() + FaceRecognizerSingleton.getSaveFileName());
            }
        }
        //}


        setContentView(R.layout.activity_register);
        createCameraView();

        //  faceRecognizer = FaceRecognizerSingleton.getInstance(this);
        //   ((ImageView)findViewById(R.id.images)).setVisibility(View.VISIBLE);
        if (takeNum > 0) {


            trainImages = new opencv_core.MatVector(takeNum);

            trainLabel = new opencv_core.Mat(takeNum, 1, opencv_core.CV_32SC1);
            labelsBuf = trainLabel.createBuffer();


            counter = 0;


        }


    }


    private void createCameraView() {

        baseFaceView = new BestFaceView(this);
        preview = new CameraSurfaceView(this, baseFaceView);



        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                faceDetector = StorageHelper.loadClassifierCascade(RegisterActivity.this, R.raw.frontalface);
                return null;
            }
        }.execute();

        layout.addView(preview);
        layout.addView(baseFaceView);

        Button button1 = findViewById(takePhotoButton);


        button1.setOnClickListener((View.OnClickListener) RegisterActivity.this);


    }

    private void destoryCamereView() {
        layout.removeView(preview);
        layout.removeView(baseFaceView);
        ((Button) findViewById(R.id.takePhotoButton)).setVisibility(View.GONE);
        //  ((Button) findViewById(R.id.startbutton)).setVisibility(View.VISIBLE);
    }


    public void returnOnclick(View view) {
        finish();
    }

    private void toastFactory(String str) {
        if (t != null) t.cancel();
        t = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
    }


    @Override
    public void onPictureTaken(byte[] bytes) {

    }

    @Override
    public void onShutter() {

    }


}