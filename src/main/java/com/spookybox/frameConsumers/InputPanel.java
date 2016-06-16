package com.spookybox.frameConsumers;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class InputPanel {
    public final int[] image;
    public final int panelWidth;
    public final int panelHeight;
    private final int startX;
    private final int startY;

    public InputPanel(
            int startX,
            int startY,
            int panelWidth,
            int panelHeight,
            int[] panelImage) {
        this.startX = startX;
        this.startY = startY;
        this.image = panelImage;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
    }

    static BufferedImage stichPanelsToImage(final InputPanelImage inputPanelImage) {
        List<InputPanel> panels = inputPanelImage.panels;
        int imageWidth = panels.get(0).panelWidth * inputPanelImage.panelsInRow;
        int imageHeight = panels.get(0).panelHeight * inputPanelImage.rows;

        BufferedImage image = new BufferedImage(imageWidth, imageHeight, TYPE_INT_ARGB);
        for(InputPanel p : inputPanelImage.panels){
            BufferedImage subImage = image.getSubimage(p.startX, p.startY, p.panelWidth, p.panelHeight);
            subImage.setRGB(0, 0, p.panelWidth, p.panelHeight, p.image, 0, p.panelWidth);
        }
        return image;
    }

    static InputPanelImage splitIntoPanels(
            BufferedImage image,
            int panelsPerRow,
            int numberOfRows) {

        ArrayList<InputPanel> result = new ArrayList<>();
        int panelWidth = image.getWidth() / panelsPerRow;
        int panelHeight = image.getHeight() / numberOfRows;
        for(int panelIndex = 0; panelIndex < panelsPerRow * numberOfRows; panelIndex++){
            int startX = (panelIndex % panelsPerRow) * panelWidth;
            int startY = (panelIndex / panelsPerRow) * panelHeight;
            int readWidth = panelWidth;
            int readHeight = panelHeight ;
            if(startX + readWidth > image.getWidth()){
                readWidth = image.getWidth() - startX;
            }
            if(startY + readHeight > image.getHeight()){
                readHeight = image.getHeight() - startY;
            }
            BufferedImage subImage = image.getSubimage(startX, startY, readWidth, readHeight);
            result.add(new InputPanel(
                    startX,
                    startY,
                    readWidth,
                    readHeight,
                    subImage.getRGB(0,0,subImage.getWidth(), subImage.getHeight(), null, 0, subImage.getWidth())
            ));
        }
        return new InputPanelImage(result, panelsPerRow);
    }
}
