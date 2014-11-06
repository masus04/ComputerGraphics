#version 150
// GLSL version 1.50
// Fragment shader for diffuse shading in combination with a texture map

// Uniform variables passed in from host program
uniform sampler2D myTexture;

// Variables passed in from the vertex shader
in float ndotl[8];
in int noLights;
in vec2 frag_texcoord;

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		
	float tmp=0;
	
	// The built-in GLSL function "texture" performs the texture lookup
	for (int i=0; i<nLights; i++){
			tmp += ndolt[i] * texture(myTexture, frag_texcoord);
	}

	frag_shaded = tmp;
}

