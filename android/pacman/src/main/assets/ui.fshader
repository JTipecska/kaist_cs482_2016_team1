precision mediump float;

uniform sampler2D uTexture;
uniform float uOpacity;

varying vec2 vTextureCoordinate;

void main(void) {
  vec4 color = texture2D(uTexture, vTextureCoordinate);
  color.w = uOpacity * color.w;
  gl_FragColor = color;
}