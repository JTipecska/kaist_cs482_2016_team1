package com.kaist.icg.pacman.graphic;

import android.graphics.Bitmap;

import com.kaist.icg.pacman.tool.Material;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * OBJ file parser
 */
public class Object3D extends Drawable {
    protected float[] color;
    protected boolean hasTexture;
    protected boolean hasNormalmap = false;
    private int childIndexCounter;

    protected FloatBuffer tangentBuffer;
    protected FloatBuffer bitangentBuffer;

    public Object3D(int vertexBufferSize, FloatBuffer vertexBuffer,
                    FloatBuffer normalBuffer) {
        this.vertexBufferSize = vertexBufferSize;
        this.vertexBuffer = vertexBuffer;
        this.normalBuffer = normalBuffer;
        this.hasTexture = true;
        this.color = new float[] {(float) Math.random(), (float) Math.random(), (float) Math.random()};

        material = new Material(color);
    }

    public Object3D(int vertexBufferSize, FloatBuffer vertexBuffer,
                    FloatBuffer normalBuffer, FloatBuffer textureCoordinatesBuffer) {
        this.vertexBufferSize = vertexBufferSize;
        this.vertexBuffer = vertexBuffer;
        this.normalBuffer = normalBuffer;
        this.textureCoordinatesBuffer = textureCoordinatesBuffer;
        this.hasTexture = true;
        this.color = new float[] {(float) Math.random(), (float) Math.random(), (float) Math.random()};

        material = new Material(color);
    }

    public void setTextureFile(String fileName) {
        if(this.textureCoordinatesBuffer == null)
            throw new RuntimeException("Assigned texture file to a mesh without UV mapping information");

        material.setTexture(fileName);
    }

    public void setTexture(Bitmap bitmap) {
        if(this.textureCoordinatesBuffer == null)
            throw new RuntimeException("Assigned texture file to a mesh without UV mapping information");

        material.setTexture(bitmap);
    }

    public void setNormalmapFile(String filename) {
        if (this.textureCoordinatesBuffer == null)
            throw new RuntimeException("Assigned normal file to a mesh without UV mapping information");

        material.setNormalMap(filename);
        computeTangentBasis();
    }

    public void setNormalmap(Bitmap bitmap) {
        if(this.textureCoordinatesBuffer == null)
            throw new RuntimeException("Assigned normal file to a mesh without UV mapping information");

        material.setNormalMap(bitmap);
        computeTangentBasis();
    }

    public Object3D(int vertexBufferSize, FloatBuffer vertexBuffer,
                    FloatBuffer normalBuffer, FloatBuffer textureCoordinatesBuffer, Bitmap texture) {
        this.vertexBufferSize = vertexBufferSize;
        this.vertexBuffer = vertexBuffer;
        this.normalBuffer = normalBuffer;
        this.textureCoordinatesBuffer = textureCoordinatesBuffer;
        this.hasTexture = true;

        material = new Material(texture);
    }

    public Object3D(int vertexBufferSize, FloatBuffer vertexBuffer,
                    FloatBuffer normalBuffer, FloatBuffer textureCoordinatesBuffer, Bitmap texture,
                    Bitmap normalmap) {
        this.vertexBufferSize = vertexBufferSize;
        this.vertexBuffer = vertexBuffer;
        this.normalBuffer = normalBuffer;
        this.textureCoordinatesBuffer = textureCoordinatesBuffer;
        this.hasTexture = true;
        this.hasNormalmap = true;

        material = new Material(texture);
        material.setNormalMap(normalmap);
    }

