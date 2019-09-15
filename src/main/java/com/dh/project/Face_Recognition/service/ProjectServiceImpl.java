package com.dh.project.Face_Recognition.service;

import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private OpencvService opencvService;

    @Override
    public void loadData() throws IOException {
        File[] files = new File("D:\\Dataset\\Face Recognition\\training data").listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                File[] fileArray = file.listFiles();
                cleanUp(fileArray);
                for (File file1 : fileArray) {
                    if (file1.isFile()) {
                        String fileName = file1.getName();
                        fileName = fileName.substring(0, fileName.lastIndexOf("."));
                        String featurePath = file.getAbsolutePath() + File.separator + fileName;
                        new File(featurePath).createNewFile();
                    }
                }
            }
        }
    }

    private void cleanUp(File[] fileArray) {
        for (File file : fileArray) {
            String fileName = file.getName();
            if (!fileName.contains(".")) {
                FileSystemUtils.deleteRecursively(file);
            }
        }
    }


    @Override
    public void trainData() throws IOException {
        File[] files = new File("D:\\Dataset\\Face Recognition\\training data").listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                File[] imgFiles = file.listFiles();
                String imgPath = "";
                for (File imgFile : imgFiles) {
                    String name = imgFile.getName();
                    if (name.contains(".")) {
                        imgPath = imgFile.getAbsolutePath();
                        String featureName = imgFile.getName();
                        featureName = featureName.substring(0, featureName.lastIndexOf("."));
                        Mat input = Imgcodecs.imread(imgPath);
                        Mat greyImage = new Mat();
                        Imgproc.cvtColor(input, greyImage, Imgproc.COLOR_RGB2GRAY);
                        MatOfKeyPoint keypoint_ORB = this.opencvService.ExtractORBFeatures(greyImage);
                        this.opencvService.saveObject(file.getAbsolutePath(), keypoint_ORB, featureName);
                    }
                }
            }
        }
    }


    @Override
    public List<Map> testData() {
        List<Map> responseArr = new ArrayList<>();
        File[] testingFiles = new File("D:\\Dataset\\Face Recognition\\testing data").listFiles();
        File[] trainingFiles = new File("D:\\Dataset\\Face Recognition\\training data").listFiles();
        for (File file : testingFiles) {
            if (file.isDirectory()) {
                File[] imgFiles = file.listFiles();
                for (File imgFile : imgFiles) {
                    String imgName = imgFile.getName();

                    Mat input = Imgcodecs.imread(imgFile.getAbsolutePath());
                    Mat greyImage = new Mat();
                    Imgproc.cvtColor(input, greyImage, Imgproc.COLOR_RGB2GRAY);

                    for (File trainFile : trainingFiles) {
                        if (trainFile.isDirectory()) {
                            File[] featureFiles = trainFile.listFiles();
                            String featurePath = "";
                            String trainFileName = "";
                            for (File trainedFeature : featureFiles) {
                                String featureName = trainedFeature.getName();
                                if (!featureName.contains(".")) {
                                    featurePath = trainedFeature.getAbsolutePath();
                                } else {
                                    trainFileName = trainedFeature.getName();
                                }
                                Mat keyPoint = this.opencvService.loadMObject(featurePath);
                                LinkedList<DMatch> goodMatch = this.opencvService.DetectORBFeature(greyImage, keyPoint);
                                Map response = new HashMap();
                                response.put("TEST FILE NAME", imgName);
                                response.put("TRAIN FILE NAME", trainFileName);
                                response.put("LIST OF MATCHES", goodMatch.size());
                                response.put("Match Details", goodMatch.toString());
                                responseArr.add(response);
//                            if(goodMatch.size()>2){
//                                goodMatch.forEach(dMatch -> {
//                                    System.out.println("Distance Match= "+dMatch.distance);
//                                    response.put("Distance Match",dMatch.distance);
//                                });
//                            }
                            }
                        }
                    }
                }
            }
        }
        return responseArr;
    }
}
