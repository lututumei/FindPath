package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayDeque;

public class DrawPanel extends JPanel implements Runnable {
    private final int DRAWPANELWIDTH = AppFrame.HEIGHT - AppFrame.MANUWIDTH;
    private final int DRAWPANELHEIGHT = DRAWPANELWIDTH;
    private final int SURROUNDWIDTH = 20;

    private final double delayTime = 0.05; // s
    private final double velocity = 8;// 1 grid/ 1 s
    public static int i = 0;  //loop index

    private LaserMap map;
    private ArrayDeque<Mirror> laserPath;
    private int laserSteps;
    private int width = DRAWPANELWIDTH - 4 * SURROUNDWIDTH;
    private int height = DRAWPANELHEIGHT - 4 * SURROUNDWIDTH;
    private int size; //size between two lines
    private int alignToUp;
    private int alignToRight;
    private PaintType paintType;

    enum PaintType{
        SURROUND,
        MAP,
        PATH,
        STRING
    }

    public DrawPanel(){
        super();
        paintType = PaintType.SURROUND;
    }

    public void update(Graphics g){
        paint(g);
    }
    public void paint(Graphics g) {
        super.paint(g);
        switch(paintType){
            case SURROUND:
                paintSurround(g);
                break;
            case MAP:
                paintSurround(g);
                paintMap(g);
                break;
            case PATH:
                paintSurround(g);
                paintMap(g);
                paintLaser(g);
                break;
            case STRING:
                paintSurround(g);
                paintMap(g);
                g.setColor(new Color(200, 0, 40));
                g.setFont(new Font("Invalid", Font.BOLD, 30));
                g.drawString("Can't find the shortest path!", 50, 200);
                break;
        }
    }

    public void preparePaintMap(LaserMap laserMap)
    {
        map = laserMap;
        size = (Math.min(width / map.cols, height / map.rows));
        alignToUp = (height - size * map.rows) / 2 + 2 * SURROUNDWIDTH;
        alignToRight = (width - size * map.cols) / 2 + 2 * SURROUNDWIDTH;
        paintType = PaintType.MAP;
    }

    public void preparePaintLaserPath(ArrayDeque<Mirror> path, int steps)
    {
        laserPath = path;
        laserPath.addLast(new Mirror(map.getTarget(), Mirror.Dir.DIR0));
        laserSteps = steps;
        paintType = PaintType.PATH;
    }

    public void preparePaintInvalidResult(){
        paintType = PaintType.STRING;
    }

    private void paintSurround(Graphics g){
        g.drawLine(SURROUNDWIDTH, SURROUNDWIDTH, DRAWPANELWIDTH - SURROUNDWIDTH, SURROUNDWIDTH);
        g.drawLine(SURROUNDWIDTH, DRAWPANELHEIGHT - SURROUNDWIDTH, DRAWPANELWIDTH - SURROUNDWIDTH, DRAWPANELHEIGHT - SURROUNDWIDTH);
        g.drawLine(SURROUNDWIDTH, SURROUNDWIDTH, SURROUNDWIDTH, DRAWPANELHEIGHT - SURROUNDWIDTH);
        g.drawLine(DRAWPANELWIDTH - SURROUNDWIDTH, SURROUNDWIDTH, DRAWPANELWIDTH - SURROUNDWIDTH, DRAWPANELHEIGHT - SURROUNDWIDTH);
    }

    private void paintMap(Graphics g){
        g.setColor(new Color(80, 80, 200));
        paintGrid(g);
        paintMirrors(g);
        paintObstructs(g);
    }
    private void paintGrid(Graphics g) {
        for (int y = 0; y <= map.rows; y++) {
            g.drawLine(alignToRight, y * size + alignToUp, alignToRight + map.cols * size, y * size + alignToUp);
        }
        for (int x = 0; x <= map.cols; x++) {
            g.drawLine(alignToRight + x * size, alignToUp, alignToRight + x * size, alignToUp + map.rows * size);
        }
    }

    private void paintMirrors(Graphics g){
        for(int[] m : map.mirrors){
            g.drawOval(alignToRight + m[1] * size + size/4, alignToUp + m[0] * size, size/2, size);
        }
    }

    private void paintObstructs(Graphics g){
        for(int[] o : map.obstructions){
            g.drawLine(alignToRight + o[1] * size, alignToUp + o[0] * size,
                    alignToRight + (o[1] + 1) * size, alignToUp + (o[0] + 1) * size);
            g.drawLine(alignToRight +(o[1] + 1) * size, alignToUp + o[0] * size,
                    alignToRight + o[1] * size, alignToUp + (o[0] + 1) * size);
        }
    }

    /*private void paintLaser(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(new Color(200, 0, 40));
        g2.setStroke(new BasicStroke(3.0f));
        int[] sourceP = map.getSource();
        int[] targetP = {0, 0};
        //laserPath.addLast(new Mirror(map.getTarget(), Mirror.Dir.DIR0));

        while (laserPath.size() > 0) {
            Mirror targetMirror = laserPath.pop();
            targetP = targetMirror.getPo();
            int x1 = alignToRight + sourceP[1] * size + size / 2;
            int y1 = alignToUp + sourceP[0] * size + size / 2;
            int x2 = alignToRight + targetP[1] * size + size / 2;
            int y2 = alignToUp + targetP[0] * size + size / 2;
            g2.drawLine(x1, y1, x2, y2);
            sourceP = targetP;
        }
    }*/

    private void paintLaser(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(200, 0, 40));
        g2.setStroke(new BasicStroke(3.0f));
        int[] sourceP = map.getSource();
        int[] targetP;

        double totalTime = i * delayTime;
        double totalStep = totalTime * velocity;
        double passedStep = 0;
        int x1, y1, x2, y2;

        ArrayDeque<Mirror> path = laserPath.clone();
        while (path.size() > 0) {
            Mirror targetMirror = path.peek();
            targetP = targetMirror.getPo();
            int distance = LaserMap.getDistance(targetP, sourceP);
            if(passedStep + distance <= totalStep){
                //System.out.println("==1==: " + i);
                x1 = alignToRight + sourceP[1] * size + size / 2;
                y1 = alignToUp + sourceP[0] * size + size / 2;
                x2 = alignToRight + targetP[1] * size + size / 2;
                y2 = alignToUp + targetP[0] * size + size / 2;
                g2.drawLine(x1, y1, x2, y2);
                passedStep += distance;
                path.pop();
                if(passedStep == totalStep)
                    break;
            } else{
                //System.out.println("==2==: " + i);
                x1 = alignToRight + sourceP[1] * size + size / 2;
                y1 = alignToUp + sourceP[0] * size + size / 2;
                x2 = (int) (alignToRight + sourceP[1] * size + size / 2 + size * (targetP[1] - sourceP[1]) * (totalStep - passedStep)/distance);
                y2 = (int) (alignToUp + sourceP[0] * size + size / 2 + size * (targetP[0] - sourceP[0]) * (totalStep - passedStep)/distance);
                g2.drawLine(x1, y1, x2, y2);
                break;
            }
            sourceP = targetP;
        }
    }

    @Override
    public void run(){
        double loopNum = laserSteps / velocity / delayTime;
        while(i <= loopNum){
            //System.out.println("paint times: " + i);
            repaint();
            i++;
            try {
                Thread.sleep((long) (delayTime * 1000));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}


