package com.company;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.IOException;

public class App {
    private AppFrame myFrame = new AppFrame();
    private DrawPanel drawPanel = new DrawPanel();
    private ButtonPanel buttonPanel = new ButtonPanel();
    private Button fileButton = new Button("Choose Map File");
    private Button generateButton = new Button("Generate Path");
    private String fileName;

    //public static int delayTime = 1000;
    //public static int i = 0;

    private LaserMap laserMap;
    private Laser laser;

    void init(){
        FileDialog fileDialog = new FileDialog(myFrame, "choose map file", FileDialog.LOAD);
        fileButton.addActionListener( e ->{
            fileDialog.setVisible(true);
            fileName = fileDialog.getFile();
            System.out.println("file name is " + fileName);
            try {
                laserMap = LaserMap.initMap(fileName);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Not invalid input file");
            }
            int[] sourceP = {0, 0};
            int[] targetP = {0, laserMap.cols};
            if(!laserMap.setSourceAndTarget(sourceP, Laser.Dir.RIGHT, targetP, Laser.Dir.RIGHT)){
                System.out.println("Not valid enter and out position in map");
                return;
            }
            drawPanel.preparePaintMap(laserMap);
            drawPanel.repaint();
        });

        generateButton.addActionListener( e->{
            laser = new Laser(laserMap, Mirror.Dir.DIR0);
            try {
                laser.start();
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            if(laser.shortestSteps == Integer.MAX_VALUE)
            {
                System.out.println("Not found the shortest path");
                drawPanel.preparePaintInvalidResult();
                drawPanel.repaint();
            }else {
                System.out.println("Found the shortest path, steps:" + laser.shortestSteps);
                System.out.println("Path:" + laser.shortestPath);
                for (Mirror mirror : laser.shortestPath) {
                    System.out.println("[" + mirror.getPo()[0] + ", " + mirror.getPo()[1] + ", " +
                            (mirror.getDir() == Mirror.Dir.DIR0 ? "\\" : "/") + "]");
                }
                drawPanel.preparePaintLaserPath(laser.shortestPath);
                drawPanel.repaint();
            }
        });
        buttonPanel.add(fileButton);
        buttonPanel.add(generateButton);
        myFrame.add("Center", drawPanel);
        myFrame.add("East", buttonPanel);
        myFrame.setVisible(true);
    }

    public static void main(String[] args){
        new App().init();
    }
}
