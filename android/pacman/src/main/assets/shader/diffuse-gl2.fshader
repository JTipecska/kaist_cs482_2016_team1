precision mediump float;

uniform vec3 uLight, uColor;

varying vec3 vNormal;
varying vec3 vPosition;

const float uAttConst = 1.0, uAttLin = 0.05, uAttExp = 0.01;

void main() {
  vec3 tolight = normalize(uLight - vPosition);
  vec3 normal = normalize(vNormal);

  float diffuse = max(0.0, dot(normal, tolight));


  float dist = distance(uLight, vPosition);
  float attenuation = uAttConst + uAttLin * dist + uAttExp * pow(dist, 2.0);

  vec3 intensity = uColor * diffuse/attenuation;

  gl_FragColor = vec4(intensity, 1.0);
}
