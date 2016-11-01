package com.kaist.icg.pacman.manager;

import android.opengl.GLES20;

import java.util.HashMap;

public class TextureManager {
    private static TextureManager INSTANCE;

    public static TextureManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new TextureManager();
        return INSTANCE;
    }

    private HashMap<Integer, int[]> textureSlots;

    private TextureManager() {
        textureSlots = new HashMap<>();
    }

    public int getFreeTextureSlot(int packIndex) {
        int[] materials = textureSlots.get(packIndex);

        if(materials == null) {
            materials = new int[32];
            textureSlots.put(packIndex, materials);
            for(int i = 0; i<32; i++)
                materials[i] = -1;
        }

        for(int i = 0; i<32; i++) {
            if(materials[i] == -1) {
                return i;
            }
        }

        throw new RuntimeException("Not enough texture slot. Please dispose old materials");
    }

    public int[] getTexturePack(int packIndex) {
        return textureSlots.get(packIndex);
    }

    public void cleanup() {
        for(int i = 0; i<textureSlots.size(); i++) {
            if(textureSlots.get(i) != null) {
                for (int j = 0; j < textureSlots.get(i).length; j++) {
                    if(textureSlots.get(i)[j] != -1) {
                        GLES20.glDeleteTextures(1, textureSlots.get(i), j);
                        textureSlots.get(i)[j] = -1;
                    }
                }
            }
        }
    }
}
