package enums;


public enum FBNodeType {
	FILE, DIRECTORY, UNKNOWN;
	
	public static FBNodeType parseString(String s) {
		
		if (s == null
				|| "".equals(s)) {
			return FBNodeType.UNKNOWN;
		}
		
		
		if (s.toLowerCase().startsWith("f")) {
			return FBNodeType.FILE;
		}
		
		if (s.toLowerCase().startsWith("d")) {
			return FBNodeType.DIRECTORY;
		}
		
		return FBNodeType.UNKNOWN;
	}
}
