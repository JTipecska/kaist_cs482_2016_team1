package com.kaist.icg.pacman.graphic.pipe;

import com.kaist.icg.pacman.graphic.Drawable;
import com.kaist.icg.pacman.graphic.Object3DFactory;
import com.kaist.icg.pacman.graphic.ui.TextElement;
import com.kaist.icg.pacman.manager.LevelManager;
import com.kaist.icg.pacman.manager.ShaderManager;
import com.kaist.icg.pacman.tool.Material;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Population extends Drawable {


    private Drawable unusedGhosts, usedGhosts, unusedCoins, usedCoins, usedBonus, unusedBonus;
    private static final float radToDeg = (float) (360 / (Math.PI * 2));
    private final float GHOST_RAD = 0.1f, COIN_RAD = 0.1f;
    private double angle;
    private LevelManager levelManager;
    private CopyOnWriteArrayList<TextElement> scoresElements;

    public Population() {
        initGhosts();
        initCoins();
        initBonus();
        angle = Math.random() * (Math.PI * 2);
        addGhost(angle);

        double initAngle =  Math.random() * (Math.PI * 2);
        Random rand = new Random();
        int noOfCoins = rand.nextInt(10) + 1;
        addCoins(initAngle, Math.random() * (Math.PI / 10), noOfCoins);

        levelManager = LevelManager.getInstance();
    }

    public void onUpdate(float translationZ) {
        updateSpawn(usedGhosts, unusedGhosts, translationZ);
        updateSpawn(usedCoins, unusedCoins, translationZ);
        updateSpawn(usedBonus, unusedBonus, translationZ);
        spawnPopulation();
        System.out.println(unusedBonus.children.size() + "     " + usedBonus.children.size());
    }

    public void initGhosts() {

        unusedGhosts = new Drawable();
        usedGhosts = new Drawable();
        addChild(usedGhosts);
        for(int i = 0; i < 30; i++) {
            Ghost ghost = Object3DFactory.getInstance().instanciate("objects/Ghost.obj", Ghost.class);
            Random rand = new Random();
            int n = rand.nextInt(4);
            switch (n) {
                case 0:
                    ghost.setTextureFile("Ghost_orange.png");
                    break;
                case 1:
                    ghost.setTextureFile("Ghost_blue.png");
                    break;
                case 2:
                    ghost.setTextureFile("Ghost_pink.png");
                    break;
                case 3:
                    ghost.setTextureFile("Ghost_red.png");
                    break;
            }
            ghost.setType(Type.GHOST);
            ghost.setShader(ShaderManager.Shader.PHONGTEX);
            ghost.setCollisionRadius(GHOST_RAD);
            unusedGhosts.addChild(ghost);
        }
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
        //gold.setShininess(0.4f*128.0f);
        gold.setShininess(8.0f);

        for(int i = 0; i < 20; i++) {
            Coin coin = Object3DFactory.getInstance().instanciate("objects/Coin.obj", Coin.class);
            coin.setMaterial(gold);
            coin.setType(Type.COIN);
            coin.setShader(ShaderManager.Shader.PHONG);

            coin.setCollisionRadius(GHOST_RAD);
            //coin.setScale(0.5f, 0.5f, 0.5f);
            unusedCoins.addChild(coin);
        }

    }

    public void initBonus() {

        unusedBonus = new Drawable();
        usedBonus = new Drawable();
        addChild(usedBonus);
        Bonus_Double bonus = Object3DFactory.getInstance().instanciate("objects/Bonus_double.obj", Bonus_Double.class);
        bonus.setType(Type.BONUS_DOUBLE);
        bonus.setTextureFile("Bonus_double.png");
        bonus.setShader(ShaderManager.Shader.PHONGTEX);
        bonus.setCollisionRadius(GHOST_RAD);
        unusedBonus.addChild(bonus);

        Bonus_Double bonus1 = Object3DFactory.getInstance().instanciate("objects/Bonus_invincible.obj", Bonus_Double.class);
        bonus1.setType(Type.BONUS_INVINCIBLE);
        //bonus1.setTextureFile("Bonus_double.png");
        bonus1.setShader(ShaderManager.Shader.PHONGTEX);
        bonus1.setCollisionRadius(GHOST_RAD);
        unusedBonus.addChild(bonus1);

        Bonus_Double bonus2 = Object3DFactory.getInstance().instanciate("objects/Malus_dark.obj", Bonus_Double.class);
        bonus2.setType(Type.MALUS_DARK);
        bonus2.setTextureFile("Dark.png");
        bonus2.setShader(ShaderManager.Shader.PHONGTEX);
        bonus2.setCollisionRadius(GHOST_RAD);
        unusedBonus.addChild(bonus2);

        Bonus_Double bonus3 = Object3DFactory.getInstance().instanciate("objects/Malus_inverse.obj", Bonus_Double.class);
        bonus3.setType(Type.MALUS_INVERSE);
        bonus3.setTextureFile("Bonus_double.png");
        bonus3.setShader(ShaderManager.Shader.PHONGTEX);
        bonus3.setCollisionRadius(GHOST_RAD);
        unusedBonus.addChild(bonus3);

    }

    public void updateSpawn(Drawable used, Drawable unUsed, float translation) {
        if(used.children.size() > 0) {
            if (used.children.get(0).getPosition()[2] > 2 + 1f) {
                unUsed.addChild(used.children.get(0));
            }
            for (int i = 0; i < used.children.size(); i++) {
                used.children.get(i).translate(0, 0, translation);
                if (used.children.get(i).getCollision(0.0f, -0.9f, 2.5f, 0.5f)) {
                    switch (used.children.get(i).getType()) {
                        case GHOST:
                            levelManager.reduceLife();
                            break;

                        case COIN:
                            levelManager.addParticleEmitter();
                            levelManager.addPoint();
                            break;

                        case BONUS_DOUBLE:
                            levelManager.activateDoublePoints();
                            break;

                        case BONUS_INVINCIBLE:
                            levelManager.activateInvincible();
                            break;

                        case MALUS_DARK:
                            levelManager.activateDarkMalus();
                            break;

                        case MALUS_INVERSE:
                            levelManager.activateReverseMalus();
                            break;

                        default:
                            break;
                    }
                    unUsed.addChild(used.children.get(i));
                }
            }
        }
    }

    public void spawnPopulation() {
        Random rand = new Random();
        angle = Math.random() * (Math.PI * 2);
        //Retarded spawn version, so so so retarded
        int n = rand.nextInt(1000) + 1;
        if (n < 150)
            addGhost(angle);
        if (n > 980) {
            Random random = new Random();
            int noOfCoins = random.nextInt(7) + 5;
            addCoins(angle, Math.random() * (Math.PI / 10), noOfCoins);
        }
        if(500 < n && n < 506)
            addBonus(angle);
    }

    public void addGhost(double angle) {
        if(unusedGhosts.children.size() > 0) {
            unusedGhosts.children.get(0).setRotation(0, 0, 1, (float) angle * radToDeg + 90);
            unusedGhosts.children.get(0).setPosition((float) (Math.cos(angle) * 1.8), (float) (Math.sin(angle) * 1.8), -20f);
            if(!unusedGhosts.children.get(0).getCollision(children.get(0)) && !unusedGhosts.children.get(0).getCollision(children.get(1)))
                usedGhosts.addChild(unusedGhosts.children.get(0));
            }
    }

    public void addCoins(double initAngle, double angle, int noOfCoins) {
        for (int i = 0; i < noOfCoins; i++) {
            if (unusedCoins.children.size() > 0) {
                unusedCoins.children.get(0).setRotation(0, 0, 1, (float) initAngle * radToDeg + 90);
                unusedCoins.children.get(0).setPosition((float) (Math.cos(initAngle) * 1.8), (float) (Math.sin(initAngle) * 1.8), -20f + i);
                if(!unusedCoins.children.get(0).getCollision(children.get(0)) && !unusedCoins.children.get(0).getCollision(children.get(1)))
                    usedCoins.addChild(unusedCoins.children.get(0));
                initAngle += angle;
            }
        }
    }

    public void addBonus(double angle) {
        if (unusedBonus.children.size() > 0) {
            System.out.println("spawna kukar");
            Random rand = new Random();
            int n = rand.nextInt(unusedBonus.children.size());
            unusedBonus.children.get(n).setRotation(0, 0, 1, (float) angle * radToDeg + 90);
            unusedBonus.children.get(n).setPosition((float) (Math.cos(angle) * 1.8), (float) (Math.sin(angle) * 1.8), -20f);
            if (!unusedBonus.children.get(n).getCollision(children.get(0)) && !unusedBonus.children.get(n).getCollision(children.get(1)))
                usedBonus.addChild(unusedBonus.children.get(n));
        }
    }
}
