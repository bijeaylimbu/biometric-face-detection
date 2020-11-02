package com.example.biometricfacedetection.imageutil;

import android.content.Context;
import android.util.Log;

import com.example.biometricfacedetection.R;

import org.bytedeco.javacpp.opencv_face.FaceRecognizer;


import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;


public class FaceRecognizerSingleton {
    private static FaceRecognizer instance = null;

    private FaceRecognizerSingleton(){}

    public static FaceRecognizer getInstance(Context context){
        if (instance == null){
            synchronized (FaceRecognizerSingleton.class){
                if (instance == null){
                    if(context == null )
                        instance = createLBPHFaceRecognizer(1,8,8,8,85);
                    else{
                        int threshold = context.getSharedPreferences("threshold_data",0).
                                getInt("thrd",context.getResources().getInteger(R.integer.def_thrd));
                        instance = createLBPHFaceRecognizer(1,8,8,8,threshold);
                    }

                    Log.d("", "getInstance: createLBPHFaceRecognizer");
                }
            }
        }
        return instance;
    }
    public static String getSaveFileName(){
        return "/LBPTrainData.xml";
    }

    public static void updateThreshold(){
        if (instance != null){
            instance = null;
        }
    }

}
