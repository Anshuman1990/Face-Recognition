package com.dh.project.Face_Recognition.service;

import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;

import java.io.IOException;
import java.util.LinkedList;

public interface OpencvService {
    public MatOfKeyPoint ExtractORBFeatures(Mat inputImage);
    public LinkedList<DMatch> DetectORBFeature(Mat img1, Mat keypoints1);
    public Mat detectFace(Mat inputimage) throws IOException;
    public void saveObject(String path, MatOfKeyPoint mat, String fileName);
    public Mat loadMObject(String path);
}
