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
uniform bool jvr_UseTexture0;
uniform bool jvr_Texture0_IsSemiTransparent;
uniform vec4 jvr_Material_Ambient;

varying vec2 texCoord;

void jvr_discardIfClipped();

void main(void)
{
   	jvr_discardIfClipped();
   	
   	vec4 a = jvr_Material_Ambient;
   	if (jvr_UseTexture0)
   	{
        a *= texture2D(jvr_Texture0, texCoord);
        if(a.w<0.2) discard;
    }
	vec4 final_color = a;
	gl_FragColor = final_color;
	
	if(!jvr_Texture0_IsSemiTransparent && jvr_Material_Ambient.a == 1.0)
	{
		gl_FragColor.w=1.0;
	}
}
