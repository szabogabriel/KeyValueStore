package kvstore.persister.audit;

public class Action {
	private final String KEY;
	private final String VALUE;
	private final String TYPE;
	private final Operations OPERATION;
	
	public Action(Operations operation, String key, String value, String mimeType) {
		this.KEY = key;
		this.VALUE = value;
		this.TYPE = mimeType;
		this.OPERATION = operation;
	}
	
	public Action(String data) {
		String [] tmp = data.split(",");
		if (tmp.length >= 2) {
			OPERATION = Operations.getOperations(tmp[0]);
			TYPE = tmp[1];
			KEY = tmp[2];
			if (tmp.length == 4) {
				VALUE = tmp[3];
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
	
	public String getType() {
		return TYPE;
	}
	
	@Override
	public String toString() {
		StringBuilder toWrite = new StringBuilder();
		toWrite
			.append(OPERATION.toString())
			.append(",");
		if (TYPE != null) {
			toWrite.append(TYPE);
		}
		toWrite.append(",")
			.append(KEY);
		if (VALUE != null) {
			toWrite
				.append(",")
				.append(VALUE);
		}
		toWrite.append(System.lineSeparator());
		return toWrite.toString();
	}
}
