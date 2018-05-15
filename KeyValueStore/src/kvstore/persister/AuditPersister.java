package kvstore.persister;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AuditPersister<K extends Serializable, V extends Serializable> implements Persister<K, V>, Runnable {
	
	private final File AUDITFILE;
	
	private final ConcurrentLinkedQueue<Action> EVENT_BUFFER = new ConcurrentLinkedQueue<>();
	
	private enum Operations {
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
	
	public AuditPersister(File persisterFile) {
		AUDITFILE = persisterFile;
		
		new Thread(this).start();
	}

	@Override
	public Map<K, V> load() {
		Map<K, V> ret = new HashMap<>();
		
		try (BufferedReader in = new BufferedReader(new FileReader(AUDITFILE))) {
			String line;
			
			while ((line = in.readLine()) != null) {
				try {
					Action data = new Action(line);
				
					K key = fromBase64(data.getKey());
					switch (data.getOperation()) {
					case ADD:
						ret.put(key, fromBase64(data.getValue()));
						break;
					case REMOVE:
						ret.remove(key);
						break;
					case UNKNOWN:
						//skip
						break;
					default:
						//skip
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}

	@Override
	public void add(K key, V value) {
		String key64 = toBase64(key);
		String val64 = toBase64(value);
		
		EVENT_BUFFER.offer(new Action(Operations.ADD, key64, val64));
	}

	@Override
	public void remove(K key) {
		String key64 = toBase64(key);
		
		EVENT_BUFFER.offer(new Action(Operations.REMOVE, key64, null));
	}

	@Override
	public void save(Map<K, V> data) {
		for (K it : data.keySet()) {
			add(it, data.get(it));
		}
	}
	
	private void writeToFile(String data) {
		try (FileOutputStream fos = new FileOutputStream(AUDITFILE, true)) {
			fos.write(data.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String toBase64(Serializable object) {
		String ret = null;
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ret = Base64.getEncoder().encodeToString(baos.toByteArray());
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	private <D> D fromBase64(String base64) {
		D ret = null;
		
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object o = ois.readObject();
			ret = (D)o;
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	private class Action {
		
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

	
	@Override
	public void run() {
		while (true) {
			Action tmpAction = EVENT_BUFFER.poll();
			if (tmpAction != null) {
				writeToFile(tmpAction.toString());
			} else {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
