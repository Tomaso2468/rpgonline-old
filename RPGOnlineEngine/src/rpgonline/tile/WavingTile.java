package rpgonline.tile;

import java.util.Collection;

import org.apache.commons.math3.util.FastMath;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import rpgonline.RPGConfig;
import rpgonline.TextureMap;
import rpgonline.net.ServerManager;
import rpgonline.world.LightSource;
import rpgonline.world.World;

public class WavingTile extends AdvancedTile {
	protected final float multiplier;

	public WavingTile(int c, String texture, boolean solid, boolean shadow, float multiplier) {
		super(c, texture, solid, shadow);
		this.multiplier = multiplier;
	}

	public WavingTile(int c, String texture, float offsetX, float offsetY, boolean solid, boolean shadow,
			float multiplier) {
		super(c, texture, offsetX, offsetY, solid, shadow);
		this.multiplier = multiplier;
	}

	public WavingTile(int c, String texture, float offsetX, float offsetY, boolean shadow, float multiplier) {
		super(c, texture, offsetX, offsetY, shadow);
		this.multiplier = multiplier;
	}

	public WavingTile(int c, String texture, boolean shadow, float multiplier) {
		super(c, texture, shadow);
		this.multiplier = multiplier;
	}

	public void render(GameContainer container, StateBasedGame game, Graphics g, long x, long y, long z, String state,
			World world, Collection<LightSource> lights, float sx, float sy) throws SlickException {
		if(RPGConfig.isWindEnabled()) {
			Image img = TextureMap.getTexture(getTexture(x, y, z, state, world));

			float wind = (float) ServerManager.getServer().request("get wind");

			float amount = (float) wibble((x * y + (System.currentTimeMillis() / 50)) * (double) multiplier) * wind
					* multiplier;

			if (shadow && RPGConfig.isShadowEnabled()) {
				float shadow_l = (float) ServerManager.getServer().request("get shadow_len");
				if (shadow_l > 0) {
					Image shadow = img.getScaledCopy(img.getWidth(), (int) (img.getHeight() * shadow_l));
					shadow.setFilter(Image.FILTER_LINEAR);
					shadow.drawSheared(sx * Tiles.getTileWidth() + offsetX - amount - 8 * shadow_l,
							sy * Tiles.getTileHeight() + offsetY - (shadow.getHeight() - img.getHeight()),
							amount + 8 * shadow_l, 0, new Color(0.001f, 0.001f, 0.05f, getShadowAlpha(lights, x, y)));
					shadow.setFilter(Image.FILTER_NEAREST);
				}
			}

			img.drawSheared(sx * Tiles.getTileWidth() + offsetX - amount, sy * Tiles.getTileHeight() + offsetY, amount, 0);
		} else {
			super.render(container, game, g, x, y, z, state, world, lights, sx, sy);
		}
		
	}

	public double wibble(double v) {
		double a = v % 50 + FastMath.log10(v);
		double b = FastMath.sin(a) + FastMath.sin(FastMath.cbrt(a)) * 2 + (FastMath.cos(2 * a) / 3)
				+ (FastMath.sin(-a) / 2) + (FastMath.tanh(a / 2)) / 3;
		return (FastMath.abs(b) / 2.5 - 1 + FastMath.sin(v / 4) * 4) / 8;
	}
	
	@Override
	public boolean isBatch() {
		return super.isBatch() && (!RPGConfig.isWindEnabled());
	}
}
