package com.kaist.icg.pacman.graphic.pipe;

import com.kaist.icg.pacman.graphic.Drawable;
import com.kaist.icg.pacman.graphic.Object3D;
import com.kaist.icg.pacman.graphic.Object3DFactory;
import com.kaist.icg.pacman.manager.ShaderManager;


public class Pipe extends Drawable {
    private final static int NB_PIPE_PART = 20;
    private final static float PIPE_SIZE = 1f;
    private Object3D mesh;

    public Pipe() {
        for(int i = 0; i < NB_PIPE_PART; i++) {
            mesh = Object3DFactory.getInstance().instanciate("objects/pipe1.obj", Object3D.class);
            mesh.setTextureFile("Ghost_orange.png");
            mesh.setNormalmapFile("normal.png");
            mesh.setShader(ShaderManager.Shader.DIFFUSE);
            mesh.setPosition(0, 0, 2 + -i * PIPE_SIZE);
            float[] color = {0.4f, 0.4f, 0.4f};
            mesh.material.setColor(color);
            addChild(mesh);
        }
    }

    public void onUpdate(float translationZ) {

        for(int i = 0; i<NB_PIPE_PART; i++) {
            children.get(i).translate(0, 0, translationZ);
        }

        if(children.get(0).getPosition()[2] > 2 + PIPE_SIZE) {
            children.get(0).setPosition(0, 0, children.get(19).getPosition()[2] - PIPE_SIZE);
            addChild(children.get(0));
        }
    }
}