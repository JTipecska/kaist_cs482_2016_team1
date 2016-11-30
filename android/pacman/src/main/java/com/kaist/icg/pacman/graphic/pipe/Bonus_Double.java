package com.kaist.icg.pacman.graphic.pipe;

import com.kaist.icg.pacman.graphic.Object3D;

import java.nio.FloatBuffer;

/**
 * Created by root on 16. 11. 30.
 */

public class Bonus_Double extends Object3D {

    public Bonus_Double(int vertexBufferSize, FloatBuffer vertexBuffer, FloatBuffer normalBuffer, FloatBuffer textureCoordinatesBuffer) {
        super(vertexBufferSize, vertexBuffer, normalBuffer, textureCoordinatesBuffer);
    }

    public Bonus_Double(int vertexBufferSize, FloatBuffer vertexBuffer, FloatBuffer normalBuffer) {
        super(vertexBufferSize, vertexBuffer, normalBuffer);
    }

    @Override
    public void draw() {
        super.draw();
    }

    @Override
    public void setPosition(float x, float y, float z) {
        super.setPosition(x, y, z);
    }

    @Override
    public void setRotation(float x, float y, float z, float angle) {
        super.setRotation(x, y, z, angle);
    }
}