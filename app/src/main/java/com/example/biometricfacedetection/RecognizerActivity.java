package com.example.biometricfacedetection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.biometricfacedetection.camera.CameraSurfaceView;
import com.example.biometricfacedetection.camera.FaceRecogntionView;
import com.example.biometricfacedetection.imageutil.FaceRecognition;
import com.example.biometricfacedetection.imageutil.FaceRecognizerSingleton;
import com.example.biometricfacedetection.service.LockScreenService;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.bytedeco.javacpp.opencv_imgproc;


import java.io.File;

public class RecognizerActivity extends Activity implements FaceRecognition {

    private FaceRecognizer faceRecognizer =null;
    private FaceRecogntionView baseFaceView;
    private CameraSurfaceView preview;
    private boolean flag = true;
    private Handler mhandler;
    private  final  static String TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognizer);


        if( new File(getFilesDir()+ FaceRecognizerSingleton.getSaveFileName()).exists()){
            @SuppressLint("WrongViewCast")
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_recongizer);
            baseFaceView = new FaceRecogntionView(this);
            baseFaceView.setActivity(this);
            preview = new CameraSurfaceView(this,baseFaceView);
            layout.addView(preview);
            layout.addView(baseFaceView);
            faceRecognizer = FaceRecognizerSingleton.getInstance(this);
            faceRecognizer.load(getFilesDir()+FaceRecognizerSingleton.getSaveFileName());
            mhandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                  //  startService(new Intent(RecognizerActivity.this, LockScreenService.class));
                    baseFaceView.setVisibility(View.INVISIBLE);

                    onBackPressed();
                    finish();
                }
            };
        }



    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: recognition");
        flag = true;


         baseFaceView.setVisibility(View.VISIBLE);






        super.onStart();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: recongition");

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        startService(new Intent(RecognizerActivity.this,LockScreenService.class));
//                    }
//                });
//            }
//        }).start();
        moveTaskToBack(true);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        //finish();
    //    startService(new Intent(RecognizerActivity.this,LockScreenService.class));
        //android.os.Process.killProcess(android.os.Process.myPid());
    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: recognition");
        preview.startPreview();
        super.onResume();
    }

    @Override
    protected void onPause(){
        Log.d(TAG, "onPause: recognition");
        preview.stopPreview();
        flag =false;
        super.onPause();

    }

    private Toast toast = null;
    private int count = 0;

    @Override
    public void execute(IplImage face) {
        if (flag) {
            opencv_imgproc.cvEqualizeHist(face,face);
            int predict = faceRecognizer.predict(new Mat(face));
            Log.d(TAG, "faceRecognizer: predict:" + predict);
            if (predict == 1) {
                flag = false;
                if (toast != null) {
                    toast.cancel();
                }
                if (count != 0) {


                    Toast.makeText(RecognizerActivity.this, "Login Success!! Try: " + count, Toast.LENGTH_SHORT).show();
                    count = 0;
                    Thread timer=new Thread(){
                        public void run(){
                            try{
                                sleep(100);

                            }
                            catch  (InterruptedException e) {
                                e.printStackTrace();
                            }
                            finally {
                                Intent open=new Intent(RecognizerActivity.this, success.class);
                                startActivity(open);



                            }
                        }

                    };
                    timer.start();

                } else {
                    Toast.makeText(RecognizerActivity.this, "Login Success!!", Toast.LENGTH_SHORT).show();

                    Thread timer=new Thread(){
                        public void run(){
                            try{
                                sleep(100);

                            }
                            catch  (InterruptedException e) {
                                e.printStackTrace();
                            }
                            finally {
                                Intent open=new Intent(RecognizerActivity.this, success.class);
                                startActivity(open);
                                //startService(new Intent(RecognizerActivity.this, LockScreenService.class));



                            }
                        }

                    };
                    timer.start();
                }
                mhandler.sendMessage(Message.obtain());

            } else {
                baseFaceView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (toast != null) toast.cancel();
                      //  toast = Toast.makeText(RecognizerActivity.this, "Login Fail!! " + count++, Toast.LENGTH_SHORT);
                        toast = Toast.makeText(RecognizerActivity.this, "NO FACE FOUND", Toast.LENGTH_SHORT);
                        toast.show();


                    }
                });
            }
        }
    }
}
