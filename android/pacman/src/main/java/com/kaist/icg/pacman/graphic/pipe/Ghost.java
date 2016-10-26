package com.kaist.icg.pacman.graphic.pipe;

import android.graphics.drawable.DrawableWrapper;

import com.kaist.icg.pacman.graphic.Drawable;
import com.kaist.icg.pacman.graphic.Object3D;
import com.kaist.icg.pacman.manager.ShaderManager;

/**
 * Created by root on 16. 10. 17.
 */

public class Ghost extends Object3D {

    private final static int NB_PIPE_PART = 20;
    private final static float PIPE_SIZE = 1f;
    private static final float radToDeg = (float) (360 / (Math.PI * 2));
    private Object3D mesh;
    private double angle;
    private float distance, spawn;
    public Ghost() {
        super("Ghost.obj");
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
