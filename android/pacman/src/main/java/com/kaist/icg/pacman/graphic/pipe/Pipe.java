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
            mesh.setTextureFile("normal9.png");
            mesh.setShader(ShaderManager.Shader.DIFFUSENORMAL);
            mesh.setPosition(0, 0, 3 + -i * PIPE_SIZE);
            //brown:
            float[] color = {68.0f/255.0f, 47.0f/255.0f, 41.0f/255.0f};
            //pink:
            //color = new float[] {255.0f/255.0f, 192.0f/255.0f, 203.0f/255.0f};
            //blue:
            color = new float[] {202.0f/255.0f, 225.0f/255.0f, 255.0f/255.0f};
            mesh.getMaterial().setColor(color);
            addChild(mesh);
        }
    }

    public void onUpdate(float translationZ) {

        for(int i = 0; i<NB_PIPE_PART; i++) {
            children.get(i).translate(0, 0, translationZ);
        }

        if(children.get(0).getPosition()[2] > 3 + PIPE_SIZE) {
            children.get(0).setPosition(0, 0, children.get(19).getPosition()[2] - PIPE_SIZE);
            addChild(children.get(0));
        }
    }

    public static int getNbPipePart() {
        return NB_PIPE_PART;
    }
}