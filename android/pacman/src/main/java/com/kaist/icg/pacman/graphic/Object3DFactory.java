package com.kaist.icg.pacman.graphic;

import com.kaist.icg.pacman.graphic.android.PacManActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Object 3D factory allow loading mesh from .OBJ file more efficiently.
 * It avoid loading the same file multiple times.
 */
public class Object3DFactory {
    private static Object3DFactory INSTANCE;

    public static Object3DFactory getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Object3DFactory();

        return INSTANCE;
    }

    private HashMap<String, Object3DData> objectsData;

    public Object3DFactory() {
        objectsData = new HashMap<>();
    }

    /**
     * Create a new Object3D instance (or a child class) from an .OBJ file
     *
     * @param file The .OBJ file name from assets folder
     * @param type Object class wanted. Must be Object3D or a children class.
     * @return
     */
    public <T extends Object3D> T instanciate(String file, Class<T> type) {
        Object3DData data = objectsData.get(file);

        if (data == null) {
            data = new Object3DData(file);
            objectsData.put(file, data);
        }

        try {
            if (data.hasTexture)
                return type.getDeclaredConstructor(int.class,
                        FloatBuffer.class, FloatBuffer.class, FloatBuffer.class).newInstance(
                        data.getVertexBufferSize(),
                        data.getVertexBuffer(),
                        data.getNormalBuffer(),
                        data.getTextureCoordinatesBuffer()
                );
            else
                return type.getDeclaredConstructor(int.class,
                        FloatBuffer.class, FloatBuffer.class).newInstance(
                        data.getVertexBufferSize(),
                        data.getVertexBuffer(),
                        data.getNormalBuffer()
                );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public class Object3DData {
        private ArrayList<float[]> verticesDictionary;
        private ArrayList<float[]> normalsDictionary;
        private ArrayList<float[]> textureCoordinatesDictionary;
        private ArrayList<Integer> verticesOrderList;
        private ArrayList<Face> faceList;

        private int nbVertices;

        protected int vertexBufferSize;
        protected FloatBuffer vertexBuffer;
        protected FloatBuffer normalBuffer;
        protected FloatBuffer textureCoordinatesBuffer;
        protected boolean hasTexture;

        public Object3DData(String file) {
            verticesDictionary = new ArrayList<>();
            normalsDictionary = new ArrayList<>();
            textureCoordinatesDictionary = new ArrayList<>();
            verticesOrderList = new ArrayList<>();
            faceList = new ArrayList<>();
            hasTexture = false;

            loadFile(file);
            buildBuffers();

            vertexBufferSize = faceList.size() * 3;
        }

        /**
         * Load OBJ file and build the model
         *
         * @param file File path
         */
        private void loadFile(String file) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        PacManActivity.context.getAssets().open(file)));

                String line;
                do {
                    line = reader.readLine();
                    if (line != null) {
                        if (line.startsWith("v ")) { //vertex
                            String[] split = line.split(" ");
                            float[] vertex = new float[3];
                            vertex[0] = Float.parseFloat(split[1]);
                            vertex[1] = Float.parseFloat(split[2]);
                            vertex[2] = Float.parseFloat(split[3]);

                            verticesDictionary.add(vertex);
                        } else if (line.startsWith("vt ")) { //texture coordinates
                            String[] split = line.split(" ");
                            float[] textureCoordinates = new float[2];
                            textureCoordinates[0] = Float.parseFloat(split[1]);
                            textureCoordinates[1] = Float.parseFloat(split[2]);

                            textureCoordinatesDictionary.add(textureCoordinates);
                            hasTexture = true;
                        } else if (line.startsWith("vn ")) { //normal
                            String[] split = line.split(" ");
                            float[] normal = new float[3];
                            normal[0] = Float.parseFloat(split[1]);
                            normal[1] = Float.parseFloat(split[2]);
                            normal[2] = Float.parseFloat(split[3]);

                            normalsDictionary.add(normal);
                        } else if (line.startsWith("f ")) { //face
                            faceList.add(new Face(line, hasTexture));
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

            ByteBuffer byteBuf = ByteBuffer.allocateDirect(faceList.size() * 3 * 3 * 4);
            byteBuf.order(ByteOrder.nativeOrder());
            vertexBuffer = byteBuf.asFloatBuffer();

            for (Face face : faceList) {
                for (int i = 0; i < face.getVerticesIndex().length; i++) {
                    vertexBuffer.put(verticesDictionary.get(face.getVerticesIndex()[i]));
                    nbVertices++;
                }
            }
            vertexBuffer.position(0);

            if (hasTexture) {
                byteBuf = ByteBuffer.allocateDirect(faceList.size() * 3 * 2 * 4);
                byteBuf.order(ByteOrder.nativeOrder());
                textureCoordinatesBuffer = byteBuf.asFloatBuffer();

                for (Face face : faceList) {
                    for (int i = 0; i < face.getTextureCoordinatesIndex().length; i++) {
                        textureCoordinatesBuffer.put(
                                textureCoordinatesDictionary.get(face.getTextureCoordinatesIndex()[i]));
                    }
                }
                textureCoordinatesBuffer.position(0);
            }

            byteBuf = ByteBuffer.allocateDirect(faceList.size() * 3 * 3 * 4);
            byteBuf.order(ByteOrder.nativeOrder());
            normalBuffer = byteBuf.asFloatBuffer();

            for (Face face : faceList) {
                for (int i = 0; i < face.getNormalsIndex().length; i++) {
                    normalBuffer.put(normalsDictionary.get(face.getNormalsIndex()[i]));
                }
            }
            normalBuffer.position(0);
        }

        public int getVertexBufferSize() {
            return vertexBufferSize;
        }

        public FloatBuffer getVertexBuffer() {
            return vertexBuffer;
        }

        public FloatBuffer getNormalBuffer() {
            return normalBuffer;
        }

        public FloatBuffer getTextureCoordinatesBuffer() {
            return textureCoordinatesBuffer;
        }
    }

    /**
     * Inner class which handle face information (3 vertices)
     */
    public class Face {
        private int[] verticesIndex;
        private int[] normalsIndex;
        private int[] textureCoordinatesIndex;

        public Face(String objLine, boolean hasTexture) {
            verticesIndex = new int[3];
            normalsIndex = new int[3];
            textureCoordinatesIndex = new int[3];

            String[] vertices = objLine.split(" ");
            String[] vertexInfo;

            for (int i = 1; i < 4; i++) {
                vertexInfo = vertices[i].split("/");
                verticesIndex[i - 1] = Integer.parseInt(vertexInfo[0]) - 1;
                if(hasTexture)
                    textureCoordinatesIndex[i - 1] = Integer.parseInt(vertexInfo[1]) - 1;
                normalsIndex[i - 1] = Integer.parseInt(vertexInfo[2]) - 1;
            }
        }

        public int[] getVerticesIndex() {
            return verticesIndex;
        }

        public int[] getNormalsIndex() {
            return normalsIndex;
        }

        public int[] getTextureCoordinatesIndex() {
            return textureCoordinatesIndex;
        }
    }
}
