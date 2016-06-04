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

    public InputPanel(
            int panelWidth,
            int panelHeight,
            int[] panelImage) {
        this.image = panelImage;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
    }

    static BufferedImage stichPanelsToImage(final InputPanelImage inputPanelImage) {
        List<InputPanel> panels = inputPanelImage.panels;
        int imageWidth = panels.get(0).panelWidth * inputPanelImage.panelsInRow;
        int imageHeight = panels.get(0).panelHeight * panels.size() / inputPanelImage.panelsInRow;

        int[] imageBuffer = new int[imageHeight * imageWidth];
        int imageBufferIndex = 0;
        for(InputPanel p : panels) {
            for (int index = 0; index < p.image.length; index++) {
                imageBuffer[imageBufferIndex] = p.image[index];
                imageBufferIndex += 1;
            }
        }
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, TYPE_INT_ARGB);
        image.setRGB(0, 0, imageWidth, imageHeight, imageBuffer, 0, imageWidth);
        return image;
    }

    static InputPanelImage splitIntoPanels(
            BufferedImage image,
            int panelsPerRow,
            int numberOfRows) {

        ArrayList<InputPanel> result = new ArrayList<>();
        int panelWidth = image.getWidth() / panelsPerRow;
        int panelHeight = image.getHeight() / numberOfRows;
        int[] imageData = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        for(int index = 0; index < imageData.length; index += panelHeight){
            result.add(new InputPanel(
                    panelWidth,
                    panelHeight,
                    Arrays.copyOfRange(imageData, index, index+panelHeight)
            ));
        }
        return new InputPanelImage(result, panelsPerRow);
    }
}
