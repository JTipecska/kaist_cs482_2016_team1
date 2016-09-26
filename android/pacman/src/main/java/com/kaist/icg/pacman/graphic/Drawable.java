package com.kaist.icg.pacman.graphic;

/**
 * Drawable object class.
 * Every object that can be draw should extend this class
 */
public class Drawable {
    private float[] position;
    private float[] velocity;
    private float[] rotation;

    public Drawable() {
        position = new float[3];
        velocity = new float[3];
        rotation = new float[4];
    }

    public void draw() {
        //TODO: compute matrix and call openGL render
    }

    public void translate(float x, float y, float z) {
        position[0] += x;
        position[1] += y;
        position[2] += z;
    }
}
