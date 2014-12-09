#version 150
// GLSL version 1.50
// Fragment shader for diffuse shading in combination with a texture map

// Uniform variables passed in from host program
uniform sampler2D myTexture;

// Variables passed in from the vertex shader
in float ndotl;
in vec3 lightingSum;
in vec2 frag_texcoord;
in vec3 normal_out;

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{	
	/*int numShades = 10;
	vec3 shadeIntensity = ceil(lightingSum * numShades)/numShades;*/
	// The built-in GLSL function "texture" performs the texture lookup
	vec4 values = vec4(lightingSum, 0) * texture(myTexture, frag_texcoord);
	
	if (values.x > 0.9)
		values.x = 1;
	else if (values.x > 0.5)
		values.x = 0.7;
	else if (values.x > 0.25)
		values.x = 0.3;
	else if (values.x < 0.25)
		values.x = 0.15;
		
	if (values.y > 0.9)
		values.y = 1;
	else if (values.y > 0.5)
		values.y = 0.7;
	else if (values.y > 0.25)
		values.y = 0.3;
	else if (values.y < 0.25)
		values.y = 0.15;
		
	if (values.z > 0.9)
		values.z = 1;
	else if (values.z > 0.5)
		values.z = 0.7;
	else if (values.z > 0.25)
		values.z = 0.3;
	else if (values.z < 0.25)
		values.z = 0.15;
	
	
	frag_shaded.x = values.x ;
	frag_shaded.y = values.y ;
	frag_shaded.z = values.z ;
	
	//frag_shaded = values * shadeIntensity;
	//frag_shaded = vec4(normal_out, 1);
}

