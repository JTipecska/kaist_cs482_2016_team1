package com.kaist.icg.pacman.manager;

import android.opengl.GLES20;

import com.kaist.icg.pacman.graphic.Object3D;
import com.kaist.icg.pacman.graphic.Object3DFactory;
import com.kaist.icg.pacman.graphic.pipe.Pipe;
import com.kaist.icg.pacman.graphic.pipe.Scene;

import retrofit2.http.Part;

/**
 * Manage level: spawn objects depending on the difficulty,
 * allocate/deallocate ground...
 */
public class LevelManager {

    private static final int COINPARTICLE_NUM = 5;
    private static final float BONUS_TIME = 2000.0f;
    private static final float DARKMALUS_TIME = 1000.0f;

    private ParticleEmitter[] particleEmitters = new ParticleEmitter[COINPARTICLE_NUM];
    private ParticleEmitter doublePointsEmitter;

    private Scene scene;

    private int next = 0;
    private float doublePointsTimer = 0.0f;
    private float invincibleTimer = 0.0f;

    //Singleton
    private static LevelManager INSTANCE;
    private int score;
    private int life;
    private float darkMalus = 0.0f;
    private boolean bDarkMalus = false;

    private Object3D shield;
    private boolean invincible = false;

    public static  LevelManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new LevelManager();

        return INSTANCE;
    }

    private LevelManager() {
        score = 0;
        life = 3;
    }

    public void update(long timeElapsed) {
        for (int i = 0; i < COINPARTICLE_NUM; ++i){
            particleEmitters[i].update(timeElapsed);
        }
        doublePointsEmitter.update(timeElapsed);
        if (doublePointsEmitter.isActive())
            doublePointsTimer -= timeElapsed;
        if (doublePointsTimer < 0) {
            doublePointsEmitter.setActive(false);
            doublePointsTimer = 0.0f;
        }

        if (invincible)
            invincibleTimer -= timeElapsed;
        if (invincibleTimer < 0) {
            invincible = false;
            invincibleTimer = 0.0f;
        }

        if (bDarkMalus){
            darkMalus += timeElapsed;
            float darkMalusPerc = darkMalus/DARKMALUS_TIME;
            float[] colorLight = new float[] {202.0f/255.0f, 225.0f/255.0f, 255.0f/255.0f};
            float[] colorDark = new float[] {50.0f/255.0f, 50.0f/255.0f, 70.0f/255.0f};

            if (darkMalusPerc <= 1.0f) {
                for (int j = 0; j < Pipe.getNbPipePart(); ++j) {
                    scene.getRoot().getPipe().children.get(j).
                            getMaterial().setColor(new float[]{
                            colorLight[0] * (1 - darkMalusPerc) + colorDark[0] * (darkMalusPerc),
                            colorLight[1] * (1 - darkMalusPerc) + colorDark[1] * (darkMalusPerc),
                            colorLight[2] * (1 - darkMalusPerc) + colorDark[2] * (darkMalusPerc)});
                }
            } else if (darkMalusPerc >= 3.0f && darkMalusPerc <= 4.0f) {
                for (int j = 0; j < Pipe.getNbPipePart(); ++j) {
                    scene.getRoot().getPipe().children.get(j).
                            getMaterial().setColor(new float[]{
                            colorDark[0] * (1 - darkMalusPerc + 3.0f) + colorLight[0] * (darkMalusPerc - 3.0f),
                            colorDark[1] * (1 - darkMalusPerc + 3.0f) + colorLight[1] * (darkMalusPerc - 3.0f),
                            colorDark[2] * (1 - darkMalusPerc + 3.0f) + colorLight[2] * (darkMalusPerc - 3.0f)});
                }
            } else if (darkMalusPerc > 5.0f){
                for (int j = 0; j < Pipe.getNbPipePart(); ++j) {
                    scene.getRoot().getPipe().children.get(j).
                            getMaterial().setColor(colorLight);
                }
                darkMalus = 0.0f;
                bDarkMalus = false;
            }
        }
    }

    public void onRender() {
        if (invincible) {
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            GLES20.glEnable(GLES20.GL_BLEND);
            shield.draw();
            GLES20.glDisable(GLES20.GL_BLEND);
        }
    }

    public void addParticleEmitter(){
        particleEmitters[next].setActive(true);
        next = (next + 1)%(COINPARTICLE_NUM);
    }

    public void init(){
        float[] position = new float[] {-0.0f, -1.0f, 2.5f};
        doublePointsEmitter = new ParticleEmitter(position,
                ParticleEmitter.ParticleType.DOUBLEPOINTS);

        position = new float[] {2.0f, -1.0f, 2.0f};
        for (int i = 0; i < COINPARTICLE_NUM; ++i){
            particleEmitters[i] = new ParticleEmitter(position,
                    ParticleEmitter.ParticleType.COIN);
        }

        shield = Object3DFactory.getInstance().instanciate("objects/ui.obj", Object3D.class);
        shield.setShader(ShaderManager.Shader.DIFFUSETEX);
        shield.setTextureFile("bubble.png");
        shield.setPosition(-0.5f, -0.2f, 2.8f);
    }

    public void activateDoublePoints() {
        doublePointsEmitter.setActive(true);
        doublePointsTimer += BONUS_TIME;
    }

    public void addPoint(){
        if (doublePointsEmitter.isActive())
            score++;
        score++;
    }

    public void reduceLife () {
        if (!invincible) life--;
    }

    public int getLife() {
        return life;
    }

    public int getScore(){
        return score;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void activateInvincible() {
        this.invincible = true;
        invincibleTimer += BONUS_TIME;
    }

    public void activateDarkMalus(){
        bDarkMalus = true;

    }
}
