package com.example.biometricfacedetection.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.example.biometricfacedetection.imageutil.IpUtil;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;
import org.bytedeco.javacpp.presets.opencv_objdetect;


import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_ROUGH_SEARCH;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_FIND_BIGGEST_OBJECT;


@SuppressWarnings("deprecation")
public class BestFaceView extends View implements Camera.PreviewCallback{
private Bitmap mbitmap;
    private CvMemStorage storage;
    private CvHaarClassifierCascade classifier=null;

    public static final int SUBSAMPLING_FACTOR = 4;
    private CvSeq faces;
    private IplImage grayImage = null;


    public BestFaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        try {
            init(context);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("", "not found classifier file");
        }
    }
    public BestFaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        try {
            init(context);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("", "not found classifier file");
        }
    }


    public BestFaceView(Context context) {
        super(context);
        try {
            init(context);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("", "not found classifier file");
        }
    }

    private void init(Context context) throws IOException{

        File classifierFile = Loader.extractResource(getClass(),

                "/org/haarcascade_frontalface_alt.xml",
                context.getCacheDir(), "classifier", ".xml");



        if (classifierFile == null || classifierFile.length() <= 0) {
            throw new IOException("Could not extract the classifier file from Java resource.");
        }


        Loader.load(opencv_objdetect.class);
        classifier = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
        classifierFile.delete();
        if (classifier.isNull()) {
            throw new IOException("Could not load the classifier file.");
        }
        storage = CvMemStorage.create();


    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        try {
            Camera.Size size = camera.getParameters().getPreviewSize();
            processImage(data, size.width, size.height);
            camera.addCallbackBuffer(data);
        } catch (RuntimeException e) {
            // The camera has probably just been released, ignore.
        }


    }
    public IplImage processImage(byte[] data, int width, int height) {
        // First, downsample our image and convert it into a grayscale IplImage
        int f = SUBSAMPLING_FACTOR;
        IplImage transposed = null;
        if (grayImage == null || grayImage.width() != width/f || grayImage.height() != height/f) {
            grayImage = IplImage.create(width/f, height/f, IPL_DEPTH_8U, 1);
            transposed = IplImage.create(height/f, width/f, IPL_DEPTH_8U, 1);
        }
        int imageWidth  = grayImage.width();
        int imageHeight = grayImage.height();
        int dataStride = f*width;
        int imageStride = grayImage.widthStep();
        ByteBuffer imageBuffer = grayImage.createBuffer();
        for (int y = 0; y < imageHeight; y++) {
            int dataLine = y*dataStride;
            int imageLine = y*imageStride;
            for (int x = 0; x < imageWidth; x++) {
                imageBuffer.put(imageLine + x, data[dataLine + f*x]);
            }
        }
//

        cvTranspose(grayImage, transposed);
        cvFlip(transposed, transposed, 0);
        grayImage = transposed;

        cvClearMemStorage(storage);
        faces = cvHaarDetectObjects(grayImage, classifier, storage, 1.1, 3,
                CV_HAAR_FIND_BIGGEST_OBJECT | CV_HAAR_DO_ROUGH_SEARCH);
        postInvalidate();
        return captureFace();


    }

    public void drawBitmap(Canvas canvas){
        double viewWidth=canvas.getWidth();
        double viewHeight=canvas.getHeight();
        double imageWidth=mbitmap.getWidth();
        double imageHeight=mbitmap.getHeight();
        double scale=Math.min(viewWidth/imageHeight,viewHeight/imageHeight);

    }




    protected void onDraw(Canvas canvas) {
        @SuppressLint("DrawAllocation") Paint paint = new Paint();
        Paint paint1=new Paint();
        //paint.setColor(Color.BLACK);
//paint.setTextSize(30);
  //      paint.setStyle(Paint.Style.FILL);

    //    paint.setStrokeWidth(15);




// String s= "PUT YOUR FACE ON THE CIRCLE";
 //float textwidth=paint.measureText(s);
 //canvas.drawText(s,(getWidth()-textwidth)/3,55,paint);




        if (faces != null) {
            int orange = Color.rgb(255, 165, 0);
            paint.setColor(orange);
            paint.setStrokeWidth(6);
            paint.setStyle(Paint.Style.STROKE);
            float scaleX = (float)getWidth()/grayImage.width();
            float scaleY = (float)getHeight()/grayImage.height();
            int total = faces.total();
//            Log.d("", "Find Face " + total);
            for (int i = 0; i < total; i++) {
                paint1.setColor(Color.BLACK);
                paint1.setTextSize(20);
                paint1.setStyle(Paint.Style.FILL);
                paint1.setTextAlign(Paint.Align.CENTER);

               // paint1.setStrokeWidth(15);




                String s= "PUT YOUR FACE ON THE CIRCLE";
                float z=paint.measureText(s)/2;
                float textsize=paint1.getTextSize();
                float textwidth=paint1.measureText(s);


                @SuppressLint("DrawAllocation") CvRect r = new CvRect(cvGetSeqElem(faces, i));
               int x = r.x(), y = r.y(), w = r.width(), h = r.height();
           //     canvas.drawText(s,150,470,paint1);

              //  RectF oval=new RectF((x*scaleX)+1, (y*scaleY)+3, ((x+w)*scaleX)+1, ((y+h)*scaleY)+3) ;
                //canvas.drawOval(oval,paint);
                //canvas.drawText(s,(x*scaleX)+165,(y*scaleY)+163,paint1);



             //  canvas.drawRect(x*scaleX, y*scaleY, (x+w)*scaleX, (y+h)*scaleY, paint);
             //   canvas.drawCircle(w*scaleX,h*scaleY,((w*scaleX)+(h*scaleY))/4,paint);

          //         canvas.drawCircle(xa, xb, 10, paint);

            }
//            faces = null;
        }
    }



    public IplImage captureFace(){
        if( faces == null || grayImage == null){return null; }
        if( faces.total() == 1) {
            return IpUtil.cropFace(grayImage, new CvRect(cvGetSeqElem(faces, 0)));
        }
        else
            return null;
    }



}
