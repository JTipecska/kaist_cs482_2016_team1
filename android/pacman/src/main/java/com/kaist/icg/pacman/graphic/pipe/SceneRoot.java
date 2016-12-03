package com.kaist.icg.pacman.graphic.pipe;

import com.kaist.icg.pacman.graphic.Drawable;
import com.kaist.icg.pacman.manager.InputManager;

import java.util.ArrayList;

/**
 * Created by root on 16. 10. 16.
 */

public class SceneRoot extends Drawable{

    private final static float TRANSLATE_Y = 0.9f;
    private Pipe pipe;
    private Population population;

    public SceneRoot() {
        pipe = new Pipe();
        addChild(pipe);
        population = new Population();
        addChild(population);
    }

    public void onUpdate(float translationZ, float rotationZ) {
        setRotation(0, 0, 1f, rotationZ);
        setPosition(0, TRANSLATE_Y, 0);

        population.onUpdate(translationZ);
        pipe.onUpdate(translationZ);
    }

    public Pipe getPipe() {
        return pipe;
    }
}
