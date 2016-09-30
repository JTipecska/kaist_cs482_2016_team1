package com.kaist.icg.pacman.graphic;

import android.opengl.GLES20;
import android.os.SystemClock;
import android.util.Log;

import com.kaist.icg.pacman.graphic.android.PacManActivity;
import com.kaist.icg.pacman.graphic.android.PacManGLRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * OBJ file parser
 */
public class Object3D extends Drawable {
    private int nbVertices;
    private float color[] = { 0.5f, 0.5f, 1f };

    private ArrayList<float[]> verticesDictionary;
    private ArrayList<float[]> normalsDictionary;
    private ArrayList<Face> facesDictionary;

    private int lightHandle;
    private int colorHandle;
    private int ambientHandle;
    private int diffuseHandle;
    private int specularHandle;
    private int materialHandle;

    private int attenuationConstHandle;
    private int attenuationLinearHandle;
    private int attenuationExponentialHandle;

    private float[] light = new float[3];

    private float[] ambient = {0.1f, 0.1f, 0.1f};
    private float[] diffuse = {0.5f, 0.5f, 0.5f};
    private float[] specular = {1.0f, 1.0f, 1.0f};
    private float material = 2.0f; // shininess

    private float attenuationConst = 1.0f; //should be set to 1
    private float attenuationLinear = 0.1f; //smaller than 1
    private float attenuationExponential = 0.2f; //smaller than 1


    public Object3D(String file) {
        verticesDictionary = new ArrayList<>();
        normalsDictionary = new ArrayList<>();
        facesDictionary = new ArrayList<>();

        loadFile(file);
        buildBuffers();
        Log.d("Object3D", "[" + file + "] " + nbVertices + " vertices");

        // prepare shaders and OpenGL program
        int vertexShader = PacManGLRenderer.loadShaderFromFile(
                GLES20.GL_VERTEX_SHADER, "basic-gl2.vshader"); //default: basic-gl2.vshader
        int outlinefShader = PacManGLRenderer.loadShaderFromFile(
                GLES20.GL_FRAGMENT_SHADER, "outline-gl2.fshader"); //default: diffuse-gl2.fshader

        programOutline = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(programOutline, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(programOutline, outlinefShader); // add the fragment shader to program
        GLES20.glLinkProgram(programOutline);                  // create OpenGL program executables

        //Set light position
        light = new float[] {0.0f, 0.0f, -2.0f}; //default: 2, 3, 14


        int fragmentShader = PacManGLRenderer.loadShaderFromFile(
                GLES20.GL_FRAGMENT_SHADER, "toon-gl2.fshader");

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
    }

    /**
     * Load OBJ file and build the model
     * @param file File path
     */
    private void loadFile(String file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    PacManActivity.context.getAssets().open(file)));

            String line;
            do {
                line = reader.readLine();
                if(line != null) {
                    if (line.startsWith("v ")) { //vertex
                        String[] split = line.split(" ");
                        float[] vertex = new float[3];
                        vertex[0] = Float.parseFloat(split[1]);
                        vertex[1] = Float.parseFloat(split[2]);
                        vertex[2] = Float.parseFloat(split[3]);

                        verticesDictionary.add(vertex);
                    } else if (line.startsWith("vn ")) { //normal
                        String[] split = line.split(" ");
                        float[] normal = new float[3];
                        normal[0] = Float.parseFloat(split[1]);
                        normal[1] = Float.parseFloat(split[2]);
                        normal[2] = Float.parseFloat(split[3]);

                        normalsDictionary.add(normal);
                    } else if (line.charAt(0) == 'f') { //face
                        facesDictionary.add(new Face(line));
                    }
                }
            } while (line != null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Build vertices and normals buffer (FloatBuffer) from the model
     */
    private void buildBuffers() {
        nbVertices = 0;

        ByteBuffer byteBuf = ByteBuffer.allocateDirect(facesDictionary.size() * 3 * 3 * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuf.asFloatBuffer();

        for(Face face : facesDictionary) {
            for(int i = 0; i<face.getVerticesIndex().length; i++) {
                vertexBuffer.put(verticesDictionary.get(face.getVerticesIndex()[i]));
                nbVertices++;
            }
        }
        vertexBuffer.position(0);

        byteBuf = ByteBuffer.allocateDirect(facesDictionary.size() * 3 * 3 * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        normalBuffer = byteBuf.asFloatBuffer();

        for(Face face : facesDictionary) {
            for(int i = 0; i<face.getNormalsIndex().length; i++) {
                normalBuffer.put(normalsDictionary.get(face.getNormalsIndex()[i]));
            }
        }
        normalBuffer.position(0);
    }

    /**
     * Draw the mesh on the current OpenGL context
     * @param projectionMatrix
     * @param viewMatrix
     */
    @Override
    public void drawOutline(float[] projectionMatrix, float[] viewMatrix) {
        GLES20.glUseProgram(programOutline);
        prepareDraw(projectionMatrix, viewMatrix);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, facesDictionary.size() * 3);

        endDraw();
    }

    @Override
    public void draw(float[] projectionMatrix, float[] viewMatrix) {
        GLES20.glUseProgram(program);

        prepareDraw(projectionMatrix, viewMatrix);

        colorHandle = GLES20.glGetUniformLocation(program, "uColor");
        lightHandle = GLES20.glGetUniformLocation(program, "uLight");
        ambientHandle = GLES20.glGetUniformLocation(program, "uAmbient");
        diffuseHandle = GLES20.glGetUniformLocation(program, "uDiffuse");
        specularHandle = GLES20.glGetUniformLocation(program, "uSpecular");
        materialHandle = GLES20.glGetUniformLocation(program, "uMaterial");
        attenuationConstHandle = GLES20.glGetUniformLocation(program, "uAttConst");
        attenuationLinearHandle = GLES20.glGetUniformLocation(program, "uAttLin");
        attenuationExponentialHandle = GLES20.glGetUniformLocation(program, "uAttExp");

        GLES20.glUniform3fv(colorHandle, 1, color, 0);
        GLES20.glUniform3fv(lightHandle, 1, light, 0);
        GLES20.glUniform3fv(ambientHandle, 1, ambient, 0);
        GLES20.glUniform3fv(diffuseHandle, 1, diffuse, 0);
        GLES20.glUniform3fv(specularHandle, 1, specular, 0);
        GLES20.glUniform1f(materialHandle, material);
        GLES20.glUniform1f(attenuationConstHandle, attenuationConst);
        GLES20.glUniform1f(attenuationLinearHandle, attenuationLinear);
        GLES20.glUniform1f(attenuationExponentialHandle, attenuationExponential);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, facesDictionary.size() * 3);

        endDraw();
    }

    public void setColor(float[] color) {
        this.color = color;
    }

    /**
     * Inner class which handle face information (3 vertices)
     */
    public class Face {
        private int[] verticesIndex;
        private int[] normalsIndex;

        public Face(String objLine)
        {
            verticesIndex = new int[3];
            normalsIndex = new int[3];

            String[] vertices = objLine.split(" ");
            String[] vertexInfo;

            for(int i = 1; i<4; i++) {
                vertexInfo = vertices[i].split("/");
                verticesIndex[i - 1] = Integer.parseInt(vertexInfo[0]) - 1;
                normalsIndex[i - 1] = Integer.parseInt(vertexInfo[2]) - 1;
            }
        }

        public int[] getVerticesIndex() {
            return verticesIndex;
        }

        public int[] getNormalsIndex() {
            return normalsIndex;
        }
    }
}
