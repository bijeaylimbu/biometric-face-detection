package com.example.biometricfacedetection.camera;

import android.content.Context;
import android.util.AttributeSet;

import com.example.biometricfacedetection.imageutil.FaceRecognition;

import org.bytedeco.javacpp.opencv_core.IplImage;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class FaceRecogntionView extends BestFaceView {

    private FaceRecognition activity=null ;
    private Lock lock = new ReentrantLock();

    public FaceRecogntionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FaceRecogntionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FaceRecogntionView(Context context) {
        super(context);
    }

    @Override
    public IplImage processImage(byte[] data, int width, int height) {
        IplImage face = super.processImage(data, width, height);
        if( face != null && activity != null ){
            if( !lock.tryLock())
                return face;
            try {
                activity.execute(face);
            }
            finally {
                lock.unlock();
            }

        }
        return face;

    }

    public void setActivity(FaceRecognition activity){
        this.activity = activity;
    }
}
