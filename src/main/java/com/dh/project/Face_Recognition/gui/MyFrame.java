package com.dh.project.Face_Recognition.gui;

import com.dh.project.Face_Recognition.service.MyVideoCapture;
import org.opencv.core.Core;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MyFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static boolean isChecked = true;
    private JLabel lblVideo;
    private MyVideoCapture videoCapture = new MyVideoCapture();



    public MyFrame() throws IOException, InterruptedException {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, dim.width, dim.height);
        getContentPane().setLayout(null);

        JPanel panel = new JPanel();
        panel.setBounds(10, 11, dim.width, dim.height);
        getContentPane().add(panel);
        panel.setLayout(null);

        lblVideo = new JLabel();
        lblVideo.setBounds(0, 0, dim.width, dim.height);
//        lblVideo.setVisible(true);
        panel.add(lblVideo);
        new  MyThread().start();
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MyFrame frame = new MyFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class MyThread extends Thread {
        @Override
        public void run() {
            long start = System.currentTimeMillis();
            while (MyFrame.isChecked) {
                long time = System.currentTimeMillis() - start;
                int seconds = (int) (time / 1000);
                BufferedImage img = null;
                try {
                    img = videoCapture.getOneFrame();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(img != null) {
                    lblVideo.setIcon(new ImageIcon(img));
                }
            }
        }
    }

    private void close() {
        this.dispose();
    }
}
