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

varying vec2 texCoord;

void main (void)
{
   	if(jvr_UseTexture0)
   	{
   		vec4 tex = texture2D(jvr_Texture0, texCoord);
   		gl_FragColor = tex;
   		float alpha = tex.r+tex.g+tex.b;
   		if(alpha<0.1) discard;
   		gl_FragColor.a = alpha*0.75;
    }
   	else
   	{
	   	gl_FragColor.rgb = vec3(1.0, 0.0, 0.0);
	   	gl_FragColor.a = 1.0;
	}
}
