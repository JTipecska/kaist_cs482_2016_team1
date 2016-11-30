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

    private HashMap<Integer, TextureBloc> textureBlocs;

    private TextureManager() {
        textureBlocs = new HashMap<>();
    }

    public TextureInfo getTextureSlotFor(Bitmap texture) {
        for(Integer j : textureBlocs.keySet()) {
            TextureBloc textureBloc = textureBlocs.get(j);
            for(int i = 0; i<32; i++) {
                boolean isFree = textureBloc.getTextureHandlers()[i] == -1;
                boolean isRecycled = textureBloc.getTextures()[i] != null && textureBloc.getTextures()[i].isRecycled();
                boolean isSameTexture = !isRecycled && (textureBloc.getTextures()[i] != null && textureBloc.getTextures()[i].sameAs(texture));
                if(isRecycled && !isFree) {
                    textureBloc.getTextureHandlers()[i] = -1;
                    textureBloc.getTextures()[i] = null;
                    isFree = true;
                }
                if(isFree || isSameTexture) {
                    textureBloc.getTextures()[i] = texture;
                    return new TextureInfo(j, i, textureBloc.getTextures()[i]);
                }
            }
        }

        if(textureBlocs.size() < 32) {
            System.out.println("Create new texture bloc");
            TextureBloc textureBloc = new TextureBloc();
            int bloc = GLES20.GL_TEXTURE0 + textureBlocs.size();
            textureBlocs.put(bloc, textureBloc);
            textureBloc.getTextures()[0] = texture;

            return new TextureInfo(bloc, 0, texture);
        }

        throw new RuntimeException("Not enough texture slot. Please dispose old materials");
    }

    public int[] getTextureHandlerArrayFromTextureInfo(TextureInfo ti) {
        return textureBlocs.get(ti.getBloc()).getTextureHandlers();
    }

    public void cleanupTexture(TextureInfo ti) {
        if(textureBlocs.get(ti.getBloc()).getTextures()[ti.getSlot()] != null) {
            GLES20.glDeleteTextures(1, textureBlocs.get(ti.getBloc()).getTextureHandlers(), ti.getSlot());
            textureBlocs.get(ti.getBloc()).getTextureHandlers()[ti.getSlot()] = -1;
            textureBlocs.get(ti.getBloc()).getTextures()[ti.getSlot()].recycle();
            textureBlocs.get(ti.getBloc()).getTextures()[ti.getSlot()] = null;
        }
    }

    public void cleanup() {
        for(Integer i : textureBlocs.keySet()) {
            for (int j = 0; j < textureBlocs.get(i).getTextureHandlers().length; j++) {
                if(textureBlocs.get(i).getTextureHandlers()[j] != -1) {
                    GLES20.glDeleteTextures(1, textureBlocs.get(i).getTextureHandlers(), j);
                    textureBlocs.get(i).getTextureHandlers()[j] = -1;
                    textureBlocs.get(i).getTextures()[j].recycle();
                    textureBlocs.get(i).getTextures()[j] = null;
                }
            }
        }
    }

    public static boolean sameTexture(Bitmap i1, Bitmap i2) {
        if(i1.getWidth() != i2.getWidth() || i1.getHeight() != i2.getHeight())
            return false;

        for(int x = 0; x<i1.getWidth(); x++) {
            for (int y = 0; y < i1.getHeight(); y++) {
                if(i1.getPixel(x, y) != i2.getPixel(x, y))
                    return false;
            }
        }

        return true;
    }

    public class TextureBloc {
        private int blocIndex;
        private int[] textureHandlers;
        private Bitmap[] textures;

        public TextureBloc() {
            textureHandlers = new int[32];
            for (int i = 0; i < textureHandlers.length; i++) {
                textureHandlers[i] = -1;
            }
            textures = new Bitmap[32];
        }

        public int getBlocIndex() {
            return blocIndex;
        }

        public void setBlocIndex(int blocIndex) {
            this.blocIndex = blocIndex;
        }

        public int[] getTextureHandlers() {
            return textureHandlers;
        }

        public void setTextureHandlers(int[] textureHandlers) {
            this.textureHandlers = textureHandlers;
        }

        public Bitmap[] getTextures() {
            return textures;
        }

        public void setTextures(Bitmap[] textures) {
            this.textures = textures;
        }
    }

    public class TextureInfo {
        private int bloc;
        private int slot;
        private Bitmap texture;

        public TextureInfo(int bloc, int slot, Bitmap texture) {
            this.bloc = bloc;
            this.slot = slot;
            this.texture = texture;
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

        public Bitmap getTexture() {
            return texture;
        }

        public void setTexture(Bitmap texture) {
            this.texture = texture;
        }
    }
}
