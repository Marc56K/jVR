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

uniform sampler2D jvr_Texture0;
uniform bool      jvr_UseTexture0;
uniform vec4      jvr_Material_Ambient;
uniform vec4      jvr_Material_Specular;
uniform vec4      jvr_Material_Diffuse;
uniform float     jvr_Material_Shininess;

varying vec4 position;
varying vec4 worldPosition;
varying vec3 normal;
varying vec2 texCoord;
//varying vec3 tangent;
//varying vec3 binormal;

void jvr_discardIfClipped();

void main (void)
{
   	jvr_discardIfClipped();
	
   	vec4 ambientColor = jvr_Material_Ambient;
   	vec4 diffuseColor = jvr_Material_Diffuse;
   	vec4 specularColor = jvr_Material_Specular;
   	if (jvr_UseTexture0)
   	{
        vec4 tex = texture2D(jvr_Texture0, texCoord);
        ambientColor *= tex;
        if(ambientColor.a < 0.2) discard;
        diffuseColor *= tex;       
        specularColor *= tex;
    }
	 
	gl_FragData[0] = ambientColor;	
	gl_FragData[1] = diffuseColor;
	gl_FragData[2] = specularColor;
	gl_FragData[3] = position;	
	gl_FragData[4] = worldPosition;
	gl_FragData[5] = vec4(normal, jvr_Material_Shininess); // use the alpha channel to store the shininess	
	//gl_FragData[6].xyz = tangent;
	//gl_FragData[7].xyz = binormal;
}