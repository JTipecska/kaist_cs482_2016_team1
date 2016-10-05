package com.kaist.icg.pacman.graphic.pipe;

import com.kaist.icg.pacman.graphic.Object3D;

public class PipePart extends Object3D {
    private static final float radToDeg = (float) (360 / (Math.PI * 2));
    private Object3D mesh;
    private double angle;

    public PipePart() {
        super("pipe0.obj");

        angle = Math.random() * (Math.PI * 2);

        mesh = new Object3D("suzanne.obj");
        mesh.setColor(this.color);
        mesh.setScale(0.15f, 0.15f, 0.15f);
        mesh.setRotation(0, 0, 1, (float) angle * radToDeg + 90);
        mesh.setPosition((float) (Math.cos(angle) * 1.8), (float)(Math.sin(angle) * 1.8), 0.5f);

        addChild(mesh);
    }

    @Override
    public void draw(float[] projectionMatrix, float[] viewMatrix) {
        super.draw(projectionMatrix, viewMatrix);
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
