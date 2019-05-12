package rpgonline.post;

import slickshader.Shader;

public class ColorEffectsShader extends ShaderEffect {
	public float saturation;
	public float brightness;
	public float contrast;
	public float vibrance;
	public float hue;
	
	public ColorEffectsShader(float saturation, float brightness, float contrast,
			float vibrance, float hue) {
		super(ColorEffectsShader.class.getResource("/generic.vrt"), ColorEffectsShader.class.getResource("/colorpost.frg"));
		this.saturation = saturation;
		this.brightness = brightness;
		this.contrast = contrast;
		this.vibrance = vibrance;
		this.hue = hue;
	}
	
	@Override
	protected void updateShader(Shader shader) {
		shader.setUniformFloatVariable("saturation", saturation);
		shader.setUniformFloatVariable("brightness", brightness);
		shader.setUniformFloatVariable("contrast", contrast);
		shader.setUniformFloatVariable("_vibrance", vibrance);
		shader.setUniformFloatVariable("hueShift", hue);
	}
}
