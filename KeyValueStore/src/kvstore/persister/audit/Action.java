package kvstore.persister.audit;

public class Action {
	private final String KEY;
	private final String VALUE;
	private final Operations OPERATION;
	
	public Action(Operations operation, String key, String value) {
		this.KEY = key;
		this.VALUE = value;
		this.OPERATION = operation;
	}
	
	public Action(String data) {
		String [] tmp = data.split(",");
		if (tmp.length >= 2) {
			KEY = tmp[1];
			OPERATION = Operations.getOperations(tmp[0]);
			if (tmp.length == 3) {
				VALUE = tmp[2];
			} else {
				VALUE = null;
			}
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public String getKey() {
		return KEY;
	}
	
	public String getValue() {
		return VALUE;
	}
	
	public Operations getOperation() {
		return OPERATION;
	}
	
	@Override
	public String toString() {
		StringBuilder toWrite = new StringBuilder();
		toWrite
		.append(Operations.ADD.toString())
		.append(",")
		.append(KEY)
		.append(",")
		.append(VALUE)
		.append(System.lineSeparator());
		return toWrite.toString();
	}
}
