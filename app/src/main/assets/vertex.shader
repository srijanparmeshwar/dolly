attribute vec3 vPos;
attribute vec3 vCol;
uniform mat4 projectionMatrix;
uniform float z;
varying vec3 colour;

void main() {
	colour = vCol;
	gl_Position = projectionMatrix * vec4(vPos.x + cos(z) / 10.0, vPos.y + sin(z) / 10.0, vPos.z, 1.0);
}