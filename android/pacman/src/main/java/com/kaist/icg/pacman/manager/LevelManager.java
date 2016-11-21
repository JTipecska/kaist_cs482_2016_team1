package com.kaist.icg.pacman.manager;

/**
 * Manage level: spawn objects depending on the difficulty,
 * allocate/deallocate ground...
 */
public class LevelManager {

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

    public void update(float timeElapsed) {

    }

    public void addPoint(){score++;}

    public int getScore(){
        return score;
    }
}
