package kvstore.persister.audit;

public enum Operations {
	ADD("add"),
	REMOVE("remove"),
	UNKNOWN(""),
	;
	
	private final String NAME;
	
	private Operations(String name) {
		NAME = name;
	}
	
	public static Operations getOperations(String op){
		Operations ret = Operations.UNKNOWN;
		for (Operations it : values()) {
			if (it.NAME.equals(op)) {
				ret = it;
			}
		}
		return ret;
	}
	
	public String toString() {
		return NAME;
	}
}