    public void computeTangentBasis(){
        ByteBuffer bytebuf = ByteBuffer.allocateDirect(vertexBufferSize*3*4);
        bytebuf.order(ByteOrder.nativeOrder());
        tangentBuffer = bytebuf.asFloatBuffer();

        bytebuf = ByteBuffer.allocateDirect(vertexBufferSize*3*4);
        bytebuf.order(ByteOrder.nativeOrder());
        bitangentBuffer = bytebuf.asFloatBuffer();

        int j = 0;
        for (int i = 0; i < vertexBufferSize*3; i+=9){

            // get vertices and UVs
            float[] v0 = new float[3];
            v0[0] = vertexBuffer.get(i);
            v0[1] = vertexBuffer.get(i+1);
            v0[2] = vertexBuffer.get(i+2);
            float[] v1 = new float[3];
            v1[0] = vertexBuffer.get(i+3);
            v1[1] = vertexBuffer.get(i+4);
            v1[2] = vertexBuffer.get(i+5);
            float[] v2 = new float[3];
            v2[0] = vertexBuffer.get(i+6);
            v2[1] = vertexBuffer.get(i+7);
            v2[2] = vertexBuffer.get(i+8);

            float[] deltaPos1 = new float[3];
            deltaPos1[0] = v1[0]-v0[0];
            deltaPos1[1] = v1[1]-v0[1];
            deltaPos1[2] = v1[2]-v0[2];
            float[] deltaPos2 = new float[3];
            deltaPos2[0] = v2[0]-v0[0];
            deltaPos2[1] = v2[1]-v0[1];
            deltaPos2[2] = v2[2]-v0[2];

            float[] uv0 = new float[2];
            uv0[0] = textureCoordinatesBuffer.get(j);
            uv0[1] = textureCoordinatesBuffer.get(j+1);
            float[] uv1 = new float[2];
            uv1[0] = textureCoordinatesBuffer.get(j+2);
            uv1[1] = textureCoordinatesBuffer.get(j+3);
            float[] uv2 = new float[2];
            uv2[0] = textureCoordinatesBuffer.get(j+4);
            uv2[1] = textureCoordinatesBuffer.get(j+5);

            float[] deltaUV1 = new float[2];
            deltaUV1[0] = uv1[0]-uv0[0];
            deltaUV1[1] = uv1[1]-uv0[1];
            float[] deltaUV2 = new float[2];
            deltaUV2[0] = uv2[0]-uv0[0];
            deltaUV2[1] = uv2[1]-uv0[1];

            j += 6;

            float r = 1.0f / deltaUV1[0] * deltaUV2[1] - deltaUV1[1]*deltaUV2[0];
            float[] tangent = new float[3];
            tangent[0] = (deltaPos1[0]*deltaUV2[1] - deltaPos2[0]*deltaUV1[1])*r;
            tangent[1] = (deltaPos1[1]*deltaUV2[1] - deltaPos2[1]*deltaUV1[1])*r;
            tangent[2] = (deltaPos1[2]*deltaUV2[1] - deltaPos2[2]*deltaUV1[1])*r;

            float[] bitangent = new float[3];
            bitangent[0] = (deltaPos2[0]*deltaUV1[0] - deltaPos1[0]*deltaUV2[0])*r;
            bitangent[1] = (deltaPos2[1]*deltaUV1[0] - deltaPos1[1]*deltaUV2[0])*r;
            bitangent[2] = (deltaPos2[2]*deltaUV1[0] - deltaPos1[2]*deltaUV2[0])*r;

            tangentBuffer.put(tangent);
            tangentBuffer.put(tangent);
            tangentBuffer.put(tangent);
            bitangentBuffer.put(bitangent);
            bitangentBuffer.put(bitangent);
            bitangentBuffer.put(bitangent);
        }
    }

    /**
     * Draw the mesh on the current OpenGL context
     */
    @Override
    public void draw() {
        computeModelMatrix();

        shaderManager.draw(modelMatrix, vertexBuffer,
                normalBuffer, textureCoordinatesBuffer,
                tangentBuffer, bitangentBuffer, vertexBufferSize,
                material, shader);

        for(childIndexCounter = 0; childIndexCounter <children.size(); childIndexCounter++)
            children.get(childIndexCounter).draw();
    }

    public void setColor(float[] color) {
        this.color = color;
    }
}