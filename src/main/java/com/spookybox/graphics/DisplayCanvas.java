package com.spookybox.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DisplayCanvas extends Component {
    private BufferedImage mImage;


    public void setImage(BufferedImage image){
        synchronized (this) {
            mImage = image;
        }
    }
    public void paint(Graphics g) {
        if(mImage == null){
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        synchronized (this) {
            g2.drawImage(mImage, 10, 10, this);

        }
        g2.finalize();
    }

    public static DisplayCanvas[] initWindow() {
        DisplayCanvas rgbCanvas = new DisplayCanvas();
        DisplayCanvas depthCanvas = new DisplayCanvas();
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(30, 30, 640, 480);
        JPanel contentPane = new JPanel(new GridLayout(0, 2));
        contentPane.add(rgbCanvas);
        contentPane.add(depthCanvas);
        window.setContentPane(contentPane);
        window.setVisible(true);
        DisplayCanvas[] result =  {rgbCanvas, depthCanvas};
        return result;
    }
}
