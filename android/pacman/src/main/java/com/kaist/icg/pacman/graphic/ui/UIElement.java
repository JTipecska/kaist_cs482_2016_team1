package com.kaist.icg.pacman.graphic.ui;

import android.opengl.GLES20;

import com.kaist.icg.pacman.graphic.Camera;
import com.kaist.icg.pacman.graphic.Object3D;
import com.kaist.icg.pacman.manager.ShaderManager;

import java.nio.FloatBuffer;

public class UIElement extends Object3D {

    public UIElement(int vertexBufferSize, FloatBuffer vertexBuffer, FloatBuffer normalBuffer, FloatBuffer textureCoordinatesBuffer, String textureFile) {
        super(vertexBufferSize, vertexBuffer, normalBuffer, textureCoordinatesBuffer, textureFile);

        this.shader = ShaderManager.Shader.UI;
        this.setPosition(0, 0, 0);
    }

    @Override
    public void setPosition(float x, float y, float z) {
        super.setPosition(x + 0, y, Camera.getInstance().getNearestZPosition());
    }

    @Override
    public void draw() {
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        super.draw();

        GLES20.glDisable(GLES20.GL_BLEND);
    }
}
