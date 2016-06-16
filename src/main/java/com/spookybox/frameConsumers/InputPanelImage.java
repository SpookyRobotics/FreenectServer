package com.spookybox.frameConsumers;

import java.util.ArrayList;
import java.util.List;

public class InputPanelImage {
    public final List<InputPanel> panels;
    public final int panelsInRow;
    public final int rows;

    public InputPanelImage(List<InputPanel> panels, int panelsInRow) {
        this.panels = panels;
        this.panelsInRow = panelsInRow;
        this.rows = panels.size() / panelsInRow;

    }
}
