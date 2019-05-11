package rpgonline.net.logon;

import java.security.SecureRandom;

public class UserServerUtils {
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static byte[] genErrorUUID(int bytes) {
		byte[] b = new byte[bytes];

		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) 0xff;
		}

		return b;
	}

	public static byte[] generateSecure(int bytes) {
		byte[] b = new byte[bytes];

		SecureRandom r = new SecureRandom();
		r.nextBytes(b);

		return b;
	}

	public static final String ERROR8 = bytesToHex(genErrorUUID(8));
	public static final String ERROR16 = bytesToHex(genErrorUUID(16));
	public static final String ERROR32 = bytesToHex(genErrorUUID(32));
	public static final String ERROR64 = bytesToHex(genErrorUUID(64));
	public static final String ERROR128 = bytesToHex(genErrorUUID(128));
}
