package rpgonline.post;

import slickshader.Shader;

/**
 * A shader creating a heat effect.
 * 
 * @author Tomas
 */
public class HeatShader extends ShaderEffect {
	/**
	 * Create the shader
	 */
	public HeatShader() {
		super(HeatShader.class.getResource("/heat.vrt"), HeatShader.class.getResource("/heat.frg"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateShader(Shader shader) {
		super.updateShader(shader);

		shader.setUniformFloatVariable("u_time", System.currentTimeMillis() % 100000 / 50f);
		shader.setUniformFloatVariable("stren", 1);
	}
}
