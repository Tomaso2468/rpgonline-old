package rpgonline.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.util.FastMath;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import rpgonline.MusicManager;
import rpgonline.entity.Entity;
import rpgonline.gui.GUIItem;
import rpgonline.net.ServerManager;
import rpgonline.post.MultiEffect;
import rpgonline.post.NullPostProcessEffect;
import rpgonline.post.PostEffect;
import rpgonline.post.pack.PotatoShaderPack;
import rpgonline.tile.Tile;
import rpgonline.tile.Tiles;
import rpgonline.world.LightSource;
import rpgonline.world.World;

/**
 * A state for rendering worlds
 * 
 * @author Tomas
 */
public class WorldState extends BasicGameState {
	/**
	 * This state's id.
	 */
	private int id;
	/**
	 * The cached {@code x} position of the player.
	 */
	private double x;
	/**
	 * The cached {@code y} position of the player.
	 */
	private double y;
	/**
	 * The world zoom.
	 */
	public float zoom = 2.75f;
	/**
	 * The strength of the shake effect.
	 */
	public float shake = 1f;
	/**
	 * The scale to scale the graphics by. This also effects GUI.
	 */
	public float base_scale = 1f;
	/**
	 * A list of GUI elements.
	 */
	private List<GUIItem> guis = new ArrayList<GUIItem>();
	/**
	 * A list of tasks to run on game update.
	 */
	private List<UpdateHook> hooks = new ArrayList<UpdateHook>();
	/**
	 * The last game delta.
	 */
	public int last_delta;
	/**
	 * A buffer for shader effects.
	 */
	private Image buffer;

	/**
	 * A buffer for lighting.
	 */
	private Image lightBuffer;

	/**
	 * The current shader effect.
	 */
	private PostEffect post = new MultiEffect(new PotatoShaderPack());

	private boolean gui = true;

	private float gui_cooldown = 0.25f;

	/**
	 * Creates a new {@code WorldState}.
	 * 
	 * @param id the ID of the state.
	 */
	public WorldState(int id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		if (buffer == null) {
			buffer = new Image(container.getWidth(), container.getHeight());
		} else if (container.getWidth() != buffer.getWidth() || container.getHeight() != buffer.getHeight()) {
			buffer.destroy();
			buffer = new Image(container.getWidth(), container.getHeight());
		}

		render2(container, game, g);

		g.resetTransform();

		g.copyArea(buffer, 0, 0);

		post.doPostProcess(container, game, buffer, g);

		if (gui) {
			Rectangle world_clip = g.getWorldClip();
			Rectangle clip = g.getClip();
			for (GUIItem gui : guis) {
				if (gui.isCentered()) {
					g.translate(container.getWidth() / 2, container.getHeight() / 2);
					g.scale(base_scale, base_scale);
				} else {
					g.scale(base_scale, base_scale);
				}
				gui.render(g, container, game, container.getWidth() / base_scale, container.getHeight() / base_scale);

				g.resetTransform();
				g.setWorldClip(world_clip);
				g.setClip(clip);
			}
		}
	}

