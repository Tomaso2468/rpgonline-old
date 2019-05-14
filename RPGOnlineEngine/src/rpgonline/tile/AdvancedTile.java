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

public class AdvancedTile extends Tile {
	protected final String texture;
	protected final float offsetX;
	protected final float offsetY;
	protected final boolean shadow;
	protected boolean legacy_shadow = false;

	public AdvancedTile(int c, String texture, float offsetX, float offsetY, boolean solid, boolean shadow) {
		super(c);
		this.texture = texture;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		setSolid(solid);
		this.shadow = shadow;
	}

	public AdvancedTile(int c, String texture, float offsetX, float offsetY, boolean shadow) {
		this(c, texture, offsetX, offsetY, true, shadow);
	}

	public AdvancedTile(int c, String texture, boolean shadow) {
		this(c, texture, 0, 0, shadow);
	}

	public AdvancedTile(int c, String texture, boolean solid, boolean shadow) {
		this(c, texture, 0, 0, solid, shadow);
	}

	@Override
	public String getTexture(long x, long y, long z, String state, World world) {
		return texture;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, long x, long y, long z, String state,
			World world, Collection<LightSource> lights, float sx, float sy) throws SlickException {
		Image img = TextureMap.getTexture(getTexture(x, y, z, state, world));

		if (shadow && RPGConfig.isShadowEnabled()) {
			float shadow_l = (float) ServerManager.getServer().request("get shadow_len");
			if (shadow_l > 0) {
				if (legacy_shadow) {
					if (world.getTile(x, y - 1, z) != this) {
						Image shadow = img.getScaledCopy(img.getWidth(), (int) (img.getHeight() * shadow_l));
						shadow.setFilter(Image.FILTER_LINEAR);
						shadow.draw(sx * Tiles.getTileWidth() + offsetX,
								sy * Tiles.getTileHeight() + offsetY - (shadow.getHeight() - img.getHeight()),
								new Color(0.001f, 0.001f, 0.05f, getShadowAlpha(lights, x, y) / 3));
						shadow.setFilter(Image.FILTER_NEAREST);
					}
				} else {
					Image shadow = img.getScaledCopy(img.getWidth(), (int) (img.getHeight() * shadow_l));
					shadow.setFilter(Image.FILTER_LINEAR);
					shadow.drawSheared(sx * Tiles.getTileWidth() + offsetX - 8 * shadow_l,
							sy * Tiles.getTileHeight() + offsetY - (shadow.getHeight() - img.getHeight()), 8 * shadow_l,
							0, new Color(0.001f, 0.001f, 0.05f, getShadowAlpha(lights, x, y)));
					shadow.setFilter(Image.FILTER_NEAREST);
				}
			}
		}

		renderSegment(g, world, lights, x, y, img, sx, sy);
	}

	public float getShadowAlpha(Collection<LightSource> lights, float x, float y) {
		double closest = Double.POSITIVE_INFINITY;

		for (LightSource l : lights) {
			double dist = FastMath.hypot(x - l.getLX(), y - l.getLY());

			if (dist < closest) {
				closest = dist;
			}
		}

		float a = 0.5f * (float) closest * 10;

		if (a > 0.5f) {
			a = 0.5f;
		}

		return a;
	}

	public void renderSegment(Graphics g, World world, Collection<LightSource> lights, float x, float y, Image img,
			float sx, float sy) throws SlickException {
		g.drawImage(img, sx * Tiles.getTileWidth() + offsetX, sy * Tiles.getTileHeight() + offsetY);
	}

	public boolean isLegacyShadow() {
		return legacy_shadow;
	}

	public AdvancedTile setLegacyShadow(boolean legacy_shadow) {
		this.legacy_shadow = legacy_shadow;
		return this;
	}
	
	public void renderOverlay(GameContainer container, StateBasedGame game, Graphics g, long x, long y, long z,
			String state, World world, Collection<LightSource> lights, String texture, float sx, float sy) throws SlickException {
		Image img = TextureMap.getTexture(texture);
		
		g.drawImage(img, sx * 32, sy * 32);
	}
	
	@Override
	public boolean isBatch() {
		return shadow && RPGConfig.isShadowEnabled();
	}
}