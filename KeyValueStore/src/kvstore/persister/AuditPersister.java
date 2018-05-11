package kvstore.persister;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class AuditPersister implements Persister {
	
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
	public Map<String, String> load() {
		Map<String, String> ret = new HashMap<>();
		
		try (BufferedReader in = new BufferedReader(new FileReader(AUDITFILE))) {
			String line;
			
			while ((line = in.readLine()) != null) {
				String [] data = line.split(",");
				
				if (data.length > 1) {
					String key = new String(Base64.getDecoder().decode(data[1]));
					switch (Operations.getOperations(data[0])) {
					case ADD:
						if (data.length == 3) {
							ret.put(key, new String(Base64.getDecoder().decode(data[2])));
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
	public void add(String key, String value) {
		String key64 = Base64.getEncoder().encodeToString(key.getBytes());
		String val64 = Base64.getEncoder().encodeToString(value.getBytes());
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
	public void remove(String key) {
		String key64 = Base64.getEncoder().encodeToString(key.getBytes());
		StringBuilder toWrite = new StringBuilder();
		toWrite
			.append(Operations.REMOVE.toString())
			.append(",")
			.append(key64)
			.append(System.lineSeparator());
		
		writeToFile(toWrite.toString());
	}

	@Override
	public void save(Map<String, String> data) {
		for (String it : data.keySet()) {
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

}
