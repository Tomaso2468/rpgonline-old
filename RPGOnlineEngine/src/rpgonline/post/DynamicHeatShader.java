package rpgonline.post;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import rpgonline.net.ServerManager;
import slickshader.Shader;

/**
 * A heat shader effect that changes over time.
 * 
 * @author Tomas
 */
public class DynamicHeatShader extends ShaderEffect {
	/**
	 * The command to run to get the heat effect.
	 */
	private final String cmd;
	/**
	 * The strength of the effect.
	 */
	private float heat;

	/**
	 * Creates the effect.
	 * 
	 * @param cmd The command to run to get the strength of the effect.
	 */
	public DynamicHeatShader(String cmd) {
		super(DynamicHeatShader.class.getResource("/heat.vrt"), DynamicHeatShader.class.getResource("/heat.frg"));
		this.cmd = cmd;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doPostProcess(GameContainer container, StateBasedGame game, Image buffer, Graphics g)
			throws SlickException {
		heat = (float) ServerManager.getServer().request(cmd);
		super.doPostProcess(container, game, buffer, g);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateShader(Shader shader) {
		super.updateShader(shader);

		shader.setUniformFloatVariable("u_time", System.currentTimeMillis() % 100000 / 50f);
		shader.setUniformFloatVariable("stren", heat);
	}
}
