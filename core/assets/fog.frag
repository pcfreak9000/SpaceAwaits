uniform float time;
uniform vec2 vel;

varying vec2 v_pos;

float rand(vec2 coord){
	return fract(sin(dot(coord, vec2(56, 78)) * 1000.0) * 1000.0);
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

	for(int i = 0; i < 4; i++){
		value += noise(coord) * scale;
		coord *= 2.0;
		scale *= 0.5;
	}
	return value;
}
void main(){
	vec2 coord = vec2(v_pos.x/60.0, v_pos.y/8.0);
	vec2 offset = vel*time;
	vec2 motion = vec2( fbm(coord +offset+ vec2(time * -0.5, time * 0.5)) );

	float final = fbm(coord + motion + offset);


    gl_FragColor = vec4(0.3, 0.3, 0.3, final*0.2+0.95);
}