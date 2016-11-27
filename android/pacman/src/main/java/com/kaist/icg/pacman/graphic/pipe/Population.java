package com.kaist.icg.pacman.graphic.pipe;

import android.renderscript.ScriptIntrinsicYuvToRGB;

import com.kaist.icg.pacman.client.Score;
import com.kaist.icg.pacman.graphic.Drawable;
import com.kaist.icg.pacman.graphic.Object3DFactory;
import com.kaist.icg.pacman.graphic.ui.TextElement;
import com.kaist.icg.pacman.manager.LevelManager;
import com.kaist.icg.pacman.manager.ParticleEmitter;
import com.kaist.icg.pacman.manager.ShaderManager;
import com.kaist.icg.pacman.tool.Material;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import static com.kaist.icg.pacman.graphic.pipe.Population.Type.COIN;
import static com.kaist.icg.pacman.graphic.pipe.Population.Type.GHOST;

public class Population extends Drawable {


    private Drawable unusedGhosts, usedGhosts, unusedCoins, usedCoins;
    private static final float radToDeg = (float) (360 / (Math.PI * 2));
    private final float GHOST_RAD = 0.2f;
    private double angle;
    private Random rand;
    private LevelManager levelManager;

    private CopyOnWriteArrayList<TextElement> scoresElements;

    public enum Type {
        GHOST, COIN, POWER_UP
    }
    public Population() {
        initGhosts();
        initCoins();
        levelManager = LevelManager.getInstance();
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
        for(int i = 0; i < 10; i++) {
            Ghost ghost = Object3DFactory.getInstance().instanciate("objects/Ghost.obj", Ghost.class);
            //ghost.setMaterial(gold);
            ghost.setTextureFile("Ghost_orange.png");
            ghost.setShader(ShaderManager.Shader.PHONGTEX);
            ghost.setCollisionRadius(GHOST_RAD);
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

        for(int i = 0; i < 10; i++) {
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
        updateSpawn(usedGhosts, unusedGhosts, translationZ, GHOST);
        updateSpawn(usedCoins, unusedCoins, translationZ, COIN);
        spawnPopulation();
    }

    public void updateSpawn(Drawable used, Drawable unUsed, float translation, Type type) {
        if(used.children.size() > 0) {
            if (used.children.get(0).getPosition()[2] > 2 + 1f) {
                unUsed.addChild(used.children.get(0));
            }
            for (int i = 0; i < used.children.size(); i++) {
                used.children.get(i).translate(0, 0, translation);
                if (used.children.get(i).getCollision(0.0f, -0.9f, 2.5f, 0.5f)) {
                    float[] position = used.children.get(i).getPosition();
                    position[0] = 0.0f;
                    position[1] = -1.0f;
                    position[2] = 2.0f;
                    unUsed.addChild(used.children.get(i));
                    switch (type) {
                        case GHOST:
                            System.out.println("DEAD MOTEHRFUCKER");
                        case COIN:
                            levelManager.addParticleEmitter(position);
                            // SET SCORE HERE DUNNO HOW OMGOMGOMGOMGOMGOMGOMG PRZ HERP ME

                    }

                }
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
        if(unusedGhosts.children.size() > 0) {
            unusedGhosts.children.get(0).setRotation(0, 0, 1, (float) angle * radToDeg + 90);
            unusedGhosts.children.get(0).setPosition((float) (Math.cos(angle) * 1.8), (float) (Math.sin(angle) * 1.8), -20f);
            usedGhosts.addChild(unusedGhosts.children.get(0));
        }
    }

    public void addCoins(double initAngle, double angle, int noOfCoins) {
            for (int i = 0; i < noOfCoins; i++) {
                if (unusedCoins.children.size() > 0) {
                unusedCoins.children.get(0).setRotation(0, 0, 1, (float) initAngle * radToDeg + 90);
                unusedCoins.children.get(0).setPosition((float) (Math.cos(initAngle) * 1.8), (float) (Math.sin(initAngle) * 1.8), -20f + i);
                usedCoins.addChild(unusedCoins.children.get(0));
                initAngle += angle;
            }
        }
    }
}
