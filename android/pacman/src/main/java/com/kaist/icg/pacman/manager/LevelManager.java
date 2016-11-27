package com.kaist.icg.pacman.manager;

/**
 * Manage level: spawn objects depending on the difficulty,
 * allocate/deallocate ground...
 */
public class LevelManager {

    private ParticleEmitter[] particleEmitters = new ParticleEmitter[30];
    private int first = 0;
    private int last = 0;

    //Singleton
    private static LevelManager INSTANCE;

    public static  LevelManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new LevelManager();

        return INSTANCE;
    }

    private LevelManager() {

    }

    public void update(long timeElapsed) {
        for (int i = first; i != last; i = (i + 1)%30){
            if (!particleEmitters[i].update(timeElapsed)){
                if (i == first) first = (first + 1)%30;
            }
        }
    }

    public void addParticleEmitter(float[] position){
        ParticleEmitter particleEmitter = new ParticleEmitter(position);
        particleEmitters[last] = particleEmitter;
        last = (last + 1)%30;
    }
}
