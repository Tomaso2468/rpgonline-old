package rpgonline.net.logon;

import java.util.Arrays;
import java.util.List;

/**
 * <p>A server that supports multiple connections and will pick the best connection.</p>
 * <p>Servers are prioritised by their order in the list.</p>
 * @author Tomas
 */
public class MultiLogonServer implements UserServer {
	/**
	 * A server used if not server is available.
	 */
	private static final DownServer DOWN_SERVER = new DownServer();
	/**
	 * A list of possible servers.
	 */
	private final List<UserServer> servers;

	/**
	 * Constructs a {@code MultiLogonServer} from a list of servers.
	 * @param servers A list of servers.
	 */
	public MultiLogonServer(List<UserServer> servers) {
		this.servers = servers;
	}

	/**
	 * Constructs a {@code MultiLogonServer} from an array.
	 * @param servers An array.
	 */
	public MultiLogonServer(UserServer[] servers) {
		this(Arrays.asList(servers));
	}

	/**
	 * Gets the best server to connect to.
	 * @return A server.
	 */
	public synchronized UserServer getBestServer() {
		for (UserServer server : servers) {
			if (server.isUp()) {
				return server;
			}
		}
		return DOWN_SERVER;
	}

	/**
	 * A server used if not other server can be found.
	 * @author Tomas
	 */
	private static class DownServer implements UserServer {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public LogonStatus attemptLogon(String login, String password) {
			return LogonStatus.CONNECT_FAILIURE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isUp() {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getUserName(long uuid) {
			return "{" + String.format("%04X", uuid & 0xFFFF000000000000L >>> 48) + "-"
					+ String.format("%04X", uuid & 0x0000FFFF00000000L >>> 32) + "-"
					+ String.format("%04X", uuid & 0x00000000FFFF0000L >>> 16) + "-"
					+ String.format("%04X", uuid & 0x000000000000FFFFL >>> 0) + "}";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long getUserUuid(String login) {
			return -1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPrivateUuid(String login, String password) {
			return UserServerUtils.ERROR32;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isValidPrivateUuid(long uuid, String puuid) {
			return false;
		}

		@Override
		public long getIDFromName(String username) {
			return -1;
		}

		@Override
		public String getToken(long uuid, String puuid) {
			return UserServerUtils.ERROR64;
		}

		@Override
		public LogonStatus attemptLogon(String token) {
			return LogonStatus.CONNECT_FAILIURE;
		}

		@Override
		public long getTokenUUID(String token) {
			return -1;
		}

		@Override
		public String getTokenPUUID(String token) {
			return UserServerUtils.ERROR32;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LogonStatus attemptLogon(String login, String password) {
		return getBestServer().attemptLogon(login, password);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUp() {
		return getBestServer().isUp();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUserName(long uuid) {
		return getBestServer().getUserName(uuid);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getUserUuid(String login) {
		return getBestServer().getUserUuid(login);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPrivateUuid(String login, String password) {
		return getBestServer().getPrivateUuid(login, password);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValidPrivateUuid(long uuid, String puuid) {
		return getBestServer().isValidPrivateUuid(uuid, puuid);
	}

	@Override
	public long getIDFromName(String username) {
		return getBestServer().getIDFromName(username);
	}

	@Override
	public String getToken(long uuid, String puuid) {
		return getBestServer().getToken(uuid, puuid);
	}

	@Override
	public LogonStatus attemptLogon(String token) {
		return getBestServer().attemptLogon(token);
	}

	@Override
	public long getTokenUUID(String token) {
		return getBestServer().getTokenUUID(token);
	}

	@Override
	public String getTokenPUUID(String token) {
		return getBestServer().getTokenPUUID(token);
	}
}
