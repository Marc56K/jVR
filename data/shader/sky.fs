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

varying vec2 texCoord;

void main (void)
{
	vec2 texC = texCoord;
	
	// remove border lines
	if(texC.x>0.999)texC.x = 0.999;
	if(texC.y>0.999)texC.y = 0.999;
	if(texC.x<0.001)texC.x = 0.001;
	if(texC.y<0.001)texC.y = 0.001;
	
	gl_FragColor = texture2D(jvr_Texture0, texC);
}
