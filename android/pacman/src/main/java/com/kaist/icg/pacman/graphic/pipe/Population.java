package com.kaist.icg.pacman.graphic.pipe;

import com.kaist.icg.pacman.graphic.Drawable;
import com.kaist.icg.pacman.graphic.Object3DFactory;
import com.kaist.icg.pacman.manager.ShaderManager;
import com.kaist.icg.pacman.tool.Material;

import java.util.ArrayList;
import java.util.Random;

public class Population extends Drawable {


    private Drawable unusedGhosts, usedGhosts, unusedCoins, usedCoins;
    private static final float radToDeg = (float) (360 / (Math.PI * 2));
    private double angle;
    private Random rand;
    public Population() {
        initGhosts();
        initCoins();
    }

    public void initGhosts() {
        unusedGhosts = new Drawable();
        usedGhosts = new Drawable();
        addChild(usedGhosts);
        Material gold = new Material();
        gold.setAmbientIntensity(
                (0.212671f*0.24725f + 0.715160f*0.1995f + 0.072169f*0.0745f)/
                        (0.212671f*0.75164f + 0.715160f*0.60648f + 0.072169f*0.22648f));
        gold.setDiffuseColor(new float[] {0.75164f,0.60648f,0.22648f});
        gold.setSpecularColor(new float[] {0.628281f, 0.555802f, 0.366065f});
        gold.setShininess(0.4f*128.0f);
        for(int i = 0; i < 50; i++) {
            Ghost ghost = Object3DFactory.getInstance().instanciate("objects/Ghost.obj", Ghost.class);
            //ghost.setMaterial(gold);
            ghost.setTextureFile("Ghost_orange.png");
            ghost.setShader(ShaderManager.Shader.PHONGTEX);
            unusedGhosts.addChild(ghost);
        }

        angle = Math.random() * (Math.PI * 2);
        addGhost(angle);
    }

    public void initCoins() {
        usedCoins = new Drawable();
        unusedCoins = new Drawable();
        addChild(usedCoins);

        Material gold = new Material();
        gold.setAmbientIntensity(
                (0.212671f*0.24725f + 0.715160f*0.1995f + 0.072169f*0.0745f)/
                (0.212671f*0.75164f + 0.715160f*0.60648f + 0.072169f*0.22648f));
        gold.setDiffuseColor(new float[] {0.75164f,0.60648f,0.22648f});
        gold.setSpecularColor(new float[] {0.628281f, 0.555802f, 0.366065f});
        gold.setShininess(0.4f*128.0f);

        for(int i = 0; i < 20; i++) {
            Coin coin = Object3DFactory.getInstance().instanciate("objects/Coin.obj", Coin.class);
            coin.setMaterial(gold);
            coin.setShader(ShaderManager.Shader.PHONG);
            //coin.setScale(0.5f, 0.5f, 0.5f);
            unusedCoins.addChild(coin);
        }
        double initAngle =  Math.random() * (Math.PI * 2);
        Random rand = new Random();
        int noOfCoins = rand.nextInt(10) + 1;
        //angle = (Math.random() * (Math.PI * 2))/;
        addCoins(initAngle, Math.random() * (Math.PI / 10), noOfCoins);
    }

    public void onUpdate(float translationZ) {
        updateSpawn(usedGhosts, unusedGhosts, translationZ);
        updateSpawn(usedCoins, unusedCoins, translationZ);
        spawnPopulation();
    }

    public void updateSpawn(Drawable used, Drawable unUsed, float translation) {
        if(used.children.size() > 0) {
            for (Drawable child : used.children) {
                child.translate(0, 0, translation);
            }
            if (used.children.get(0).getPosition()[2] > 2 + 1f) {
                unUsed.addChild(used.children.get(0));
            }
        }
    }

    public void spawnPopulation() {
        Random rand = new Random();
        angle = Math.random() * (Math.PI * 2);
        //Retarded spawn version, so so so retarded
        int n = rand.nextInt(100) + 1;
        if (n < 15)
            addGhost(angle);
        if (n > 98) {
            Random random = new Random();
            int noOfCoins = random.nextInt(7) + 5;
            addCoins(angle, Math.random() * (Math.PI / 10), noOfCoins);
        }
    }

    public void addGhost(double angle) {
        if(unusedGhosts.children.size() > 1) {

            angle = Math.random() * (Math.PI * 2);
            unusedGhosts.children.get(0).setRotation(0, 0, 1, (float) angle * radToDeg + 90);
            unusedGhosts.children.get(0).setPosition((float) (Math.cos(angle) * 1.8), (float) (Math.sin(angle) * 1.8), -20f);
            usedGhosts.addChild(unusedGhosts.children.get(0));
        }
    }

    public void addCoins(double initAngle, double angle, int noOfGhosts) {
        for (int i = 0; i < noOfGhosts; i++) {
            if (unusedCoins.children.size() > 1) {
                unusedCoins.children.get(0).setRotation(0, 0, 1, (float) initAngle * radToDeg + 90);
                unusedCoins.children.get(0).setPosition((float) (Math.cos(initAngle) * 1.8), (float) (Math.sin(initAngle) * 1.8), -20f + i);
                usedCoins.addChild(unusedCoins.children.get(0));
                initAngle += angle;
            }
        }
    }
}
