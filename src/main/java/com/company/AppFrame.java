package com.company;

import javax.swing.*;
import javax.swing.tree.FixedHeightLayoutCache;
import java.awt.*;
import java.util.ArrayDeque;


public class AppFrame extends JFrame{

    String TITLE = "Find Shortest Path";
    public static final int WIDTH = 700; //700
    public static final int HEIGHT = 600; //600
    public static final int MANUWIDTH = 30; //???

    public AppFrame(){
        super();
        setLayout(new BorderLayout());

        setTitle(TITLE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
