package com.dh.project.Face_Recognition.service;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class MyVideoCapture {
    private VideoCapture capture;
    private OpencvServiceImpl _opencv;

    public MyVideoCapture() {
        capture = new VideoCapture();
        capture.open(0);
        this._opencv = new OpencvServiceImpl();
    }


    public BufferedImage getOneFrame() throws IOException {
        Mat mat = new Mat();
        capture.read(mat);
        Size size = new Size(950, 500);
        Mat resizeImage = new Mat();
        Imgproc.resize(mat, resizeImage, size);
        Mat face = this._opencv.detectFace(mat);
        if(face != null){
            return Mat2BufferedImage(face);
        }
        return Mat2BufferedImage(mat);
    }


    public final Mat loadMat(String path) {
        try {
            int cols;
            byte[] data;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
                cols = (int) ois.readObject();
                data = (byte[]) ois.readObject();
            }
            Mat mat = new Mat(data.length / cols, cols, CvType.CV_8UC3);
            mat.put(0, 0, data);
            return mat;
        } catch (IOException | ClassNotFoundException | ClassCastException ex) {
            System.err.println("ERROR: Could not load mat from file: " + path);
        }
        return null;
    }

    public static BufferedImage Mat2BufferedImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

}
