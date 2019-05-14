package rpgonline;

public final class RPGConfig {
	private static boolean shadowEnabled = false;
	private static boolean windEnabled = false;

	public static boolean isShadowEnabled() {
		return shadowEnabled;
	}

	public static void setShadowEnabled(boolean shadowEnabled) {
		RPGConfig.shadowEnabled = shadowEnabled;
	}

	public static boolean isWindEnabled() {
		return windEnabled;
	}

	public static void setWindEnabled(boolean windEnabled) {
		RPGConfig.windEnabled = windEnabled;
	}
	
	
}
