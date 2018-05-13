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

public class AuditPersister<K extends Serializable, V extends Serializable> implements Persister<K, V> {
	
	private final File AUDITFILE;
	
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
	}

	@Override
	public Map<K, V> load() {
		Map<K, V> ret = new HashMap<>();
		
		try (BufferedReader in = new BufferedReader(new FileReader(AUDITFILE))) {
			String line;
			
			while ((line = in.readLine()) != null) {
				String [] data = line.split(",");
				
				if (data.length > 1) {
					K key = fromBase64(data[1]);
					switch (Operations.getOperations(data[0])) {
					case ADD:
						if (data.length == 3) {
							ret.put(key, fromBase64(data[2]));
						}
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
		StringBuilder toWrite = new StringBuilder();
		toWrite
			.append(Operations.ADD.toString())
			.append(",")
			.append(key64)
			.append(",")
			.append(val64)
			.append(System.lineSeparator());
		
		writeToFile(toWrite.toString());
	}

	@Override
	public void remove(K key) {
		String key64 = toBase64(key);
		StringBuilder toWrite = new StringBuilder();
		toWrite
			.append(Operations.REMOVE.toString())
			.append(",")
			.append(key64)
			.append(System.lineSeparator());
		
		writeToFile(toWrite.toString());
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
	
}
