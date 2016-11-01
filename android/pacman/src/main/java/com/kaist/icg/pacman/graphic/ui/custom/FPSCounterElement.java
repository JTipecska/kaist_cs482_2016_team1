package com.kaist.icg.pacman.graphic.ui.custom;

import android.graphics.Color;

import com.kaist.icg.pacman.graphic.ui.TextElement;
import com.kaist.icg.pacman.graphic.ui.UIElement;

import java.nio.FloatBuffer;

public class FPSCounterElement extends TextElement {
    public FPSCounterElement(int vertexBufferSize, FloatBuffer vertexBuffer, FloatBuffer normalBuffer, FloatBuffer textureCoordinatesBuffer) {
        super(vertexBufferSize, vertexBuffer, normalBuffer, textureCoordinatesBuffer);

        this.setBackgroundImage("button_yellow.png");
        this.setBackgroundColor(Color.TRANSPARENT);
        this.setForegroundColor(Color.BLACK);
        this.setTextSize(20);
        this.setScreenPosition(20, 20, UIElement.EAnchorPoint.TopRight);
        this.setPadding(15, 30, 15, 30);
    }
}