	/**
	 * A method that renders the world.
	 */
	public void render2(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		List<LightSource> lights = ServerManager.getServer().getLights();

		lights.sort(new Comparator<LightSource>() {
			@Override
			public int compare(LightSource o1, LightSource o2) {
				double dist1 = FastMath.hypot(x - o1.getLX(), y - o1.getLY());
				double dist2 = FastMath.hypot(x - o2.getLX(), y - o2.getLY());

				if (dist1 < dist2) {
					return -1;
				}

				if (dist1 > dist2) {
					return 1;
				}

				return 0;
			}
		});

		for (int i = 0; i < lights.size(); i++) {
			if (i < lights.size()) {
				LightSource l = lights.get(i);
				double dist = FastMath.hypot(x - l.getLX(), y - l.getLY());
				if (dist > 40 * l.getBrightness()) {
					lights.remove(l);
					i -= 1;
				}
			}
		}

		while (lights.size() > 32) {
			lights.remove(lights.size() - 1);
		}

		g.translate(container.getWidth() / 2, container.getHeight() / 2);

		World world = ServerManager.getServer().getWorld();

		g.scale(base_scale, base_scale);
		g.pushTransform();

		g.scale(zoom, zoom);
		// g.translate((float) -x * Tiles.getTileWidth() % 32, (float) -y *
		// Tiles.getTileHeight() % 32);
		if (shake > 0) {
			g.translate((float) (FastMath.random() * shake * 5), (float) (FastMath.random() * shake * 5));
		}
		float sx = (float) (x * Tiles.getTileWidth()) / 32;
		float sy = (float) (y * Tiles.getTileWidth()) / 32;

		long dist_x = (long) (container.getWidth() / base_scale / zoom / Tiles.getTileWidth() / 2) + 5;
		long dist_y = (long) (container.getHeight() / base_scale / zoom / Tiles.getTileHeight() / 2) + 10;

		Collection<Entity> entities1 = ServerManager.getServer().getEntities();
		List<Entity> entities = new ArrayList<Entity>();

		Rectangle screen_bounds = new Rectangle((float) (x - dist_x), (float) (y - dist_y), (float) (dist_x * 2),
				(float) (dist_y * 2));

		synchronized (entities1) {
			for (Entity e : entities1) {
				if (screen_bounds.contains((float) e.getX(), (float) e.getY())) {
					entities.add(e);
				}
			}
		}

		long mix = (long) (x - dist_x);
		long max = (long) (x + dist_x);
		long miy = (long) (y - dist_y);
		long may = (long) (y + dist_y);
		long miz = world.getMinZ();
		long maz = world.getMaxZ();
		for (long z = maz; z >= miz; z--) {
			for (long y = miy; y < may; y++) {
				for (long x = mix; x < max; x++) {
					Tile t = world.getTile(x, y, z);

					if (t.shouldRender()) {
						t.render(container, game, g, x, y, z, world.getTileState(x, y, z), world, lights, x - sx,
								y - sy);
					}

					if (z == -1) {
						synchronized (entities) {
							for (Entity e : entities) {
								synchronized (e) {
									if (!e.isFlying()) {
										if (FastMath.round(e.getX() + 0.5f) == x
												&& FastMath.floor(e.getY() - 0.25) == y) {
											e.render(container, game, g, e.getX(), e.getY(), z, world, lights,
													(float) e.getX() - sx, (float) e.getY() - sy);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		synchronized (entities) {
			for (Entity e : entities) {
				synchronized (e) {
					if (e.isFlying()) {
						e.render(container, game, g, e.getX(), e.getY(), -3, world, lights, (float) e.getX() - sx,
								(float) e.getY() - sy);
					}
				}
			}
		}

		g.popTransform();
		g.resetTransform();

		Graphics sg = g;

		if (lightBuffer == null) {
			lightBuffer = new Image(container.getWidth(), container.getHeight());
		} else if (container.getWidth() != lightBuffer.getWidth() || container.getHeight() != lightBuffer.getHeight()) {
			lightBuffer.destroy();
			lightBuffer = new Image(container.getWidth(), container.getHeight());
		}

		g = lightBuffer.getGraphics();

		g.clear();

		g.setDrawMode(Graphics.MODE_NORMAL);

		g.translate(container.getWidth() / 2, container.getHeight() / 2);

		g.scale(base_scale, base_scale);
		g.pushTransform();

		g.scale(zoom, zoom);
		if (shake > 0) {
			g.translate((float) (FastMath.random() * shake * 5), (float) (FastMath.random() * shake * 5));
		}

		for (long y = miy; y < may; y++) {
			for (long x = mix; x < max; x++) {
				renderLightingTile(g, x, y, x - sx, y - sy, lights, world);
			}
		}

		sg.resetTransform();

		sg.setDrawMode(Graphics.MODE_NORMAL);
		sg.drawImage(lightBuffer, 0, 0);

		sg.setDrawMode(Graphics.MODE_NORMAL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		this.last_delta = delta;

		float delf = delta / 1000f;

		x = ServerManager.getServer().getX() + 0.5;
		y = ServerManager.getServer().getY() - 0.1;

		double multi = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? ServerManager.getServer().getSprintMultiplier() : 1;

		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			ServerManager.getServer().moveY(-ServerManager.getServer().getPlayerSpeed() * multi * delf);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			ServerManager.getServer().moveX(-ServerManager.getServer().getPlayerSpeed() * multi * delf);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			ServerManager.getServer().moveY(ServerManager.getServer().getPlayerSpeed() * multi * delf);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			ServerManager.getServer().moveX(ServerManager.getServer().getPlayerSpeed() * multi * delf);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_EQUALS)) {
			zoom *= 1 + 0.25f * delf;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_MINUS)) {
			zoom /= 1 + 0.25f * delf;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_0)) {
			zoom = 2.25f;
		}
		if (zoom > 190) {
			zoom = 190;
		}
		if (zoom < 1f) {
			zoom = 1f;
		}

		ServerManager.getServer().getWorld().doUpdateClient();

		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			exit();
		}

		if (shake > 0) {
			shake -= delf;
		}

		for (GUIItem gui : guis) {
			gui.update(container, game, delta);
		}

		for (UpdateHook hook : hooks) {
			hook.update(container, game, delta);
		}

		String music = ServerManager.getServer().getMusic();
		if (music == null) {
			MusicManager.setMusic(null, false);
		} else if (!music.equals("last")) {
			MusicManager.setMusic(music, false);
		}

		gui_cooldown -= delf;
		if (Keyboard.isKeyDown(Keyboard.KEY_F1) && gui_cooldown <= 0) {
			gui = !gui;
			gui_cooldown = 0.25f;
		}
	}

	public void renderLightingTile(Graphics g, long x, long y, float sx, float sy, List<LightSource> lights,
			World world) throws SlickException {

	}

	public void exit() {
		System.exit(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getID() {
		return id;
	}

	/**
	 * Adds a GUI to the state.
	 * 
	 * @param e A GUI element.
	 */
	public void addGUI(GUIItem e) {
		guis.add(e);
	}

	/**
	 * Removes a GUI from the state.
	 * 
	 * @param o A GUI element.
	 */
	public void removeGUI(GUIItem o) {
		guis.remove(o);
	}

	/**
	 * Adds a GUI element at a specific point.
	 * 
	 * @param index   The index of the element.
	 * @param element A GUI element.
	 */
	public void addGUI(int index, GUIItem element) {
		guis.add(index, element);
	}

	/**
	 * Adds a task to perform during a game update.
	 * 
	 * @param e An update hook.
	 */
	public void addHook(UpdateHook e) {
		hooks.add(e);
	}

	/**
	 * Removes a task from the state.
	 * 
	 * @param o An update hook.
	 */
	public void removeHook(UpdateHook o) {
		hooks.remove(o);
	}

	/**
	 * Adds an task at a specific point.
	 * 
	 * @param index   The index of the element.
	 * @param element An update hook.
	 */
	public void addHook(int index, UpdateHook element) {
		hooks.add(index, element);
	}

	/**
	 * Gets the current shader effect.
	 * 
	 * @return A {@code PostEffect} object.
	 */
	public PostEffect getPost() {
		return post;
	}

	/**
	 * Sets the current shader effect.
	 * 
	 * @param post A {@code PostEffect} object.
	 */
	public void setPost(PostEffect post) {
		if (post == null) {
			post = NullPostProcessEffect.INSTANCE;
		}
		this.post = post;
	}

	public boolean isGuiShown() {
		return gui;
	}

	public void setGuiShown(boolean gui) {
		this.gui = gui;
	}
}
