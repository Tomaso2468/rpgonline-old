package rpgonline.post;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import rpgonline.net.ServerManager;
import slickshader.Shader;

/**
 * Tints the screen red if the players health is low.
 * 
 * @author Tomas
 *
 */
public class BloodAura extends ShaderEffect {
	/**
	 * The command to run to get the blood effect.
	 */
	private final String cmd;
	/**
	 * The strength of the blood effect.
	 */
	private float blood;

	/**
	 * Create a blood aura effect.
	 * 
	 * @param cmd The command to run to get the strength of the blood effect.
	 */
	public BloodAura(String cmd) {
		super(BloodAura.class.getResource("/generic.vrt"), BloodAura.class.getResource("/bloodaura.frg"));
		this.cmd = cmd;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doPostProcess(GameContainer container, StateBasedGame game, Image buffer, Graphics g)
			throws SlickException {
		blood = (float) ServerManager.getServer().request(cmd);
		super.doPostProcess(container, game, buffer, g);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateShader(Shader shader, GameContainer c) {
		super.updateShader(shader, c);

		shader.setUniformFloatVariable("v", blood);
	}
}
