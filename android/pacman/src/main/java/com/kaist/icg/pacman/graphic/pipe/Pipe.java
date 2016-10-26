package com.kaist.icg.pacman.graphic.pipe;

import com.kaist.icg.pacman.graphic.Drawable;
import com.kaist.icg.pacman.graphic.Object3D;
import com.kaist.icg.pacman.manager.ShaderManager;


public class Pipe extends Drawable {

    private final static int NB_PIPE_PART = 20;
    private final static float TRANSLATE_Y = 0.9f;
    private PipePart pipe;
    private Population population;

    public Pipe() {
        pipe = new PipePart();
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
}