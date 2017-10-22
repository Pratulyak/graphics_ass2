#version 120

varying vec4 vertexCol;
varying vec4 vertexPos;

void main(void) {
	gl_Position=gl_ModelViewProjectionMatrix*vertexPos;
    gl_FrontColor = vertexCol;  
}


