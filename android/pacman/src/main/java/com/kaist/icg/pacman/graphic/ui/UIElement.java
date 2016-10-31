package com.kaist.icg.pacman.graphic.ui;

import android.opengl.GLES20;

import com.kaist.icg.pacman.graphic.Camera;
import com.kaist.icg.pacman.graphic.Object3D;
import com.kaist.icg.pacman.manager.ShaderManager;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class UIElement extends Object3D {
    private static ArrayList<UIElement> uiElements = new ArrayList<>();

    public static void updateUIElements() {
        for(UIElement uiElement : uiElements)
            uiElement.update();
    }

    private float screenX;
    private float screenY;
    private float screenWidth;
    private float screenHeight;

    public UIElement(int vertexBufferSize, FloatBuffer vertexBuffer, FloatBuffer normalBuffer, FloatBuffer textureCoordinatesBuffer) {
        super(vertexBufferSize, vertexBuffer, normalBuffer, textureCoordinatesBuffer);

        this.shader = ShaderManager.Shader.UI;

        uiElements.add(this);
    }

    public void update() {
        this.setScreenPosition(screenX, screenY);
        this.setSize(screenWidth, screenHeight);
    }

    public void setScreenPosition(float x, float y) {
        this.screenX = x;
        this.screenY = y;

        setPosition(Camera.getInstance().screenToCameraX(x),
                Camera.getInstance().screenToCameraY(y),
                0);
    }

    @Override
    public void setPosition(float x, float y, float z) {
        super.setPosition(x, y, Camera.getInstance().getNearestZPosition());
    }

    @Override
    public void draw() {
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        super.draw();

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    public void setSize(float width, float height) {
        this.screenWidth = width;
        this.screenHeight = height;

        this.setScale(Camera.getInstance().screenWidthToScaleX(width),
                Camera.getInstance().screenHeightToScaleY(height),
                1);
    }
}
