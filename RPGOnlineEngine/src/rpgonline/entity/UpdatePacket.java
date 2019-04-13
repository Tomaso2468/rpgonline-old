package rpgonline.entity;

import java.util.List;

import org.newdawn.slick.util.Log;

public class UpdatePacket {
	protected final long id;
	protected final String key;
	public UpdatePacket(long id, String key) {
		this.id = id;
		this.key = key;
	}
	public long getID() {
		return id;
	}
	public String getKey() {
		return key;
	}
	public void apply(List<Entity> entities) {
		
	}
	public static Entity find(long id, List<Entity> entities) {
		for(Entity e : entities) {
			if(e.getID() == id) {
				return e;
			}
		}
		Log.error("Could not find entity with ID " + Long.toHexString(id));
		return null;
	}
	public static class UString extends UpdatePacket {
		private final String value;
		public UString(long id, String key, String value) {
			super(id, key);
			this.value = value;
		}
		@Override
		public void apply(List<Entity> entities) {
			Entity e = find(id, entities);
			
			if(e != null) {
				e.setString(key, value);
			}
		}
	}
	public static class UObject extends UpdatePacket {
		private final Object value;
		public UObject(long id, String key, Object value) {
			super(id, key);
			this.value = value;
		}
		@Override
		public void apply(List<Entity> entities) {
			Entity e = find(id, entities);
			
			if(e != null) {
				e.setObject(key, value);
			}
		}
	}
	public static class ULong extends UpdatePacket {
		private final long value;
		public ULong(long id, String key, long value) {
			super(id, key);
			this.value = value;
		}
		@Override
		public void apply(List<Entity> entities) {
			Entity e = find(id, entities);
			
			if(e != null) {
				e.setLong(key, value);
			}
		}
	}
	public static class UInt extends UpdatePacket {
		private final int value;
		public UInt(long id, String key, int value) {
			super(id, key);
			this.value = value;
		}
		@Override
		public void apply(List<Entity> entities) {
			Entity e = find(id, entities);
			
			if(e != null) {
				e.setInt(key, value);
			}
		}
	}
	public static class UFloat extends UpdatePacket {
		private final float value;
		public UFloat(long id, String key, float value) {
			super(id, key);
			this.value = value;
		}
		@Override
		public void apply(List<Entity> entities) {
			Entity e = find(id, entities);
			
			if(e != null) {
				e.setFloat(key, value);
			}
		}
	}
	public static class UDouble extends UpdatePacket {
		private final double value;
		public UDouble(long id, String key, double value) {
			super(id, key);
			this.value = value;
		}
		@Override
		public void apply(List<Entity> entities) {
			Entity e = find(id, entities);
			
			if(e != null) {
				e.setDouble(key, value);
			}
		}
	}
	public static class UBoolean extends UpdatePacket {
		private final boolean value;
		public UBoolean(long id, String key, boolean value) {
			super(id, key);
			this.value = value;
		}
		@Override
		public void apply(List<Entity> entities) {
			Entity e = find(id, entities);
			
			if(e != null) {
				e.setBoolean(key, value);
			}
		}
	}
}
