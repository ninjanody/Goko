#version 330 core

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

// Input vertex data, different for all executions of this shader.
layout(location = 0) in vec4 vertexPosition_modelspace;
layout(location = 1) in vec4 vertexColor;
layout(location = 2) in vec2 vertexUV;
layout(location = 5) in vec3 charChannel;

// Output data ; will be interpolated for each fragment.
out vec2 UV;
out vec4 vColor;
out vec3 vCharChannel;
void main(){ 
	gl_Position = projectionMatrix*modelViewMatrix*vertexPosition_modelspace; 
    
	// UV of the vertex. No special space for this one.
	UV = vertexUV;
	
	// Color, simply pass it
	vColor = vertexColor;
	
	// Pas the channel of the character
	vCharChannel = charChannel;
}