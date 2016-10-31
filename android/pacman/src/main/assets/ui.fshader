precision mediump float;

uniform sampler2D uTexture;

varying vec2 vTextureCoordinate;

void main(void) {
  gl_FragColor = texture2D(uTexture, vTextureCoordinate);
}