package com.dh.project.Face_Recognition.service;

import com.dh.project.Face_Recognition.gui.MyFrame;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Service
public class OpencvServiceImpl implements OpencvService{


    @Value("classpath:opencv-xml/haarcascade_eye.xml")
    private Resource eye;

    @Value("classpath:opencv-xml/haarcascade_mcs_mouth.xml")
    private Resource mouth;

    @Value("classpath:opencv-xml/haarcascade_profileface.xml")
    private Resource face;

    private Mat inputImage;
    private Rect histoRect = new Rect(new Point(0, 0), new Point(100, 100));


    public void test() throws IOException, InterruptedException {
        this.eye = new ClassPathResource("classpath:opencv-xml/haarcascade_eye.xml");
        this.mouth = new ClassPathResource("classpath:opencv-xml/haarcascade_mcs_mouth.xml");
        this.face = new ClassPathResource("classpath:opencv-xml/haarcascade_profileface.xml");
        VideoCapture capture = new VideoCapture();
        capture.open(0);
        MyFrame frame = new MyFrame();
        frame.setVisible(true);
    }

    public MatOfKeyPoint ExtractORBFeatures(Mat inputImage) {
        FeatureDetector features = FeatureDetector.create(FeatureDetector.ORB);
        MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
        features.detect(inputImage, objectKeyPoints);
        KeyPoint[] keypoints = objectKeyPoints.toArray();
//        System.out.println(keypoints);

        MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
//        System.out.println("Computing descriptors...");
        descriptorExtractor.compute(inputImage, objectKeyPoints, objectDescriptors);

        Mat outputImage = new Mat(inputImage.rows(), inputImage.cols(), Imgproc.COLOR_BayerBG2RGB);
        Scalar newKeypointColor = new Scalar(255, 0, 0);

//        System.out.println("Drawing key points on object image...");
        Features2d.drawKeypoints(inputImage, objectKeyPoints, outputImage, newKeypointColor, 0);
//        outputImage = captureHistogram(outputImage);
        return objectDescriptors;
    }

    public LinkedList<DMatch> DetectORBFeature(Mat img1, Mat keypoints1) {
        //        List<ObjectDescriptors> listObjectDescriptors = MyVideoCapture.getObjectList();
        FeatureDetector features = FeatureDetector.create(FeatureDetector.ORB);
        HashMap<String, Integer> map = new HashMap<String, Integer>();
//        for (ObjectDescriptors obj : listObjectDescriptors) {
        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        MatOfKeyPoint sceneKeyPoints = new MatOfKeyPoint();
        MatOfKeyPoint sceneDescriptors = new MatOfKeyPoint();
//        System.out.println("Detecting key points in background image..." + img1);
        features.detect(img1, sceneKeyPoints);
//        System.out.println("Computing descriptors in background image...");
        descriptorExtractor.compute(img1, sceneKeyPoints, sceneDescriptors);

        Mat matchoutput = new Mat(img1.rows() * 2, img1.cols() * 2, Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Scalar matchestColor = new Scalar(0, 255, 0);
        List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
        DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
//        System.out.println("Matching object and scene images..." + keypoints1);
        descriptorMatcher.knnMatch(keypoints1, sceneDescriptors, matches, 2);

//        System.out.println("Calculating good match list...");
        LinkedList<DMatch> goodMatchesList = new LinkedList<DMatch>();

        float nndrRatio = 0.7f;

        for (int i = 0; i < matches.size(); i++) {
            MatOfDMatch matofDMatch = matches.get(i);
            DMatch[] dmatcharray = matofDMatch.toArray();
            DMatch m1 = dmatcharray[0];
            DMatch m2 = dmatcharray[1];

            if (m1.distance <= m2.distance * nndrRatio) {
                goodMatchesList.addLast(m1);

            }
        }

        return goodMatchesList;
    }

    public Mat detectFace(Mat inputimage) throws IOException {

        CascadeClassifier faceDet = new CascadeClassifier();
        CascadeClassifier mouthDet = new CascadeClassifier();
        CascadeClassifier eyeDet = new CascadeClassifier();
        Rect rectCrop = null;

        boolean checkFace = faceDet.load("D:\\RAndDWorkspace\\Spring Boot Application\\Face_Recognition\\src\\main\\resources\\opencv-xml\\haarcascade_frontalface_alt.xml");

        boolean checkMouth = mouthDet.load("D:\\RAndDWorkspace\\Spring Boot Application\\Face_Recognition\\src\\main\\resources\\opencv-xml\\haarcascade_mcs_mouth.xml");

        boolean checkeye = eyeDet.load("D:\\RAndDWorkspace\\Spring Boot Application\\Face_Recognition\\src\\main\\resources\\opencv-xml\\haarcascade_eye.xml");

        Mat greyscale = inputimage.clone();
        Imgproc.cvtColor(inputimage, greyscale, Imgproc.COLOR_BGR2GRAY);
        int absoluteFaceSize = 0;
        if (absoluteFaceSize == 0) {
            int height = greyscale.rows();
            if (Math.round(height * 0.2f) > 0) {
                absoluteFaceSize = Math.round(height * 0.2f);
            }
        }
        MatOfRect faceRect = new MatOfRect();
        MatOfRect mouthRect = new MatOfRect();
        MatOfRect eyeRect = new MatOfRect();
        System.out.println(checkFace + "---------" + checkMouth+"------------"+checkeye);
        if (checkFace) {
            faceDet.detectMultiScale(greyscale, faceRect);
        }
        if (checkMouth) {
            mouthDet.detectMultiScale(greyscale, mouthRect);
        }
		if (faceRect.toArray().length == 1 ) {
            if (checkeye) {
                mouthDet.detectMultiScale(greyscale, eyeRect);
            }
            for (Rect face : faceRect.toArray()) {

                Imgproc.rectangle(greyscale, new Point(face.x, face.y),
                        new Point(face.x + face.width, face.y + face.height), new Scalar(200, 200, 100), 2);
                rectCrop = new Rect(face.x, face.y, face.width, face.height);

            }
        }
		else{
            System.out.println("More than One Face detected!!!");
        }
        if(rectCrop != null){
            Mat image_roi = new Mat(greyscale,rectCrop);
            return image_roi;
        }
        return null;
    }

    public void saveObject(String path, MatOfKeyPoint mat, String fileName) {
        File file = new File(path + File.separator + fileName).getAbsoluteFile();
        if (file.isFile()) {
            try {
                int cols = mat.cols();
                byte[] data = new byte[(int) mat.total() * mat.channels()];
                mat.get(0, 0, data);
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path + File.separator + fileName))) {
                    oos.writeObject(cols);
                    oos.writeObject(data);
                    oos.close();
                }
            } catch (IOException | ClassCastException ex) {
                System.err.println("ERROR: Could not save mat to file: " + path);
            }
        }
    }

    public Mat loadMObject(String path) {
        try {
            int cols;
            byte[] data;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
                cols = (int) ois.readObject();
                data = (byte[]) ois.readObject();
            }

            Mat mat = new Mat(data.length / cols, cols, CvType.CV_8UC1);
            mat.put(0, 0, data);
            return mat;
        } catch (IOException | ClassNotFoundException | ClassCastException ex) {
            System.err.println("ERROR: Could not load mat from file: " + path);
        }
        return null;
    }

}
