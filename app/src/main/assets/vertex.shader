attribute vec3 vPos;
attribute vec3 vCol;
uniform mat4 projectionMatrix;
uniform float z;
varying vec3 colour;

void main() {
	colour = vCol;
	gl_Position = projectionMatrix * vec4(vPos.xy, vPos.z - z, 1.0);
}