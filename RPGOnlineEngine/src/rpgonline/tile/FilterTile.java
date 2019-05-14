package rpgonline.tile;

import java.util.Collection;

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

public class FilterTile extends AdvancedTile {
	
	public FilterTile(int c, String texture, boolean solid, boolean shadow) {
		super(c, texture, solid, shadow);
	}

	public FilterTile(int c, String texture, boolean shadow) {
		super(c, texture, shadow);
	}

	public FilterTile(int c, String texture, float offsetX, float offsetY, boolean solid, boolean shadow) {
		super(c, texture, offsetX, offsetY, solid, shadow);
	}

	public FilterTile(int c, String texture, float offsetX, float offsetY, boolean shadow) {
		super(c, texture, offsetX, offsetY, shadow);
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, long x, long y, long z, String state,
			World world, Collection<LightSource> lights, float sx, float sy) throws SlickException {
		renderTexture(container, game, g, x, y, z, state, world, lights, sx, sy, getTexture(x, y, z, state, world));
		renderFilter(container, game, g, x, y, z, state, world, lights, sx, sy, getTexture(x, y, z, state, world) + ".c", new Color(Integer.parseInt(state)));
	}
	
	public void renderTexture(GameContainer container, StateBasedGame game, Graphics g, long x, long y, long z, String state,
			World world, Collection<LightSource> lights, float sx, float sy, String texture) throws SlickException {
		Image img = TextureMap.getTexture(texture);

		if (shadow && RPGConfig.isShadowEnabled()) {
			float shadow_l = (float) ServerManager.getServer().request("get shadow_len");
			if (shadow_l > 0) {
				if (legacy_shadow) {
					if (world.getTile(x, y - 1, z) != this) {
						Image shadow = img.getScaledCopy(img.getWidth(), (int) (img.getHeight() * shadow_l));
						shadow.setFilter(Image.FILTER_LINEAR);
						shadow.draw(sx * Tiles.getTileWidth() + offsetX,
								sy * Tiles.getTileHeight() + offsetY - (shadow.getHeight() - img.getHeight()),
								new Color(0.001f, 0.001f, 0.05f, getShadowAlpha(lights, x, y) / 6));
						shadow.setFilter(Image.FILTER_NEAREST);
					}
				} else {
					Image shadow = img.getScaledCopy(img.getWidth(), (int) (img.getHeight() * shadow_l));
					shadow.setFilter(Image.FILTER_LINEAR);
					shadow.drawSheared(sx * Tiles.getTileWidth() + offsetX - 8 * shadow_l,
							sy * Tiles.getTileHeight() + offsetY - (shadow.getHeight() - img.getHeight()), 8 * shadow_l,
							0, new Color(0.001f, 0.001f, 0.05f, getShadowAlpha(lights, x, y) / 2));
					shadow.setFilter(Image.FILTER_NEAREST);
				}
			}

		}

			g.drawImage(img, sx * Tiles.getTileWidth() + offsetX, sy * Tiles.getTileHeight() + offsetY);
	}
	
	public void renderFilter(GameContainer container, StateBasedGame game, Graphics g, long x, long y, long z, String state,
			World world, Collection<LightSource> lights, float sx, float sy, String texture, Color f) throws SlickException {
		Image img = TextureMap.getTexture(texture);

		if (shadow && RPGConfig.isShadowEnabled()) {
			float shadow_l = (float) ServerManager.getServer().request("get shadow_len");
			if (shadow_l > 0) {
				if (legacy_shadow) {
					if (world.getTile(x, y - 1, z) != this) {
						Image shadow = img.getScaledCopy(img.getWidth(), (int) (img.getHeight() * shadow_l));
						shadow.setFilter(Image.FILTER_LINEAR);
						shadow.draw(sx * Tiles.getTileWidth() + offsetX,
								sy * Tiles.getTileHeight() + offsetY - (shadow.getHeight() - img.getHeight()),
								new Color(0.001f, 0.001f, 0.05f, getShadowAlpha(lights, x, y) / 6));
						shadow.setFilter(Image.FILTER_NEAREST);
					}
				} else {
					Image shadow = img.getScaledCopy(img.getWidth(), (int) (img.getHeight() * shadow_l));
					shadow.setFilter(Image.FILTER_LINEAR);
					shadow.drawSheared(sx * Tiles.getTileWidth() + offsetX - 8 * shadow_l,
							sy * Tiles.getTileHeight() + offsetY - (shadow.getHeight() - img.getHeight()), 8 * shadow_l,
							0, new Color(0.001f, 0.001f, 0.05f, getShadowAlpha(lights, x, y) / 2));
					shadow.setFilter(Image.FILTER_NEAREST);
				}
			}

		}

		img.setImageColor(f.r, f.g, f.b);

		g.drawImage(img, sx * Tiles.getTileWidth() + offsetX, sy * Tiles.getTileHeight() + offsetY);
	}
}
