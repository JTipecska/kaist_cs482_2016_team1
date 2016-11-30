package com.kaist.icg.pacman.manager;

import retrofit2.http.Part;

/**
 * Manage level: spawn objects depending on the difficulty,
 * allocate/deallocate ground...
 */
public class LevelManager {

    private static final int COINPARTICLE_NUM = 5;
    private static final float DOUBLEPOINTS_TIME = 2000.0f;

    private ParticleEmitter[] particleEmitters = new ParticleEmitter[COINPARTICLE_NUM];
    private ParticleEmitter doublePointsEmitter;

    private int next = 0;
    private float doublePoints = DOUBLEPOINTS_TIME;

    //Singleton
    private static LevelManager INSTANCE;
    private int score;

    public static  LevelManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new LevelManager();

        return INSTANCE;
    }

    private LevelManager() {
        score = 0;
    }

    public void update(long timeElapsed) {
        for (int i = 0; i < COINPARTICLE_NUM; ++i){
            particleEmitters[i].update(timeElapsed);
        }
        doublePointsEmitter.update(timeElapsed);
        if (doublePointsEmitter.isActive())
            doublePoints -= timeElapsed;
        if (doublePoints < 0) {
            doublePointsEmitter.setActive(false);
            doublePoints = 0.0f;
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
    }

    public void setDoublePointsActive() {
        doublePointsEmitter.setActive(true);
        doublePoints += DOUBLEPOINTS_TIME;
    }

    public void addPoint(){
        if (doublePointsEmitter.isActive())
            score++;
        score++;
    }

    public int getScore(){
        return score;
    }
}
