package com.kaist.icg.pacman.manager;

import android.opengl.GLES20;
import android.os.Handler;
import android.os.Looper;

import com.kaist.icg.pacman.graphic.Object3D;
import com.kaist.icg.pacman.graphic.Object3DFactory;
import com.kaist.icg.pacman.graphic.pipe.Pipe;
import com.kaist.icg.pacman.graphic.pipe.Scene;
import com.kaist.icg.pacman.view.GameView;

import static java.lang.Math.floor;

/**
 * Manage level: spawn objects depending on the difficulty,
 * allocate/deallocate ground...
 */
public class LevelManager {

    private static final int COINPARTICLE_NUM = 5;
    private static final int BONUS_TIME = 8000;
    private static final int DARKMALUS_FADETIME = 1000;
    private static final int REVERSE_TIME = 5000;
    private static final int DEAD_INVINCIBLE_TIME = 2000;

    //Singleton
    private static LevelManager INSTANCE;

    private int score;
    private int life;

    // variables for bonus and malus effects
    private Scene scene;
    private ParticleEmitter[] particleEmitters = new ParticleEmitter[COINPARTICLE_NUM];
    private ParticleEmitter doublePointsEmitter;
    private int next = 0;
    private float doublePointsTimer = 0.0f;
    private float invincibleTimer = 0.0f;
    private float darkMalus = 0.0f;
    private boolean bDarkMalus = false;
    private Object3D shield;
    private boolean invincible = false;
    private boolean deadInvincible = false;
    private float deadInvincibleTimer = 0.0f;
    private float reverseTimer = 0.0f;
    private Handler handler;
    private Runnable stopReverseInput;
    private GameView currentGameView;

    public static  LevelManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new LevelManager();

        return INSTANCE;
    }

    private LevelManager() {
        Looper.prepare();
        handler = new Handler();
        stopReverseInput = new Runnable() {
            @Override
            public void run() {
                InputManager.getInstance().setReverse(false);
            }
        };
    }

    public void update(long timeElapsed) {
        for (int i = 0; i < COINPARTICLE_NUM; ++i){
            particleEmitters[i].update(timeElapsed);
        }

        if (InputManager.getInstance().isReverse()) {
            reverseTimer -= timeElapsed;
            if (reverseTimer < 0){
                InputManager.getInstance().setReverse(false);
                reverseTimer = 0.0f;
            }
        }

        if (doublePointsEmitter.isActive()) {
            doublePointsEmitter.update(timeElapsed);
            doublePointsTimer -= timeElapsed;
            if (doublePointsTimer < 0) {
                doublePointsEmitter.setActive(false);
                doublePointsTimer = 0.0f;
            }
        }

        if (invincible) {
            invincibleTimer -= timeElapsed;
            if (invincibleTimer < 0) {
                invincible = false;
                invincibleTimer = 0.0f;
            }
        }

        if (deadInvincible) {
            deadInvincibleTimer -= timeElapsed;
            // blink Pacman to indicate invincible due to recent life loss
            if ((floor(deadInvincibleTimer/DEAD_INVINCIBLE_TIME * 12)) % 2 == 1){
                scene.getPacman().setDraw(false);
            } else {
                scene.getPacman().setDraw(true);
            }
            if (deadInvincibleTimer < 0) {
                deadInvincible = false;
                invincibleTimer = 0.0f;
                scene.getPacman().setDraw(true);
            }
        }

        if (bDarkMalus){
            fadeDarkness(timeElapsed);
        }
    }

    public void onRender() {
        for (int i = 0; i < COINPARTICLE_NUM; ++i){
            particleEmitters[i].Render();
        }
        doublePointsEmitter.Render();
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
        float[] position = new float[] {-0.0f, -1.0f, 2.9f};
        doublePointsEmitter = new ParticleEmitter(position,
                ParticleEmitter.ParticleType.DOUBLEPOINTS);

        position = new float[] {0.0f, -1.0f, 2.0f};
        for (int i = 0; i < COINPARTICLE_NUM; ++i){
            particleEmitters[i] = new ParticleEmitter(position,
                    ParticleEmitter.ParticleType.COIN);
        }

        shield = Object3DFactory.getInstance().instanciate("objects/ui.obj", Object3D.class);
        shield.setShader(ShaderManager.Shader.DIFFUSETEX);
        shield.setTextureFile("bubble.png");
        shield.setPosition(-0.5f, -0.2f, 2.8f);

        doublePointsTimer = 0.0f;
        invincibleTimer = 0.0f;
        score = 0;
        life = 3;
        darkMalus = 0.0f;
        bDarkMalus = false;
        invincible = false;
        deadInvincible = false;
        deadInvincibleTimer = 0.0f;
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
        if (!invincible && !deadInvincible) {
            if (life <= 0) {
                currentGameView.gameOver();
            }
            life--;
            deadInvincible = true;
            deadInvincibleTimer = DEAD_INVINCIBLE_TIME;
        }
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

    private void fadeDarkness(long timeElapsed){
        darkMalus += timeElapsed;
        float darkMalusPerc = darkMalus/DARKMALUS_FADETIME;
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
        } else if (darkMalusPerc >= 8.0f && darkMalusPerc <= 9.0f) {
            for (int j = 0; j < Pipe.getNbPipePart(); ++j) {
                scene.getRoot().getPipe().children.get(j).
                        getMaterial().setColor(new float[]{
                        colorDark[0] * (1 - darkMalusPerc + 8.0f) + colorLight[0] * (darkMalusPerc - 8.0f),
                        colorDark[1] * (1 - darkMalusPerc + 8.0f) + colorLight[1] * (darkMalusPerc - 8.0f),
                        colorDark[2] * (1 - darkMalusPerc + 8.0f) + colorLight[2] * (darkMalusPerc - 8.0f)});
            }
        } else if (darkMalusPerc > 9.0f){
            for (int j = 0; j < Pipe.getNbPipePart(); ++j) {
                scene.getRoot().getPipe().children.get(j).
                        getMaterial().setColor(colorLight);
            }
            darkMalus = 0.0f;
            bDarkMalus = false;
        }
    }

    public void activateReverseMalus() {
        InputManager.getInstance().setReverse(true);

        if(InputManager.getInstance().isReverse())
            handler.removeCallbacks(stopReverseInput);

        handler.postDelayed(stopReverseInput, REVERSE_TIME);
        reverseTimer += REVERSE_TIME;
    }

    public void setCurrentGameView(GameView currentGameView) {
        this.currentGameView = currentGameView;
    }
}
