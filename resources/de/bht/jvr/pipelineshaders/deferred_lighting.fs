/**
 * Copyright 2013 Marc Roßbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

uniform vec4  jvr_LightSource_Diffuse;
uniform vec4  jvr_LightSource_Specular;
uniform float jvr_LightSource_Intensity;
uniform vec4  jvr_LightSource_Position;
uniform vec3  jvr_LightSource_SpotDirection;
uniform float jvr_LightSource_SpotExponent;
uniform float jvr_LightSource_SpotCutOff;
uniform float jvr_LightSource_SpotCosCutOff;
uniform bool  jvr_LightSource_CastShadow;
uniform float jvr_LightSource_ConstantAttenuation;
uniform float jvr_LightSource_LinearAttenuation;
uniform float jvr_LightSource_QuadraticAttenuation;
uniform mat4  jvr_LightSource_ModelViewProjectionMatrix;

uniform sampler2D jvr_DS_DiffuseColor;
uniform sampler2D jvr_DS_SpecularColor;
uniform sampler2D jvr_DS_Position;
uniform sampler2D jvr_DS_WorldPosition;
uniform sampler2D jvr_DS_Normal;
uniform sampler2D jvr_ShadowMap;

varying vec2  texCoord;

vec4 phong(vec3 N, vec3 L, vec3 E, vec4 diffuseColor, vec4 specularColor, float shininess, float attenuation)
{
	vec4 final_color = vec4(0,0,0,0);
	
	float lambertTerm = dot(N,L);
	if(lambertTerm > 0.0) 
	{
		final_color += attenuation * jvr_LightSource_Intensity * jvr_LightSource_Diffuse * diffuseColor * lambertTerm;		
		vec3 R = reflect(-L, N);
		final_color += attenuation * jvr_LightSource_Intensity * jvr_LightSource_Specular * specularColor * pow( max(dot(R, E), 0.0), shininess);
	}
	
	return final_color;	
}

float lookup(vec4 shadowCoord, float x, float y)
{
	shadowCoord.x += x;
	shadowCoord.y += y;
	float distanceFromLight = texture2D(jvr_ShadowMap, shadowCoord.xy).z;	
	return distanceFromLight < shadowCoord.z ? 0.0: 1.0; 	
}

void main (void)
{
   	float attenuation = 1.0;   	
   	
   	vec4 diffuseColor = texture2D(jvr_DS_DiffuseColor, texCoord);
   	vec4 specularColor = texture2D(jvr_DS_SpecularColor, texCoord);
   	vec4 position = texture2D(jvr_DS_Position, texCoord);   	
   	vec4 normal = texture2D(jvr_DS_Normal, texCoord);
   	float shininess = normal.w;
   	
   	vec3 lightDir;
   	
    if(jvr_LightSource_Position.w > 0.0)
    {
    	// point light or spot light
    	lightDir = vec3( jvr_LightSource_Position.xyz - position.xyz );
    	
    	float distance = length(lightDir);
    	attenuation = 1.0 / (jvr_LightSource_QuadraticAttenuation * distance * distance
                           + jvr_LightSource_LinearAttenuation * distance
                           + jvr_LightSource_ConstantAttenuation);
    }
    else
    {
    	// directional light
    	lightDir = -vec3( normalize( jvr_LightSource_Position.xyz ) );
    	attenuation = 1.0;
    }
	
	vec3 N = normalize(normal.xyz);
	vec3 L = normalize(lightDir);
	vec3 E = normalize(-position.xyz);
	
	if (jvr_LightSource_SpotCutOff == 0.0)
	{	
		// point light or directional light
		gl_FragColor = phong(N, L, E, diffuseColor, specularColor, shininess, attenuation);
	}
	else
	{
		vec3 D = normalize(jvr_LightSource_SpotDirection);
		float LdotD = dot(-L, D);
		if(LdotD > jvr_LightSource_SpotCosCutOff)
		{
			// spot light			
			if(jvr_LightSource_CastShadow)
			{
				vec4 worldPosition = texture2D(jvr_DS_WorldPosition, texCoord);
				
				// Moving from [-1,1] to [0,1]
				mat4 DCtoTC = mat4(	0.5, 0.0, 0.0, 0.0,
									0.0, 0.5, 0.0, 0.0,
									0.0, 0.0, 0.5, 0.0,
									0.5, 0.5, 0.5, 1.0);
				vec4 shadowCoord = DCtoTC * jvr_LightSource_ModelViewProjectionMatrix * worldPosition;
				shadowCoord /= shadowCoord.w;
				
				// with shadow mapping
				int samples = 2;
				float sum = 0.0;
				int x;
				int y;
				for(x = -samples; x<samples; x++)
				{
					for(y = -samples; y<samples; y++)
					{
						sum += lookup(shadowCoord, float(x)*0.0002, float(y)*0.0002);
					}
				}
				
				sum /= float((2*samples)*(2*samples));

				if(sum > 0.0)
				{
					gl_FragColor = phong(N, L, E, diffuseColor, specularColor, shininess, attenuation * pow(LdotD, jvr_LightSource_SpotExponent));
					gl_FragColor.w = sum;
				}
				else
				{
					discard;
				}
			}
			else
			{			
				// without shadow mapping
				gl_FragColor = phong(N, L, E, diffuseColor, specularColor, shininess, attenuation * pow(LdotD, jvr_LightSource_SpotExponent));
			}
		}
		else
		{
			discard;
		}
	}
}