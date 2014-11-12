#version 150
// GLSL version 1.50
// Fragment shader for phong shading in combination with a texture map

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
uniform vec3 lightColorAmbient[MAX_LIGHTS];
uniform vec3 materialColorDiffuse;
uniform vec3 materialColorSpecular;
uniform vec3 materialColorAmbient;
uniform float materialShininess;
uniform int nLights;

// Variables passed in from the vertex shader
in vec2 frag_texcoord;
in vec3 frag_normal;
in vec4 frag_position;

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		
	vec3 lightSum, lightDiffuse, lightSpecular, lightAmbient;
	
	vec4 position = modelview * frag_position;
	vec3 normal = frag_normal;
	
	// Texture
	float red = texture(myTexture, frag_texcoord).x;
	float green = texture(myTexture, frag_texcoord).y;
	float blue = texture(myTexture, frag_texcoord).z;
	
	vec3 materialTexture = vec3(red, green, blue);
	
	// Gloss Map
	float specularGloss = red + green + blue;

	for (int i = 0; i < nLights; i++) {
		vec4 lightVector = normalize(lightPosition[i] - position);
		 
		float ndotl = max(dot(modelview * vec4(normal,0), lightVector), 0.0);
		
		vec4 R = reflect(-lightVector, modelview * vec4(normal,0));
		vec4 e = normalize(vec4(centerOfProjection,0) - position);
		float Rep = pow(max(dot(R, e), 0.0), materialShininess);
		 
		lightDiffuse += (lightColorDiffuse[i] * materialTexture * ndotl);
		lightSpecular += (lightColorSpecular[i] * specularGloss * Rep);
		lightAmbient += (lightColorAmbient[i] * materialTexture);
	}

	lightSum = lightDiffuse + lightSpecular + lightAmbient;
	
	// The built-in GLSL function "texture" performs the texture lookup
	//frag_shaded = vec4(lightSum, 0) * texture(myTexture, frag_texcoord);
	frag_shaded = vec4(lightSum, 0);
}

