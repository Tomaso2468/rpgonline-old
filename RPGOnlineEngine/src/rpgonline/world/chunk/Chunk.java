package rpgonline.world.chunk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import rpgonline.tile.Tile;
import rpgonline.tile.Tiles;

public class Chunk {
	public static final int SIZE = 64;
	private volatile long x;
	private volatile long y;
	private volatile long z;
	private volatile Tile[][][] tiles = new Tile[1][SIZE][SIZE];
	private volatile String[][][] states = new String[1][SIZE][SIZE];
	private volatile boolean[][][] flag = new boolean[1][SIZE][SIZE];
	private volatile String[][][] area = new String[1][SIZE][SIZE];
	private volatile int[][][] biome = new int[1][SIZE][SIZE];
	private volatile float[][][] humidity = new float[1][SIZE][SIZE];
	private volatile float[][][] temperature = new float[1][SIZE][SIZE];
	private boolean change = false;
	private long lastUsed = System.currentTimeMillis();
	public Chunk(long x, long y, long z) {
		this.x = x;
		this.y = y;
		this.z = z;
		for(int tx = 0; tx < tiles[0].length; tx++) {
			for(int ty = 0; ty < tiles[0][0].length; ty++) {
				for(int tz = 0; tz < tiles.length; tz++) {
					tiles[tz][tx][ty] = Tile.EMPTY_TILE;
					states[tz][tx][ty] = "";
					area[tz][tx][ty] = null;
				}
			}
		}
	}
	public synchronized Tile getTile(long x, long y, long z) {
		lastUsed = System.currentTimeMillis();
		return tiles[(int) z][(int) x][(int) y];
	}
	public synchronized void setTile(long x, long y, long z, Tile tile) {
		lastUsed = System.currentTimeMillis();
		change = true;
		tiles[(int) z][(int) x][(int) y] = tile;
	}
	public synchronized String getState(long x, long y, long z) {
		lastUsed = System.currentTimeMillis();
		return states[(int) z][(int) x][(int) y];
	}
	public synchronized void setState(long x, long y, long z, String state) {
		lastUsed = System.currentTimeMillis();
		change = true;
		states[(int) z][(int) x][(int) y] = state;
	}
	public synchronized boolean getFlag(long x, long y, long z) {
		lastUsed = System.currentTimeMillis();
		return flag[(int) z][(int) x][(int) y];
	}
	public synchronized void setFlag(long x, long y, long z, boolean f) {
		lastUsed = System.currentTimeMillis();
		change = true;
		flag[(int) z][(int) x][(int) y] = f;
	}
	public synchronized String getArea(long x, long y, long z) {
		lastUsed = System.currentTimeMillis();
		return area[(int) z][(int) x][(int) y];
	}
	public synchronized void setArea(long x, long y, long z, String id) {
		lastUsed = System.currentTimeMillis();
		change = true;
		area[(int) z][(int) x][(int) y] = id;
	}
	public synchronized int getBiome(long x, long y, long z) {
		lastUsed = System.currentTimeMillis();
		return biome[(int) z][(int) x][(int) y];
	}
	public synchronized void setBiome(long x, long y, long z, int id) {
		lastUsed = System.currentTimeMillis();
		change = true;
		biome[(int) z][(int) x][(int) y] = id;
	}
	public synchronized float getTemperature(long x, long y, long z) {
		lastUsed = System.currentTimeMillis();
		return temperature[(int) z][(int) x][(int) y];
	}
	public synchronized void setTemperature(long x, long y, long z, float id) {
		lastUsed = System.currentTimeMillis();
		change = true;
		temperature[(int) z][(int) x][(int) y] = id;
	}
	public synchronized float getHumidity(long x, long y, long z) {
		lastUsed = System.currentTimeMillis();
		return humidity[(int) z][(int) x][(int) y];
	}
	public synchronized void setHumidity(long x, long y, long z, float id) {
		lastUsed = System.currentTimeMillis();
		change = true;
		humidity[(int) z][(int) x][(int) y] = id;
	}
	public long getX() {
		return x;
	}
	public long getY() {
		return y;
	}
	public long getZ() {
		return z;
	}
	public boolean isAt(long cx, long cy, long cz) {
		return x == cx && y == cy && z == cz;
	}
	public void serialise(File f) throws IOException {
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(f));
		
		dos.writeByte(0);
		
		for(long z = 0; z < tiles.length; z++) {
			for(long x = 0; x < tiles[0].length; x++) {
				for(long y = 0; y < tiles[0][0].length; y++) {
					dos.writeInt(tiles[(int) z][(int) x][(int) y].getID());
					dos.writeUTF(states[(int) z][(int) x][(int) y]);
				}
			}
			dos.flush();
		}
		
		dos.close();
	}
	public void read(File f) throws IOException {
		change = true;
		DataInputStream dis = new DataInputStream(new FileInputStream(f));
		
		byte ver = dis.readByte();
		
		if (ver != 0) {
			dis.close();
			throw new IOException("Error reading chunk: Unknown version number: 0x" + Integer.toHexString(ver));
		}
		
		for(long z = 0; z < tiles.length; z++) {
			for(long x = 0; x < tiles[0].length; x++) {
				for(long y = 0; y < tiles[0][0].length; y++) {
					int id = dis.readInt();
					tiles[(int) z][(int) x][(int) y] = Tiles.getByID(id);
					states[(int) z][(int) x][(int) y] = dis.readUTF();
				}
			}
		}
		
		dis.close();
	}
	public boolean isChanged() {
		return change;
	}
	public void setChanged(boolean change) {
		this.change = change;
	}
	public long getLastUsed() {
		return lastUsed;
	}
}
