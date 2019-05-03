package rpgonline.world.chunk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.newdawn.slick.util.Log;

import rpgonline.tile.Tiles;
import rpgonline.world.World;

public class ChangesChunkWorld extends ChunkWorld {
	public static final int WORLD_VERSION = 1;
	private File f;
	public ChangesChunkWorld(File f) {
		this.f = f;
	}
	@Override
	public void save() {
		try {
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
			
			dos.writeInt(WORLD_VERSION);
			dos.writeLong(System.currentTimeMillis());
			
			for(long z = getMinZ(); z <= getMaxZ(); z++) {
				for(long y = getMinY(); y <= getMaxY(); y++) {
					for(long x = getMinX(); x <= getMaxX(); x++) {
						dos.writeLong(x);
						dos.writeLong(y);
						dos.writeLong(z);
						dos.writeInt(getTile(x, y, z).getID());
						dos.writeUTF(getTileState(x, y, z));
					}
				}
				dos.flush();
			}
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static World read(File f) throws IOException {
		DataInputStream header = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
		
		int version = header.readInt();
		long date = header.readLong();
		
		header.close();
		
		Log.info("Loading world " + f.getName() + " from " + new Date(date) + " version " + version);
		
		if(version == 0) {
			return new DiskChunkWorld(f);
		}
		
		if(version == 1) {
			ChangesChunkWorld world = new ChangesChunkWorld(f);
			
			DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
			
			//Skip header
			dis.skip(Integer.SIZE + Long.SIZE);
			
			while(dis.available() > 0) {
				long x = dis.readLong();
				long y = dis.readLong();
				long z = dis.readLong();
				int t = dis.readInt();
				String state = dis.readUTF();
				
				world.setTile(x, y, z, Tiles.getByID(t), state);
			}
			
			dis.close();
			
			return world;
		}
		
		throw new IOException("Unknown world version ID");
	}
}
