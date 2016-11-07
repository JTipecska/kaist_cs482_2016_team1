package com.kaist.icg.pacman.graphic.pipe;

import com.kaist.icg.pacman.graphic.Drawable;
import com.kaist.icg.pacman.graphic.Object3D;
import com.kaist.icg.pacman.graphic.Object3DFactory;
import com.kaist.icg.pacman.manager.ShaderManager;

import java.util.ArrayList;
import java.util.Random;

public class Population extends Drawable {

    private final static int NB_PIPE_PART = 20;
    private final static float PIPE_SIZE = 1f;
    private ArrayList<Ghost> ghosts;
    private static final float radToDeg = (float) (360 / (Math.PI * 2));
    private double angle;

    public Population() {

        addGhost();
    }
    public void onUpdate(float translationZ) {
        for(Drawable child : children) {
            child.translate(0, 0, translationZ);
            if(getCollision(child.getPosition()))
                System.out.println("HIT ME PUSSY");
        }
        if(children.get(0).getPosition()[2] > 2 + 1f) {
            removeChild(children.get(0));
        }

        Random rand = new Random();
        //Retarded spawn version, so so so retarded
        int  n = rand.nextInt(10) + 1;
        if(n < 2)
            addGhost();
        if(n > 8)
            addCoin();
    }

    public void addGhost() {
        Ghost ghost = Object3DFactory.getInstance().instanciate("objects/Ghost_red.obj", Ghost.class);

        angle = Math.random() * (Math.PI * 2);
        ghost.setShader(ShaderManager.Shader.PHONG);
        ghost.setScale(0.25f, 0.25f, 0.25f);
        ghost.setRotation(0, 0, 1, (float) angle * radToDeg + 90);
        ghost.setPosition((float) (Math.cos(angle) * 1.8), (float) (Math.sin(angle) * 1.8), -20f);
        addChild(ghost);
    }

    public void addCoin() {
        Coin coin = Object3DFactory.getInstance().instanciate("objects/Coin_ball.obj", Coin.class);

        angle = Math.random() * (Math.PI * 2);
        coin.setShader(ShaderManager.Shader.PHONG);
        coin.setScale(0.5f, 0.5f, 0.5f);
        coin.setRotation(0, 0, 1, (float) angle * radToDeg + 90);
        coin.setPosition((float) (Math.cos(angle) * 1.8), (float) (Math.sin(angle) * 1.8), -20f);
        addChild(coin);
    }

    public boolean getCollision(float[] pos) {
        if(Math.sqrt(Math.pow(pos[0], 2.0) + Math.pow(pos[0] + 0.9f, 2.0) + Math.pow(pos[0]-2.f, 2.0)) < 0.5)
            return true;
        return false;
    }
}
