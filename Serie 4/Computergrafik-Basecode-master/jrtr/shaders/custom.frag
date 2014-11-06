#version 150
// GLSL version 1.50
// Fragment shader for diffuse shading in combination with a texture map

// Uniform variables passed in from host program
uniform sampler2D myTexture;

uniform vec4 lightPosition[8];

uniform vec3 diffuseRadience[8];
uniform vec3 specularRadience[8];

uniform vec3 diffuseReflection;
uniform vec3 specularReflection;
uniform int nLights;

uniform int p;


// Variables passed in from the vertex shader
in float ndotl[8];
in vec4 R[8];
in vec4 E;
in vec2 frag_texcoord;

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		
	// The built-in GLSL function "texture" performs the texture lookup
	//frag_shaded = ndotl * texture(myTexture, frag_texcoord);

	//myTexture.diffuse;
	
	// --------------------------------------------------------------------------------------------
	lightPosition;
	
	vec3 sum;
	sum.x = 0; sum.y = 0; sum.z=0;
	
	for (int i = 0; i< nLights; i++){
		sum.x = sum.x + diffuseRadience[i].x * diffuseReflection.x * ndotl[i];
		sum.y = sum.y + diffuseRadience[i].y * diffuseReflection.y * ndotl[i];
		sum.z = sum.z + diffuseRadience[i].z * diffuseReflection.z * ndotl[i];
	}
	
	frag_shaded.x = sum.x * texture(myTexture, frag_texcoord).x;
	frag_shaded.y = sum.y * texture(myTexture, frag_texcoord).y;
	frag_shaded.z = sum.z * texture(myTexture, frag_texcoord).z;
	
	// --------------------------------------------------------------------------------------------
	
	sum.x = 0; sum.y = 0; sum.z=0;
	
	for (int i=0; i<nLights; i++){
	//	sum.x = sum.x + specularRadience[i].x * specularReflection.x * pow(R.x * E.x, p);
	//	sum.y = sum.y + specularRadience[i].y * specularReflection.y * pow(R.y * E.y, p);
	//	sum.z = sum.z + specularRadience[i].z * specularReflection.z * pow(R.z * E.z, p);
	}
	
	//frag_shaded.x = frag_shaded.x + sum.x;
	//frag_shaded.y = frag_shaded.y + sum.y;
	//frag_shaded.z = frag_shaded.z + sum.z;
	// --------------------------------------------------------------------------------------------
	
}

