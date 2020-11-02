package com.example.biometricfacedetection.imageutil;

import org.bytedeco.javacpp.opencv_core.IplImage;


public interface FaceRecognition {
    public void execute(IplImage face);
}
