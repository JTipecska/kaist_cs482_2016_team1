precision mediump float;

uniform vec3 uLight, uColor;
uniform sampler2D uTexture;

varying vec3 vNormal;
varying vec3 vPosition;

varying vec2 vTextureCoordinate;

void main() {
  vec3 tolight = normalize(uLight - vPosition);
  vec3 normal = normalize(vNormal);

  float diffuse = max(0.0, dot(normal, tolight));
  vec4 color = texture2D(uTexture, vTextureCoordinate);
  color.w = 1.0;
  vec4 intensity = color * diffuse;

  gl_FragColor = intensity;
}