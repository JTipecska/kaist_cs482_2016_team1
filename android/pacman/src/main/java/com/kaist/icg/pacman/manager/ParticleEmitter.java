package com.kaist.icg.pacman.manager;

import android.opengl.GLES20;

import com.kaist.icg.pacman.graphic.Drawable;
import com.kaist.icg.pacman.graphic.Object3D;
import com.kaist.icg.pacman.graphic.Object3DFactory;
import com.kaist.icg.pacman.graphic.android.PacManGLRenderer;
import com.kaist.icg.pacman.graphic.pipe.Coin;
import com.kaist.icg.pacman.tool.Material;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

/**
 * Created by Lou on 26.11.2016.
 */



public class ParticleEmitter {
    //currently only 1 Coin will pop up. but modifiable to spawn several "particles"
    public int MAX_PARTICLENUM = 30;

    public enum ParticleType {
        COIN, DOUBLEPOINTS
    }

    class Particle
    {
        public float[] velocity = {0.0f, 0.0f, 0.0f};
        public float[] force = {0.0f, 0.0f, 0.0f};
        public float lifetime = 0.0f;
        public Object3D mesh;
    };

    public static float gravity = 9.8f;
    private float maxLifetime = 300.0f; //seconds

    private float[] position = new float[3];
    private ParticleType type = ParticleType.COIN;

    public Particle[] particles = new Particle[MAX_PARTICLENUM];
    private int currentParticleNum = 0;
    private boolean active = false;
    private float xForceMul = 2.0f;
    private float yForceMul = 0.0f;
    private float xForceAdd = -1.0f;
    private float yForceAdd = 5.0f;

    public ParticleEmitter(float[] position, ParticleType type){
        this.position = position;
        this.type = type;
        initializeParticles();
        switch (type) {
            case COIN:
                xForceMul = 2.0f;
                yForceMul = 0.0f;
                xForceAdd = -1.0f;
                yForceAdd = 5.0f;
                break;
            case DOUBLEPOINTS:
                xForceMul = 2.0f;
                yForceMul = 4.0f;
                xForceAdd = -1.0f;
                yForceAdd = 1.0f;
                break;
        }
    }

    public void update(long elapsedTime) {
        if (active) {
            UpdateParticles(elapsedTime);
        }
    }

    public void initializeParticles(){
        Random rand = new Random();
        Particle particle;
        Material material;
        Object3D mesh;
        switch(type) {
            case COIN:
                particle = new Particle();
                particle.force = new float[]{
                        rand.nextFloat() * xForceMul + xForceAdd,
                        rand.nextFloat() * yForceMul + yForceAdd,
                        0.0f};
                material = new Material();

                mesh = Object3DFactory.getInstance().instanciate("objects/Coin.obj", Coin.class);

                        material.setAmbientIntensity(
                                (0.212671f * 0.24725f + 0.715160f * 0.1995f + 0.072169f * 0.0745f) /
                                        (0.212671f * 0.75164f + 0.715160f * 0.60648f + 0.072169f * 0.22648f));
                        material.setDiffuseColor(new float[]{0.75164f, 0.60648f, 0.22648f});
                        material.setSpecularColor(new float[]{0.628281f, 0.555802f, 0.366065f});
                        material.setShininess(8.0f);
                        mesh.setMaterial(material);
                        mesh.setShader(ShaderManager.Shader.PHONG);

                mesh.setPosition(position[0]-2, position[1], position[2]);
                particle.mesh = mesh;

                particles[0] = particle;
                currentParticleNum = 1;
                break;
            case DOUBLEPOINTS:
                for (int i = 0; i < MAX_PARTICLENUM; ++i){
                    particle = new Particle();
                    particle.force = new float[]{
                            rand.nextFloat() * xForceMul + xForceAdd,
                            rand.nextFloat() * yForceMul + yForceAdd,
                            0.0f};
                    material = new Material();
                    mesh = Object3DFactory.getInstance().instanciate("objects/Star.obj", Object3D.class);
                    material.setSpecularColor(new float[]{0.628281f, 0.555802f, 0.366065f});
                    material.setShininess(0.001f);
                    mesh.setMaterial(material);
                    mesh.setTextureFile("star4.png");
                    mesh.setShader(ShaderManager.Shader.PHONGTEX);

                    mesh.setPosition(position[0]+(rand.nextInt(2)-rand.nextFloat())*i%2, position[1], position[2]);
                    particle.mesh = mesh;

                    particles[i] = particle;
                    currentParticleNum = i+1;
                }
                break;
        }

    }

    public void UpdateParticles(long elapsedTime){
        for (int i = 0; i < currentParticleNum; ++i){
            particles[i].lifetime = particles[i].lifetime + elapsedTime;
            if (particles[i].lifetime < maxLifetime){
                particles[i].force = new float[] {particles[i].force[0],
                        particles[i].force[1] - gravity*elapsedTime/200.0f,
                        particles[i].force[2]};
                particles[i].velocity = new float[] {
                        particles[i].velocity[0] + particles[i].force[0]*elapsedTime/200.0f,
                        particles[i].velocity[1] + particles[i].force[1]*elapsedTime/200.0f,
                        particles[i].velocity[2] + particles[i].force[2]*elapsedTime/200.0f};
                particles[i].mesh.translate(
                        particles[i].velocity[0]*elapsedTime/200.0f,
                        particles[i].velocity[1]*elapsedTime/200.0f,
                        particles[i].velocity[2]*elapsedTime/200.0f);
            } else if (type == ParticleType.DOUBLEPOINTS) {
               reset(i);

            }
        }
    }

    public void Render(){
        if (active) {
            if (type == ParticleType.DOUBLEPOINTS) {
                GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
                GLES20.glEnable(GLES20.GL_BLEND);

            }

            boolean active = false;
            for (int i = 0; i < currentParticleNum; ++i) {
                if (particles[i].lifetime < maxLifetime) {
                    particles[i].mesh.draw();
                    active = true;
                }
            }

            this.active = active;

            if (type == ParticleType.DOUBLEPOINTS) {
                GLES20.glDisable(GLES20.GL_BLEND);
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        for (int i = 0; i < currentParticleNum; ++i) {
            reset(i);
        }
        this.active = active;

    }

    private void reset(int i){
        Random rand = new Random();
        particles[i].mesh.setPosition(position[0]+(rand.nextInt(2)-rand.nextFloat())*i%2, position[1], position[2]);
        particles[i].force = new float[]{
                rand.nextFloat() * xForceMul + xForceAdd,
                rand.nextFloat() * yForceMul + yForceAdd,
                0.0f};
        particles[i].velocity = new float[]{0.0f, 0.0f, 0.0f};
        particles[i].lifetime = 0;

    }
}
