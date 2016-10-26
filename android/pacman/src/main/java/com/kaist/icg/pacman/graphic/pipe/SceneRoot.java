package com.kaist.icg.pacman.graphic.pipe;

import com.kaist.icg.pacman.manager.InputManager;

import java.util.ArrayList;

/**
 * Created by root on 16. 10. 16.
 */

public class SceneRoot {
    private final static double PIPE_SPEED = 200.0;

    private final static float ROTATION_SPEED = 3f;
    private Pipe pipe;
    private long animationTime;
    private float translationZ;
    private float rotationZ;
    private final static int NB_PIPE_PART = 20;
    private final static float PIPE_SIZE = 1f;

    public SceneRoot() {
        animationTime = 0;
        pipe = new Pipe();
    }

    public void onUpdate(long elapsedTime) {


        translationZ = (float)(elapsedTime/PIPE_SPEED);//(float)(elapsedTime / PIPE_SPEED);
        if(Math.abs(InputManager.getInstance().getHorizontalMovement()) >= 0.8)
            rotationZ -= ROTATION_SPEED * Math.signum(InputManager.getInstance().getHorizontalMovement());
        pipe.onUpdate(translationZ, rotationZ);
            //animationTime -= PIPE_SPEED;

    }
    public void render() {
        pipe.draw();
    }
}
