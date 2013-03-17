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
uniform int iterations;
varying vec2 texCoord;

const float blurSize = 1.0/512.0;

void main (void)
{
   vec4 sum = vec4(0.0);
   
   // blur in x (horizontal)
   // take nine samples, with the distance blurSize between them
   sum += texture2D(jvr_Texture0, vec2(texCoord.x - 4.0*blurSize, texCoord.y)) * 0.05;
   sum += texture2D(jvr_Texture0, vec2(texCoord.x - 3.0*blurSize, texCoord.y)) * 0.09;
   sum += texture2D(jvr_Texture0, vec2(texCoord.x - 2.0*blurSize, texCoord.y)) * 0.12;
   sum += texture2D(jvr_Texture0, vec2(texCoord.x - blurSize, texCoord.y)) * 0.15;
   sum += texture2D(jvr_Texture0, vec2(texCoord.x, texCoord.y)) * 0.16;
   sum += texture2D(jvr_Texture0, vec2(texCoord.x + blurSize, texCoord.y)) * 0.15;
   sum += texture2D(jvr_Texture0, vec2(texCoord.x + 2.0*blurSize, texCoord.y)) * 0.12;
   sum += texture2D(jvr_Texture0, vec2(texCoord.x + 3.0*blurSize, texCoord.y)) * 0.09;
   sum += texture2D(jvr_Texture0, vec2(texCoord.x + 4.0*blurSize, texCoord.y)) * 0.05;
 
   // blur in y (vertical)
   // take nine samples, with the distance blurSize between them
   sum += texture2D(jvr_Texture0, vec2(texCoord.x, texCoord.y - 4.0*blurSize)) * 0.05;
   sum += texture2D(jvr_Texture0, vec2(texCoord.x, texCoord.y - 3.0*blurSize)) * 0.09;
   sum += texture2D(jvr_Texture0, vec2(texCoord.x, texCoord.y - 2.0*blurSize)) * 0.12;
   sum += texture2D(jvr_Texture0, vec2(texCoord.x, texCoord.y - blurSize)) * 0.15;
   sum += texture2D(jvr_Texture0, vec2(texCoord.x, texCoord.y)) * 0.16;
   sum += texture2D(jvr_Texture0, vec2(texCoord.x, texCoord.y + blurSize)) * 0.15;
   sum += texture2D(jvr_Texture0, vec2(texCoord.x, texCoord.y + 2.0*blurSize)) * 0.12;
   sum += texture2D(jvr_Texture0, vec2(texCoord.x, texCoord.y + 3.0*blurSize)) * 0.09;
   sum += texture2D(jvr_Texture0, vec2(texCoord.x, texCoord.y + 4.0*blurSize)) * 0.05;
 
   gl_FragColor = sum/2.0;
   gl_FragColor.a = 1.0;
}
