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
uniform vec4 cameraPosition;

// specular light
uniform vec3 specularRadience[8];
uniform vec3 specularReflection;
uniform int p;

// Variables passed in from the vertex shader
in vec2 frag_texcoord;
in vec3 frag_normal;
in vec4 gl_Position;

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
	vec3 sum;
	sum.x = 0; sum.y = 0; sum.z=0;
	
	for (int i=0; i<nLights; i++){
		float ndotl = max(dot((frag_normal), normalize(vec3 (L[i].x, L[i].y, L[i].z))), 0);
		
		sum.x = sum.x + diffuseRadience[i].x * diffuseReflection.x * ndotl;
		sum.y = sum.y + diffuseRadience[i].y * diffuseReflection.y * ndotl;
		sum.z = sum.z + diffuseRadience[i].z * diffuseReflection.z * ndotl;
	}
	
	frag_shaded.x = sum.x * texture(myTexture, frag_texcoord).x;
	frag_shaded.y = sum.y * texture(myTexture, frag_texcoord).y;
	frag_shaded.z = sum.z * texture(myTexture, frag_texcoord).z;
	
	
	//----------------------------------------------------------------------------------------------------------
	
	// calculate R: the vector of the reflection
	vec4 R[8];
	
	for (int i=0; i< nLights; i++){
		R[i].x = max(2 * dot(L[i], vec4(frag_normal,0)) * frag_normal.x - L[i].x, 0);
		R[i].y = max(2 * dot(L[i], vec4(frag_normal,0)) * frag_normal.y - L[i].y, 0);
		R[i].z = max(2 * dot(L[i], vec4(frag_normal,0)) * frag_normal.z - L[i].z, 0);
		R[i].w = 0;
	}
	
	
	// calculate e: the vector to the camera
	vec4 E;
	
	E.x = gl_Position.x - cameraPosition.x;
	E.y = gl_Position.y - cameraPosition.y;
	E.z = gl_Position.z - cameraPosition.z;
	
	for (int i=0; i<nLights; i++){
		sum.x = sum.x + specularRadience[i].x * specularReflection.x * pow(R[i].x * E.x, p);
		sum.y = sum.y + specularRadience[i].y * specularReflection.y * pow(R[i].y * E.y, p);
		sum.z = sum.z + specularRadience[i].z * specularReflection.z * pow(R[i].z * E.z, p);
	}
	
	frag_shaded.x = sum.x * texture(myTexture, frag_texcoord).x;
	frag_shaded.y = sum.y * texture(myTexture, frag_texcoord).y;
	frag_shaded.z = sum.z * texture(myTexture, frag_texcoord).z;
	
	

	// The built-in GLSL function "texture" performs the texture lookup
	//frag_shaded = ndotl * texture(myTexture, frag_texcoord);
}

