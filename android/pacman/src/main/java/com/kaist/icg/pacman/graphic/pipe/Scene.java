package com.kaist.icg.pacman.graphic.pipe;

import com.kaist.icg.pacman.graphic.Object3DFactory;
import com.kaist.icg.pacman.manager.InputManager;

/**
 * Created by root on 16. 10. 26.
 */

public class Scene {
    private final static double PIPE_SPEED = 200.0;

    private final static float ROTATION_SPEED = 3f;
    private SceneRoot root;
    private Pacman pacman;
    private float translationZ;
    private float rotationZ;

    public Scene() {
        root = new SceneRoot();
        pacman = Object3DFactory.getInstance().instanciate("objects/Pacman.obj", Pacman.class);
        pacman.setTextureFile("Pacman_yellow.png");
    }

    public void onUpdate(long elapsedTime) {

        translationZ = (float)(elapsedTime/PIPE_SPEED);//(float)(elapsedTime / PIPE_SPEED);
        if(Math.abs(InputManager.getInstance().getHorizontalMovement()) >= 0.8)
            rotationZ -= ROTATION_SPEED * Math.signum(InputManager.getInstance().getHorizontalMovement());
        pacman.setPosition(0.0f, -0.8f, 2.0f);
        pacman.setScale(0.3f, 0.3f, 0.3f);
        pacman.setRotation(0, 1, 0, 180f);
        root.onUpdate(translationZ, rotationZ);
    }
    public void render() {
        root.draw();
        pacman.draw();
    }
}

