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

    public TextureSlot getFreeTextureSlot() {
        for(Integer j : textureSlots.keySet()) {
            int[] materials = textureSlots.get(j);

            for(int i = 0; i<32; i++) {
                if(materials[i] == -1) {
                    return new TextureSlot(j, i);
                }
            }
        }

        if(textureSlots.size() < 32) {
            int[] materials = new int[32];
            int bloc = GLES20.GL_TEXTURE0 + textureSlots.size();
            textureSlots.put(bloc, materials);
            for(int i = 0; i<32; i++)
                materials[i] = -1;

            return new TextureSlot(bloc, 0);
        }

        throw new RuntimeException("Not enough texture slot. Please dispose old materials");
    }

    public int[] getTexturePack(int packIndex) {
        return textureSlots.get(packIndex);
    }

    public void cleanup() {
        for(Integer i : textureSlots.keySet()) {
            for (int j = 0; j < textureSlots.get(i).length; j++) {
                if(textureSlots.get(i)[j] != -1) {
                    GLES20.glDeleteTextures(1, textureSlots.get(i), j);
                    textureSlots.get(i)[j] = -1;
                }
            }
        }
    }

    public class TextureSlot {
        private int bloc;
        private int slot;

        public TextureSlot(int bloc, int slot) {
            this.bloc = bloc;
            this.slot = slot;
        }

        public int getBloc() {
            return bloc;
        }

        public void setBloc(int bloc) {
            this.bloc = bloc;
        }

        public int getSlot() {
            return slot;
        }

        public void setSlot(int slot) {
            this.slot = slot;
        }
    }
}
