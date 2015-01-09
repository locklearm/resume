package old;


public enum FBModType {
	
	ADDED, MODIFIED, DELETED, UNKNOWN;
	
	public static FBModType parseString(String s) {
		
		if (s == null) {
			return UNKNOWN;
		}
		else if (s.toLowerCase().startsWith("a")) {
			return ADDED;
		}
		else if (s.toLowerCase().startsWith("m")) {
			return MODIFIED;
		}
		else if (s.toLowerCase().startsWith("d")) {
			return DELETED;
		}
		else {
			return UNKNOWN;
		}
		
	}
	
}
