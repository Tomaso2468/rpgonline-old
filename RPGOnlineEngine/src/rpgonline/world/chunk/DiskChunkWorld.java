package rpgonline.world.chunk;

import java.io.File;
import java.io.IOException;

import org.newdawn.slick.util.Log;

import rpgonline.tile.Tiles;

@Deprecated
public class DiskChunkWorld extends ChunkWorld {
	private final File f;
	public DiskChunkWorld(File f) {
		this.f = f;
		System.out.println("World IDs");
		Tiles.printIDs();
		if(!f.exists()) {
			f.mkdirs();
		} else {
			for(int cx = -2; cx <= 2; cx++) {
				for(int cy = -2; cy <= 2; cy++) {
					for(int cz = -1; cz <= 1; cz++) {
						if(getChunkFile(f, cx, cy, cz).exists()) {
							System.out.println("Reading chunk " + cx + " " + cy + " " + cz);
							Chunk chunk = new Chunk(cx, cy, cz);
							try {
								chunk.read(getChunkFile(f, cx, cy, cz));
							} catch (IOException e) {
								Log.error("Failed to cache chunk " + cx + " " + cy + " " + cz);
							}
							chunks.add(chunk);
							cache.add(new CacheEntry(chunk, System.currentTimeMillis()));
						}
					}
				}
			}
		}
	}
	
	public synchronized void writeChanges() throws IOException {
		for (Chunk chunk : chunks) {
			if(chunk.isChanged()) {
				chunk.serialise(getChunkFile(f, chunk.getX(), chunk.getY(), chunk.getZ()));
			}
			chunk.setChanged(false);
			if(chunk.getLastUsed() + (1000 * 60 * 10) < System.currentTimeMillis()) {
				chunk.serialise(getChunkFile(f, chunk.getX(), chunk.getY(), chunk.getZ()));
				chunks.remove(chunk);
				return;
			}
		}
	}
	
	public synchronized void writeAll() throws IOException {
		for (Chunk chunk : chunks) {
			chunk.serialise(getChunkFile(f, chunk.getX(), chunk.getY(), chunk.getZ()));
		}
	}
	
	protected synchronized Chunk getChunk(long x, long y, long z) {
		long cx = (int) Math.floor(x / (Chunk.SIZE * 1f));
		long cy = (int) Math.floor(y / (Chunk.SIZE * 1f));
		long cz = (int) Math.floor(z / (2 * 1f));
		
		synchronized(cache) {
			for(CacheEntry e : cache) {
				Chunk chunk = e.getChunk();
				if(chunk.isAt(cx, cy, cz)) {
					e.setTime(System.currentTimeMillis());
					return chunk;
				}
			}
		}
		
		for(Chunk chunk : chunks) {
			if(chunk.isAt(cx, cy, cz)) {
				cache.add(new CacheEntry(chunk, System.currentTimeMillis()));
				return chunk;
			}
		}
		
		try {
			if(getChunkFile(f, cx, cy, cz).exists()) {
				System.out.println("Reading chunk " + cx + " " + cy + " " + cz);
				Chunk chunk = new Chunk(cx, cy, cz);
				chunk.read(getChunkFile(f, cx, cy, cz));
				chunks.add(chunk);
				cache.add(new CacheEntry(chunk, System.currentTimeMillis()));
				
				return chunk;
			}
		} catch (IOException e) {
			Log.error("Failed to read chunk " + cx + " " + cy + " " + cz);
			e.printStackTrace();
		}
		
		Chunk chunk = new Chunk(cx, cy, cz);
		chunks.add(chunk);
		cache.add(new CacheEntry(chunk, System.currentTimeMillis()));
		
		return chunk;
	}
	
	private File getChunkFile(File f, long cx, long cy, long cz) {
		return new File(f, "chunk" + cx + "_" + cy + "_" + cz + ".chunk");
	}
	
	@Override
	public synchronized void doUpdateClient() {
		super.doUpdateClient();
		
		if(update_counter % (60 * 30) == 0) {
			try {
				writeChanges();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public synchronized void doUpdateServer() {
		super.doUpdateServer();
		
		if(update_counter % (60 * 60 * 2) == 0) {
			try {
				writeChanges();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void save() {
		try {
			writeAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
