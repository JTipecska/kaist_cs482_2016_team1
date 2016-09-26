package com.kaist.icg.pacman.manager;

/**
 * Manage level: spawn objects depending on the difficulty,
 * allocate/deallocate ground...
 */
public class LevelManager {

    //Singleton
    private static LevelManager INSTANCE;

    public static  LevelManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new LevelManager();

        return INSTANCE;
    }

    private LevelManager() {

    }

    public void update(float timeElapsed) {

    }
}
