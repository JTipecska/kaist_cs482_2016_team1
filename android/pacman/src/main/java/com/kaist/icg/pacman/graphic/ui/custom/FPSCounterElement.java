package com.kaist.icg.pacman.graphic.ui.custom;

import android.graphics.Color;
import android.os.SystemClock;

import com.kaist.icg.pacman.graphic.ui.TextElement;
import com.kaist.icg.pacman.graphic.ui.UIElement;

import java.nio.FloatBuffer;

public class FPSCounterElement extends TextElement {
    private long lastUpdate;
    private long elapsedTime;
    private long lastFPSupdate;
    private int nbFrameSinceLastFPSupdate;

    public FPSCounterElement(int vertexBufferSize, FloatBuffer vertexBuffer, FloatBuffer normalBuffer, FloatBuffer textureCoordinatesBuffer) {
        super(vertexBufferSize, vertexBuffer, normalBuffer, textureCoordinatesBuffer);

        this.setBackgroundImage("button_yellow.png");
        this.setBackgroundColor(Color.TRANSPARENT);
        this.setForegroundColor(Color.BLACK);
        this.setTextSize(20);
        this.setScreenPosition(20, 20, UIElement.EAnchorPoint.TopRight);
        this.setPadding(15, 30, 15, 30);
    }

    public void update(long elapsedTime) {
        nbFrameSinceLastFPSupdate++;

        if(SystemClock.uptimeMillis() - lastFPSupdate > 1000) {
            //Compute FPS: number_frame_drew / (elapsed_time / 1000)
            this.setText((int) (nbFrameSinceLastFPSupdate / ((SystemClock.uptimeMillis() -
                    lastFPSupdate) / 1000)) + " FPS");

            nbFrameSinceLastFPSupdate = 0;
            lastFPSupdate = SystemClock.uptimeMillis();
        }
        lastUpdate = SystemClock.uptimeMillis();
    }
}
