#version 120
uniform float time;
uniform vec2 vel;
uniform vec4 color;
uniform vec2 scales;
uniform vec4 rectOuter;
uniform vec4 rectInner;
uniform vec2 finalCoeff;
uniform vec2 fbmVel;

varying vec2 v_pos;

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

float fbm(vec2 coord){
	float value = 0.0;
	float scale = 0.5;
	//octaves could be part of a performance setting for shaders which uses the prepend function and preprocessor stuff
	for(int i = 0; i < 4; i++){
		value += noise(coord) * scale;
		coord *= 2.0;
		scale *= 0.5;
	}
	return value;
}

void main(){
	vec2 coord = vec2(v_pos.x/scales.x, v_pos.y/scales.y);
	vec2 offset = -vec2(vel.x/1, vel.y/1) * time;
	vec2 motion = vec2( fbm(coord + offset + vec2(time/scales.x * fbmVel.x, time/scales.y * fbmVel.y)) );

	float final = fbm(coord + motion + offset);
	
	final = final * finalCoeff.x + finalCoeff.y;
	
	final = final * smoothstep(rectOuter.x, rectInner.x, v_pos.x);
	final = final * smoothstep(rectOuter.y, rectInner.y, v_pos.y);
	final = final * (1-smoothstep(rectInner.z, rectOuter.z, v_pos.x));
	final = final * (1-smoothstep(rectInner.w, rectOuter.w, v_pos.y));	

    gl_FragColor = vec4(color.rgb, final);
}