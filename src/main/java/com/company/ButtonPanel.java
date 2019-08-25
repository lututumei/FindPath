package com.company;

import javax.swing.*;
import java.awt.*;

public class ButtonPanel extends JPanel {

    public ButtonPanel(){
        super();
        setLayout(new GridLayout(10, 1, 0, 20));
        this.add(new Panel()); //put button lower
    }
}
