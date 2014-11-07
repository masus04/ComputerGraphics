#version 150
// GLSL version 1.50 
// Vertex shader for diffuse shading in combination with a texture map

// Uniform variables, passed in from host program via suitable 
// variants of glUniform*
uniform mat4 projection;
uniform mat4 modelview;
uniform int nLights;
uniform vec4 lightDirection[8];
uniform vec4 cameraPosition;

// Input vertex attributes; passed in from host program to shader
// via vertex buffer objects
in vec3 normal;
in vec4 position;
in vec2 texcoord;

// Output variables for fragment shader
out float ndotl[8];
out vec4 R[8];
out vec4 E;
out vec2 frag_texcoord;

void main()
{	
	
	// Compute dot product of normal and light direction
	// and pass color to fragment shader
	// Note: here we assume "lightDirection" is specified in camera coordinates,
	// so we transform the normal to camera coordinates, and we don't transform
	// the light direction, i.e., it stays in camera coordinates
	//ndotl[0] = max(dot(modelview * vec4(normal,0), lightDirection[0]),0);
	
	// --------------------------------------------------
	
	for (int i=0; i< nLights; i++){
		ndotl[i] = max(dot(modelview * vec4(normal,0), lightDirection[i]),0);
	}
	
	// --------------------------------------------------
	
	for (int i=0; i< nLights; i++){
		R[i].x = max(2 * dot(lightDirection[i], vec4(normal,0)) * normal.x - lightDirection[i].x, 0);
		R[i].y = max(2 * dot(lightDirection[i], vec4(normal,0)) * normal.y - lightDirection[i].y, 0);
		R[i].z = max(2 * dot(lightDirection[i], vec4(normal,0)) * normal.z - lightDirection[i].z, 0);
		R[i].w = 0;
	}
	
	E.x = position.x - cameraPosition.x;
	E.y = position.y - cameraPosition.y;
	E.z = position.z - cameraPosition.z;
	E.w = 0;
	
	// --------------------------------------------------
	
	
	// Pass texture coordiantes to fragment shader, OpenGL automatically
	// interpolates them to each pixel  (in a perspectively correct manner) 
	frag_texcoord = texcoord;

	// Transform position, including projection matrix
	// Note: gl_Position is a default output variable containing
	// the transformed vertex position
	gl_Position = projection * modelview * position;
}


