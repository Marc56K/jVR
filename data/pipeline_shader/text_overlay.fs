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
uniform vec2 positions[128];
uniform vec2 letters[128];
uniform vec2 size[128];
uniform int lettersCount;
uniform vec2 gridSize;

varying vec2 texCoord;

void main (void)
{
	vec2 screenPos = vec2(texCoord.x, 1.0-texCoord.y);

	bool found = false;
	for(int i=0; i<lettersCount; i++)
	{
		if(
		(screenPos.x >= positions[i].x) && 
		(screenPos.x <= (positions[i].x + size[i].x)) &&
		(screenPos.y >= positions[i].y) &&
		(screenPos.y <= (positions[i].y + size[i].y))
		)
		{
			vec2 fontCoord = letters[i];
			fontCoord.x += ((screenPos.x - positions[i].x)/size[i].x) * gridSize.x;
			fontCoord.y += ((screenPos.y - positions[i].y)/size[i].y) * gridSize.y;
			gl_FragColor = texture2D(jvr_Texture0, fontCoord);
			found = true;
		}
	}
	
	if(!found) discard;
}
