package rpgonline.tile;

import java.util.Collection;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import rpgonline.TextureMap;
import rpgonline.world.LightSource;
import rpgonline.world.World;
import rpgonline.world.Entity;

/**
 * A class for tiles in a 2D top-down world.
 * @author Tomas
 */
public abstract class Tile {
	/**
	 * The next ID for tiles.
	 */
	private static volatile int nextID = -1;
	/**
	 * An empty tile that has no collision.
	 */
	public static final EmptyTile EMPTY_TILE = EmptyTile.EMPTY_TILE;
	/**
	 * If any render calls should occur for this tile.
	 */
	private boolean render = true;
	/**
	 * If the tile should have a collision box.
	 */
	private boolean solid = true;
	/**
	 * The ID of the tile.
	 */
	private final int id;
	/**
	 * The map colour of the tile.
	 */
	private final int c;

	/**
	 * Create a new tile.
	 * @param id The ID of the tile.
	 * @param c The colour of the tile on a map.
	 */
	public Tile(int id, int c) {
		this.id = id;
		this.c = c;
		Tiles.tiles.add(this);
	}

	/**
	 * Create a new tile.
	 * @param c The colour of the tile on a map.
	 */
	public Tile(int c) {
		this(getNextID(), c);
	}

	/**
	 * Gets the next ID for a tile.
	 * @return An integer.
	 */
	private static int getNextID() {
		nextID += 1;
		return nextID;
	}

	/**
	 * If the tile should render.
	 * @return {@code true} if the {@code render} method should be called. {@code false} otherwise.
	 */
	public boolean shouldRender() {
		return render;
	}

	/**
	 * Sets if the tile should render.
	 * @param render {@code true} if the {@code render} method should be called. {@code false} otherwise.
	 */
	public void setRender(boolean render) {
		this.render = render;
	}

	/**
	 * Gets the ID of the tile.
	 * @return An integer.
	 */
	public int getID() {
		return id;
	}

	/**
	 * Gets the basic texture for this tile.
	 * @param x The {@code x} position of this tile.
	 * @param y The {@code y} position of this tile.
	 * @param z The {@code z} position of this tile.
	 * @param state The state of the tile.
	 * @param world The game world.
	 * @return A non-null string.
	 */
	public abstract String getTexture(long x, long y, long z, String state, World world);

	/**
	 * Render the tile.
	 * @param container The container holding the game.
	 * @param game The game.
	 * @param g The screen's graphics.
	 * @param x The {@code x} position of this tile.
	 * @param y The {@code y} position of this tile.
	 * @param z The {@code z} position of this tile.
	 * @param state The state of the tile.
	 * @param world The game world.
	 * @param lights A list of nearby lights.
	 * @param sx The {@code x} position (in tiles) of this tile on the screen.
	 * @param sy The {@code y} position (in tiles) of this tile on the screen.
	 * @throws SlickException If an error occurs rendering.
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g, long x, long y, long z, String state,
			World world, Collection<LightSource> lights, float sx, float sy) throws SlickException {
		Image img = TextureMap.getTexture(getTexture(x, y, z, state, world));
		
		float[] colors1 = getLighting(world, lights, x, y);
		img.setColor(Image.TOP_LEFT, colors1[0], colors1[1], colors1[2]);
		
		float[] colors2 = getLighting(world, lights, x + 1, y + 1);
		img.setColor(Image.BOTTOM_RIGHT, colors2[0], colors2[1], colors2[2]);
		
		float[] colors3 = getLighting(world, lights, x + 1, y);
		img.setColor(Image.TOP_RIGHT, colors3[0], colors3[1], colors3[2]);
		
		float[] colors4 = getLighting(world, lights, x, y + 1);
		img.setColor(Image.BOTTOM_LEFT, colors4[0], colors4[1], colors4[2]);
		
		g.drawImage(img, sx * Tiles.getTileWidth(), sy * Tiles.getTileHeight());
	}
	
	/**
	 * Gets the appropriate lighting.
	 * @param world The game world.
	 * @param lights A list of nearby lights.
	 * @param x The {@code x} position of this tile.
	 * @param y The {@code y} position of this tile.
	 * @return A shared float array in the format RGB of size 3.
	 */
	protected float[] getLighting(World world, Collection<LightSource> lights, float x, float y) {
		return Entity.getLighting(world, lights, x, y);
	}

	/**
	 * Set if the tile should have a collision box.
	 * @param state The state of the tile.
	 * @return {@code true} if the tile should have a collision box. {@code false} otherwise.
	 */
	public boolean isSolid(String state) {
		return solid;
	}
	/**
	 * If the tile should have a collision box.
	 * @return {@code true} if the tile should have a collision box. {@code false} otherwise.
	 */
	public void setSolid(boolean solid) {
		this.solid = solid;
	}
	
	/**
	 * Cause a world update to the tile.
	 * @param delf A float measuring the time in seconds since the last update.
	 * @param x The {@code x} position of this tile.
	 * @param y The {@code y} position of this tile.
	 * @param z The {@code z} position of this tile.
	 */
	public void update(float delf, long x, long y, long z) {
		
	}

	/**
	 * Gets the map colour of this tile.
	 * @return An ARGB integer.
	 */
	public int getTileColor() {
		return c;
	}
}
