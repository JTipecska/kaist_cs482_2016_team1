package com.kaist.icg.pacman.graphic.ui;

import android.graphics.Rect;
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

    public enum EAnchorPoint {
        TopLeft,
        TopRight,
        BottomLeft,
        BottomRight,
        Center
    }

    protected int elementWidth;
    protected int elementHeight;
    protected Rect bounds;
    protected EAnchorPoint anchorPoint;
    protected float zIndex;

    private int lastScreenPositionX;
    private int lastScreenPositionY;

    public UIElement(int vertexBufferSize, FloatBuffer vertexBuffer, FloatBuffer normalBuffer, FloatBuffer textureCoordinatesBuffer) {
        super(vertexBufferSize, vertexBuffer, normalBuffer, textureCoordinatesBuffer);

        this.shader = ShaderManager.Shader.UI;
        this.anchorPoint = EAnchorPoint.TopLeft;
        this.bounds = new Rect();

        uiElements.add(this);
    }

    public void update() {
        this.setScreenPosition(bounds.left, bounds.top, EAnchorPoint.TopLeft);
        this.setScreenSize(bounds.width(), bounds.height());
    }

    public void setScreenPosition(int x, int y, EAnchorPoint anchorPoint) {
        this.anchorPoint = anchorPoint;
        this.lastScreenPositionX = x;
        this.lastScreenPositionY = y;

        switch (anchorPoint) {
            case TopLeft:
                this.bounds.left = x;
                this.bounds.top = y;
                this.bounds.right = x + elementWidth;
                this.bounds.bottom = y + elementHeight;
                break;
            case TopRight:
                this.bounds.left = (int) (Camera.getInstance().getScreenWidth() - x - elementWidth);
                this.bounds.top = y;
                this.bounds.right = this.bounds.left + elementWidth;
                this.bounds.bottom = y + elementHeight;
                break;
            case BottomLeft:
                this.bounds.left = x;
                this.bounds.top = (int) (Camera.getInstance().getScreenHeight() - y - elementHeight);
                this.bounds.right = x + elementWidth;
                this.bounds.bottom = this.bounds.top + elementHeight;
                break;
            case BottomRight:
                this.bounds.left = (int) (Camera.getInstance().getScreenWidth() - x - elementWidth);
                this.bounds.top = (int) (Camera.getInstance().getScreenHeight() - y - elementHeight);
                this.bounds.right = this.bounds.left + elementWidth;
                this.bounds.bottom = this.bounds.top + elementHeight;
                break;
            case Center:
                this.bounds.left = (int) (Camera.getInstance().getScreenWidth() / 2 - elementWidth / 2);
                this.bounds.top = (int) (Camera.getInstance().getScreenHeight() / 2 - elementHeight / 2);
                this.bounds.right = this.bounds.left + elementWidth;
                this.bounds.bottom = this.bounds.top + elementHeight;
                break;
        }

        setPosition(Camera.getInstance().screenToCameraX(this.bounds.left),
                Camera.getInstance().screenToCameraY(this.bounds.top),
                0);
    }

    protected void updateBounds() {
        this.setScreenPosition(this.lastScreenPositionX,
                this.lastScreenPositionY,
                this.anchorPoint);
    }

    @Override
    public void setPosition(float x, float y, float z) {
        super.setPosition(x, y, Camera.getInstance().getNearestZPosition() + zIndex);
    }

    @Override
    public void draw() {
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        super.draw();

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    public void setScreenSize(int width, int height) {
        this.elementWidth = width;
        this.elementHeight = height;
        this.updateBounds();

        this.setScale(Camera.getInstance().screenWidthToScaleX(bounds.width()),
                Camera.getInstance().screenHeightToScaleY(bounds.height()),
                1);
    }

    /**
     * Set the z-index. It should be in range [0;10[
     * @param z
     */
    public void setZIndex(float z) {
        if(z < 0 || z > 10)
            throw new RuntimeException("Trying to set a z-index out of range (0 <= z-index < 10). Current value: " + z);
        this.zIndex = z / 10000;
    }
}
