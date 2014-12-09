#version 150
// GLSL version 1.50
// Fragment shader for phong shading

#define MAX_LIGHTS 8

// Uniform variables passed in from host program
uniform sampler2D myTexture;

uniform mat4 projection;
uniform mat4 modelview;

uniform vec3 centerOfProjection;
uniform vec4 lightDirection[MAX_LIGHTS];
uniform vec4 lightPosition[MAX_LIGHTS];
uniform vec3 lightColorDiffuse[MAX_LIGHTS];
uniform vec3 lightColorSpecular[MAX_LIGHTS];
uniform vec3 materialColorDiffuse;
uniform vec3 materialColorSpecular;
uniform float materialShininess;
uniform int nLights;

// Variables passed in from the vertex shader
// in float ndotl;
in vec2 frag_texcoord;
in vec3 frag_normal;
in vec4 frag_position;

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		
	vec3 lightSum, lightDiffuse, lightSpecular;
	vec4 position = modelview * frag_position;
	vec3 normal = normalize(frag_normal);

	for (int i = 0; i < nLights; i++) {
		vec4 lightVector = (lightPosition[i] - position);
		 
		float ndotl = max(dot(modelview * vec4(normal,0), lightVector), 0.0);
		
		vec4 R = reflect(-lightVector, modelview * vec4(normal,0));
		vec4 e = normalize(vec4(centerOfProjection,0) - position);
		float Rep = pow(max(dot(normalize(R), e), 0.0), materialShininess);
		 
		lightDiffuse += (lightColorDiffuse[i] * materialColorDiffuse * ndotl);
		lightSpecular += (lightColorSpecular[i] * materialColorSpecular * Rep);
	}

	lightSum = lightDiffuse + lightSpecular;
	
	// The built-in GLSL function "texture" performs the texture lookup
	//frag_shaded = vec4(lightSum, 0) * texture(myTexture, frag_texcoord);
	frag_shaded = vec4(lightSum, 0);
}

