package com.kaist.icg.pacman.graphic.pipe;

import com.kaist.icg.pacman.graphic.Drawable;
import com.kaist.icg.pacman.graphic.Object3DFactory;
import com.kaist.icg.pacman.manager.ShaderManager;

import java.util.ArrayList;

public class Population extends Drawable {

    private final static int NB_PIPE_PART = 20;
    private final static float PIPE_SIZE = 1f;
    private ArrayList<Ghost> ghosts;
    private static final float radToDeg = (float) (360 / (Math.PI * 2));
    private double angle;

    public Population() {
        for (int i = 0; i < NB_PIPE_PART; i++) {

            Ghost ghost = Object3DFactory.getInstance()
                    .instanciate("objects/Ghost.obj", Ghost.class);

            ghost.setTextureFile("Ghost_orange.png");
            angle = Math.random() * (Math.PI * 2);
            ghost.setShader(ShaderManager.Shader.DIFFUSE);
            //ghost.setScale(0.15f, 0.15f, 0.15f);
            ghost.setRotation(0, 0, 1, (float) angle * radToDeg + 90);
            ghost.setPosition((float) (Math.cos(angle) * 1.8), (float) (Math.sin(angle) * 1.8), -i + 0.5f);
            addChild(ghost);
        }
    }

    public void onUpdate(float translationZ) {
        for (int i = 0; i < NB_PIPE_PART; i++)
            children.get(i).translate(0, 0, translationZ);

        if (children.get(0).getPosition()[2] > 2 + 1f) {
            angle = Math.random() * (Math.PI * 2);
            children.get(0).setRotation(0, 0, 1, (float) angle * radToDeg + 90);
            children.get(0).setPosition((float) (Math.cos(angle) * 1.8), (float) (Math.sin(angle) * 1.8), children.get(19).getPosition()[2] - PIPE_SIZE);
            addChild(children.get(0));
        }
    }
}
