package old;


public enum FBNodeKind {
	
	DIRECTORY, FILE, UNKNOWN;
	
	public static FBNodeKind parseString(String s) {
		
		if (s == null) {
			return UNKNOWN;
		}
		else if (s.toLowerCase().startsWith("fil")) {
			return FILE;
		}
		else if (s.toLowerCase().startsWith("dir")) {
			return DIRECTORY;
		}
		else {
			return UNKNOWN;
		}
		
	}
	
}
