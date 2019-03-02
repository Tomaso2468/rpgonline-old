package rpgonline.tile;

import rpgonline.world.World;

/**
 * An empty tile that has no collision.
 * @author Tomas
 */
public class EmptyTile extends Tile {
	/**
	 * The singleton instance of this class.
	 */
	public static final EmptyTile EMPTY_TILE = new EmptyTile();
	/**
	 * Singleton constructor.
	 */
	private EmptyTile() {
		super(0);
		setRender(false);
		setSolid(false);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTexture(long x, long y, long z, String state, World world) {
		return "air";
	}
}
