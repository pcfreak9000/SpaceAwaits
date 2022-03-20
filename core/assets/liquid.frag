#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_pos;
varying vec2 v_sspos;
varying vec2 v_shoreline;

uniform sampler2D u_texture;
uniform float time;
uniform vec2 size;

float rand(vec2 coord){
	return fract(sin(dot(coord, vec2(12.9898,78.233))) * 43758.5453);
}

float noise(vec2 coord){
	vec2 i = floor(coord);
	vec2 f = fract(coord);

	// 4 corners of a rectangle surrounding our point
	float a = rand(i);
	float b = rand(i + vec2(1.0, 0.0));
	float c = rand(i + vec2(0.0, 1.0));
	float d = rand(i + vec2(1.0, 1.0));

	vec2 cubic = f * f * (3.0 - 2.0 * f);

	return mix(a, b, cubic.x) + (c - a) * cubic.y * (1.0 - cubic.x) + (d - b) * cubic.x * cubic.y;
}

void main(){

	vec2 noisec1 = v_pos;//v_texCoords * size;
	vec2 noisec2 = v_pos+vec2(4.0);//v_texCoords * size + vec2(4.0);
	
	vec2 motion1 = vec2(time * 0.3, time * -0.4);
	vec2 motion2 = vec2(time * 0.1, time * 0.5);
	
	vec2 distort1 = vec2(noise(noisec1 + motion1), noise(noisec2 + motion1)) - vec2(0.5);
	vec2 distort2 = vec2(noise(noisec1 + motion2), noise(noisec2 + motion2)) - vec2(0.5);
	
	vec2 distortTotal = (distort1 + distort2) / 250.0;
	
	vec2 distortedCoords = v_sspos*0.5+vec2(0.5)+distortTotal;
	
	vec4 texCol = texture2D(u_texture, distortedCoords);
	vec3 mixed = mix(texCol.rgb, v_color.rgb, v_color.a);
	vec4 totalCol = vec4(mixed.rgb, 1.0);
	
	float nearTop = (v_pos.y + distortTotal.y * 20.0 - v_shoreline.y) / (v_shoreline.x - v_shoreline.y);
	nearTop = clamp(nearTop, 0.0, 1.0);
	nearTop = 1.0 - nearTop;
	nearTop = nearTop / 0.4;
	nearTop = clamp(nearTop, 0.0, 1.0);
	nearTop = 1.0 - nearTop;
	//totalCol = vec4(nearTop,nearTop,nearTop,1.0);
	totalCol = mix(totalCol, vec4(v_color.rgb, 1.0), nearTop);
	
	if (nearTop > 0.8) {
		if (nearTop > 0.99) {
			discard;
		} 
		totalCol.a = (0.99 - nearTop) / (0.99-0.8);
	}
	
	gl_FragColor = totalCol;
}