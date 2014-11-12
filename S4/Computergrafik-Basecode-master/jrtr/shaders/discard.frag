#version 150
// GLSL version 1.50
// Fragment shader for discard shading in combination with a texture map

// Uniform variables passed in from host program
uniform sampler2D myTexture;

// Variables passed in from the vertex shader
in vec2 frag_texcoord;

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		
	// The built-in GLSL function "texture" performs the texture lookup	
	vec4 color = texture(myTexture,frag_texcoord); 
	       
	if (color.rgb == vec3(0.0,0.0,0.0))
		discard; 

	gl_FragColor = color;
}