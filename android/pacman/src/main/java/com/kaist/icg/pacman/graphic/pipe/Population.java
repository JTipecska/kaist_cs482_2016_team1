package com.kaist.icg.pacman.graphic.pipe;

import com.kaist.icg.pacman.graphic.Drawable;
import com.kaist.icg.pacman.graphic.Object3DFactory;
import com.kaist.icg.pacman.manager.ShaderManager;

import java.util.ArrayList;
import java.util.Random;

public class Population extends Drawable {


    private Drawable unusedGhosts, usedGhosts, unusedCoins, usedCoins;
    private static final float radToDeg = (float) (360 / (Math.PI * 2));
    private double angle;

    public Population() {
        unusedGhosts = new Drawable();
        usedGhosts = new Drawable();
        usedCoins = new Drawable();
        unusedCoins = new Drawable();
        addChild(usedGhosts);
        addChild(usedCoins);
        for(int i = 0; i < 20; i++) {
            Ghost ghost = Object3DFactory.getInstance().instanciate("objects/Ghost.obj", Ghost.class);

            ghost.setTextureFile("Ghost_orange.png");
            ghost.setShader(ShaderManager.Shader.TOONTEX);
            unusedGhosts.addChild(ghost);
        }
        for(int i = 0; i < 10; i++) {
            Coin coin = Object3DFactory.getInstance().instanciate("objects/Coin.obj", Coin.class);

            coin.setShader(ShaderManager.Shader.TOONTEX);
            coin.setScale(0.5f, 0.5f, 0.5f);
            unusedCoins.addChild(coin);
        }
        addGhost();
        addCoin();
    }

    public void onUpdate(float translationZ) {
        for(Drawable child : usedGhosts.children) {
            child.translate(0, 0, translationZ);
        }
        if(usedGhosts.children.get(0).getPosition()[2] > 2 + 1f) {
            unusedGhosts.addChild(usedGhosts.children.get(0));
        }


            Ghost ghost = Object3DFactory.getInstance()
                    .instanciate("objects/Ghost.obj", Ghost.class);

        for(Drawable child : usedCoins.children) {
            child.translate(0, 0, translationZ);
        }
        if(usedCoins.children.get(0).getPosition()[2] > 2 + 1f) {
            unusedCoins.addChild(usedCoins.children.get(0));
        }
        Random rand = new Random();
        //Retarded spawn version, so so so retarded
        int  n = rand.nextInt(10) + 1;
        if(n < 2)
            addGhost();
        if(n > 9)
            addCoin();
    }

    public void addGhost() {
        if(unusedGhosts.children.size() > 1) {

            angle = Math.random() * (Math.PI * 2);
            unusedGhosts.children.get(0).setRotation(0, 0, 1, (float) angle * radToDeg + 90);
            unusedGhosts.children.get(0).setPosition((float) (Math.cos(angle) * 1.8), (float) (Math.sin(angle) * 1.8), -20f);
            usedGhosts.addChild(unusedGhosts.children.get(0));
        }
    }

    public void addCoin() {

        if(unusedCoins.children.size() > 1) {
            angle = Math.random() * (Math.PI * 2);
            unusedCoins.children.get(0).setRotation(0, 0, 1, (float) angle * radToDeg + 90);
            unusedCoins.children.get(0).setPosition((float) (Math.cos(angle) * 1.8), (float) (Math.sin(angle) * 1.8), -20f);
            usedCoins.addChild(unusedCoins.children.get(0));
        }
    }
}
