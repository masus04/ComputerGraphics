#version 150
// GLSL version 1.50
// Fragment shader for diffuse shading in combination with a texture map

// Uniform variables passed in from host program
uniform sampler2D myTexture;

uniform vec4 lightPosition[8];
uniform int nLights;

// diffuse light
uniform vec3 diffuseRadience[8];
uniform vec3 diffuseReflection;


// Variables passed in from the vertex shader
in vec2 frag_texcoord;
in frag_normal;
in gl_Position;

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		
	vec4 L[8];


	// calculate Lightvector L for every light source
	for (int i=0; i<nLights; i++){
		L[i] = lightPosition[i] - gl_Position;
	}
	
	// calculate diffuse shading
	vec3 sum = (0,0,0);
	
	for (int i=0; i<nLights; i++){
		vec4 ndotl = max(dot(frag_normal, normalize(L[i])), 0);
		
		sum.x = sum.x + diffuseRadience.x * diffuseReflection.x * ndotl.x;
		sum.y = sum.y + diffuseRadience.y * diffuseReflection.y * ndotl.y;
		sum.z = sum.z + diffuseRadience.z * diffuseReflection.z * ndotl.z;
	}
	
	frag_shaded = sum * texture(myTexture, frag_texcoord);

	// The built-in GLSL function "texture" performs the texture lookup
	//frag_shaded = ndotl * texture(myTexture, frag_texcoord);
}

