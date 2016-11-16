package com.kaist.icg.pacman.manager;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.util.HashMap;

public class TextureManager {
    private static TextureManager INSTANCE;

    public static TextureManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new TextureManager();
        return INSTANCE;
    }

    private HashMap<Integer, TextureInfo[]> textureSlots;

    private TextureManager() {
        textureSlots = new HashMap<>();
    }

    public TextureMemoryInfo getTextureSlotFor(Bitmap texture) {
        for(Integer j : textureSlots.keySet()) {
            TextureInfo[] textures = textureSlots.get(j);
            for(int i = 0; i<32; i++) {
                if(textures[i].getHandler() == -1 ||
                        (textures[i].getBitmap() != null && textures[i].getBitmap().sameAs(texture))) {
                    if(textures[i].getBitmap() != null && textures[i].getBitmap().sameAs(texture))
                        System.out.println("Use existing texture slot");
                    else
                        System.out.println("Use new texture slot");
                    return new TextureMemoryInfo(j, i);
                }
            }
        }

        if(textureSlots.size() < 32) {
            System.out.println("Create new texture bloc");
            TextureInfo[] materials = new TextureInfo[32];
            int bloc = GLES20.GL_TEXTURE0 + textureSlots.size();
            textureSlots.put(bloc, materials);
            for(int i = 0; i<32; i++) {
                materials[i] = new TextureInfo();
            }

            return new TextureMemoryInfo(bloc, 0);
        }

        throw new RuntimeException("Not enough texture slot. Please dispose old materials");
    }

    public TextureInfo[] getTextureInfoForBloc(int bloc) {
        return textureSlots.get(bloc);
    }

    public TextureInfo getTextureInfoFromMemoryInfo(TextureMemoryInfo info) {
        return textureSlots.get(info.getBloc())[info.getSlot()];
    }

    public void cleanup() {
        for(Integer i : textureSlots.keySet()) {
            for (int j = 0; j < textureSlots.get(i).length; j++) {
                if(textureSlots.get(i)[j].getSlot() != -1) {
                    GLES20.glDeleteTextures(1, textureSlots.get(i)[j].getHandlerArray(), 0);
                    textureSlots.get(i)[j].setSlot(-1);
                }
            }
        }
    }

    public class TextureMemoryInfo {
        private int bloc;
        private int slot;

        public TextureMemoryInfo(int bloc, int slot) {
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

    public class TextureInfo {
        private Bitmap bitmap;
        private int slot;
        private int[] handlerArray;

        public TextureInfo() {
            this.slot = -1;
            handlerArray = new int[] {-1};
        }

        public TextureInfo(Bitmap bitmap, int slot) {
            this.bitmap = bitmap;
            this.slot = slot;
            handlerArray = new int[] {-1};
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public int getSlot() {
            return slot;
        }

        public void setSlot(int slot) {
            this.slot = slot;
        }

        public int[] getHandlerArray() {
            return handlerArray;
        }

        public int getHandler() {
            return handlerArray[0];
        }

        public void setHandler(int h) {
            handlerArray[0] = h;
        }
    }
}
