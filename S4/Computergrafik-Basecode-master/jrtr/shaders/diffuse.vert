#version 150
// GLSL version 1.50 
// Vertex shader for diffuse shading in combination with a texture map

#define MAX_LIGHTS 8

// Uniform variables, passed in from host program via suitable 
// variants of glUniform*
uniform mat4 projection;
uniform mat4 modelview;
uniform vec4 lightDirection[MAX_LIGHTS];
uniform vec4 lightPosition[MAX_LIGHTS];
uniform vec3 lightColorDiffuse[MAX_LIGHTS];
uniform vec3 materialColorDiffuse;
uniform int nLights;

// Input vertex attributes; passed in from host program to shader
// via vertex buffer objects
in vec3 normal;
in vec4 position;
in vec2 texcoord;

// Output variables for fragment shader
out vec3 lightingSum;
out float ndotl;
out vec2 frag_texcoord;
out vec3 normal_out;

void main()
{		
	// Compute dot product of normal and light direction
	// and pass color to fragment shader
	// Note: here we assume "lightDirection" is specified in camera coordinates,
	// so we transform the normal to camera coordinates, and we don't transform
	// the light direction, i.e., it stays in camera coordinates
	
	//ndotl = max(dot(modelview * vec4(normal,0), lightDirection[0]),0);
	
	vec3 temp;
	normal_out = normal;
	
	for (int i = 0; i < nLights; i++) {
		// Calculate the light distance and direction to the vertex.
		//float distance = length(lightPosition[i] - position);
		vec4 lightVector = normalize(lightPosition[i] - position);
		 
		ndotl = max(dot(modelview * vec4(normal,0), lightVector), 0.0);
		 
		temp += (lightColorDiffuse[i] * materialColorDiffuse * ndotl);
	}

	lightingSum = temp;

	// Pass texture coordiantes to fragment shader, OpenGL automatically
	// interpolates them to each pixel  (in a perspectively correct manner) 
	frag_texcoord = texcoord;

	// Transform position, including projection matrix
	// Note: gl_Position is a default output variable containing
	// the transformed vertex position
	gl_Position = projection * modelview * position;
}
