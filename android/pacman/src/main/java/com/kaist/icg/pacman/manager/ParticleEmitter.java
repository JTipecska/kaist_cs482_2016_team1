package com.kaist.icg.pacman.manager;

import android.opengl.GLES20;

import com.kaist.icg.pacman.graphic.Drawable;
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
    public static final int MAX_PARTICLENUM = 1;

    class Particle
    {
        public float[] velocity = {0.0f, 0.0f, 0.0f};
        public float[] force = {0.0f, 0.0f, 0.0f};
        public float lifetime = 0.0f;
        public Coin coin;
    };

    public static float gravity = 9.8f;
    private float maxLifetime = 200.0f; //seconds

    private float[] position = new float[3];

    public Particle[] particles = new Particle[MAX_PARTICLENUM];
    private int currentParticleNum = 0;
    private int activeParticles = 0;
    private int min = 1;
    private int max = 5;
    private float spawnTime = 20.0f;
    private float emitterLifeTime = maxLifetime + spawnTime*10 + 1.0f;

    public ParticleEmitter(float[] position){
        this.position = position;
    }

    public boolean update(long elapsedTime) {
        if (emitterLifeTime < 0) {
            return false;
        } else {
            SpawnParticles(elapsedTime);
            UpdateParticles(elapsedTime);

            Render();
            return true;
        }
    }

    public void SpawnParticles(long elapsedTime){
        Random rand = new Random();
        int spawnCount = rand.nextInt((max - min) + 1) + min;
        spawnTime -= elapsedTime;
        emitterLifeTime -= elapsedTime;
        if (spawnTime < 0) {
            spawnTime = 20.0f;
            return;
        }

        for (int i = 0; i < spawnCount; ++i){
            if (currentParticleNum < MAX_PARTICLENUM) {
                Particle particle = new Particle();

                particle.force = new float[]{
                        rand.nextFloat()*2-1,
                        5.0f,
                        0.0f};


                Material gold = new Material();
                gold.setAmbientIntensity(
                        (0.212671f*0.24725f + 0.715160f*0.1995f + 0.072169f*0.0745f)/
                                (0.212671f*0.75164f + 0.715160f*0.60648f + 0.072169f*0.22648f));
                gold.setDiffuseColor(new float[] {0.75164f,0.60648f,0.22648f});
                gold.setSpecularColor(new float[] {0.628281f, 0.555802f, 0.366065f});
                gold.setShininess(0.005f);

                Coin coin = Object3DFactory.getInstance().instanciate("objects/Coin.obj", Coin.class);
                coin.setMaterial(gold);
                coin.setShader(ShaderManager.Shader.PHONG);
                //coin.setScale(0.5f, 0.5f, 0.5f);

                particle.coin = coin;
                coin.setPosition(position[0], position[1], position[2]);

                particles[currentParticleNum] = particle;
                ++currentParticleNum;
            }
        }

    }

    public void UpdateParticles(long elapsedTime){
        activeParticles = 0;
        for (int i = 0; i < currentParticleNum; ++i){
            if (particles[i].lifetime < maxLifetime){
                particles[i].lifetime = particles[i].lifetime + elapsedTime;
                ++activeParticles;

                particles[i].force = new float[] {particles[i].force[0],
                        particles[i].force[1] - gravity*elapsedTime/200.0f,
                        particles[i].force[2]};
                particles[i].velocity = new float[] {
                        particles[i].velocity[0] + particles[i].force[0]*elapsedTime/200.0f,
                        particles[i].velocity[1] + particles[i].force[1]*elapsedTime/200.0f,
                        particles[i].velocity[2] + particles[i].force[2]*elapsedTime/200.0f};
                particles[i].coin.translate(
                        particles[i].velocity[0]*elapsedTime/200.0f,
                        particles[i].velocity[1]*elapsedTime/200.0f,
                        particles[i].velocity[2]*elapsedTime/200.0f);
            }
        }
    }

    public void Render(){
        for (int i = 0; i < currentParticleNum; ++i){
            if (particles[i].lifetime < maxLifetime){
                particles[i].coin.draw();
            }
        }

        // create vertexbuffer out of particles
        /*FloatBuffer particleBuffer;
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(activeParticles * 3 * 3 * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        particleBuffer = byteBuf.asFloatBuffer();
        for (int i = 0; i < currentParticleNum; ++i){
            if (particles[i].lifetime < maxLifetime){
                particleBuffer.put(particles[i].position[0]);
                particleBuffer.put(particles[i].position[1]);
                particleBuffer.put(particles[i].position[2]);
            }
        }

        // attach shaders for particles
        int vertexShader = PacManGLRenderer.loadShaderFromFile(
                GLES20.GL_VERTEX_SHADER, "shader/particle.vshader");
        int fragmentShader = PacManGLRenderer.loadShaderFromFile(
                GLES20.GL_FRAGMENT_SHADER, "shader/particle.fshader");

        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);

        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);

        //Retrieve attributes handlers
        int positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        //Set attributes
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(
                positionHandle, 3,
                GLES20.GL_FLOAT, false,
                3*4, particleBuffer);
        // draw GL_POINT
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, activeParticles);

        // end draw
        GLES20.glDisableVertexAttribArray(positionHandle);*/
    }
}
