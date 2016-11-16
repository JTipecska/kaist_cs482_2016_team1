precision mediump float;

uniform mat4 uProjMatrix;
uniform mat4 uModelViewMatrix;
uniform mat4 uNormalMatrix;

attribute vec3 aPosition;
attribute vec3 aNormal;
attribute vec2 aTextureCoordinate;
attribute vec3 aTangent;
attribute vec3 aBitangent;

varying vec3 vNormal;
varying vec3 vPosition;
varying vec2 vTextureCoordinate;
varying mat3 TBN;

void main() {
  vNormal = vec3(uNormalMatrix * vec4(aNormal, 0.0));

  // send position (eye coordinates) to fragment shader
  vec4 tPosition = uModelViewMatrix * vec4(aPosition, 1.0);
  vPosition = vec3(tPosition);

  vTextureCoordinate = aTextureCoordinate;

   /*vec4 vertexNormal_cameraspace = uModelViewMatrix * normalize(vec4(aNormal, 0.0));
   vec4 vertexTangent_cameraspace = uModelViewMatrix * normalize(vec4(aTangent, 0.0));
   vec4 vertexBitangent_cameraspace = uModelViewMatrix * normalize(vec4(aBitangent, 0.0));

    TBN = transpose(mat3(
        vertexTangent_cameraspace.rgb,
        vertexBitangent_cameraspace.rgb,
        vertexNormal_cameraspace.rgb));*/

  gl_Position = uProjMatrix * tPosition;
}