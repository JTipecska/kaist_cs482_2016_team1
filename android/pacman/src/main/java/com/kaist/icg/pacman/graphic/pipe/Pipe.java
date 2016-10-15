package com.kaist.icg.pacman.graphic.pipe;

import com.kaist.icg.pacman.graphic.Drawable;
import com.kaist.icg.pacman.manager.InputManager;

import java.util.ArrayList;


public class Pipe extends Drawable {
    private final static long PIPE_SPEED = 200;
    private final static int NB_PIPE_PART = 20;
    private final static float PIPE_SIZE = 1f;
    private final static float TRANSLATE_Y = 0.9f;
    private final static float ROTATION_SPEED = 3f;

    private ArrayList<PipePart> parts;
    private long pipeAnimationTime;
    private float translationZ;
    private float rotationZ;

    public Pipe() {
        pipeAnimationTime = 0;
        parts = new ArrayList<>();

        for(int i = 0; i<NB_PIPE_PART; i++) {
            PipePart part = new PipePart();
            part.setPosition(0, 0, 2 + -i * PIPE_SIZE);
            parts.add(part);
        }
    }

    public void onUpdate(long elapsedTime) {
        pipeAnimationTime += elapsedTime;
        translationZ = PIPE_SIZE * ((float)(pipeAnimationTime) / PIPE_SPEED);

        if(Math.abs(InputManager.getInstance().getHorizontalMovement()) >= 0.8)
            rotationZ -= ROTATION_SPEED * Math.signum(InputManager.getInstance().getHorizontalMovement());

        for(int i = 0; i<NB_PIPE_PART; i++) {
            parts.get(i).setPosition(0, TRANSLATE_Y, translationZ + 2 + -i * PIPE_SIZE);
            parts.get(i).setRotation(0, 0, 1f, rotationZ);
        }

        if(parts.get(0).getPosition()[2] > 2 + PIPE_SIZE) {
            PipePart part = parts.remove(0);
            part.setPosition(0, TRANSLATE_Y, translationZ + 2 + -(NB_PIPE_PART - 1) * PIPE_SIZE);
            parts.add(part);
            pipeAnimationTime -= PIPE_SPEED;
        }
    }

    @Override
    public void draw() {

        for(PipePart part : parts)
            part.draw();
    }
}