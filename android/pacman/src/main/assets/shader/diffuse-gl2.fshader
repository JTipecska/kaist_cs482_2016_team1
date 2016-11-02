precision mediump float;

uniform vec3 uLight, uColor;

varying vec3 vNormal;
varying vec3 vPosition;

void main() {
  vec3 tolight = normalize(uLight - vPosition);
  vec3 normal = normalize(vNormal);

  float diffuse = max(0.0, dot(normal, tolight));
  vec3 intensity = uColor * diffuse;

  gl_FragColor = vec4(intensity, 1.0);
}
