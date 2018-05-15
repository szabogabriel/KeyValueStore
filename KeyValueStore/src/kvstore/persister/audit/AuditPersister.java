package kvstore.persister.audit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kvstore.persister.Persister;

public class AuditPersister<K extends Serializable, V extends Serializable> implements Persister<K, V> {
	
	private final DataWriter DATA_WRITER;
	
	public AuditPersister(File persisterFile) {
		DATA_WRITER = new DataWriter(persisterFile);
		new Thread(DATA_WRITER).start();
	}

	@Override
	public Map<K, V> load() {
		Map<K, V> ret = new HashMap<>();
		
		List<Action> actions = DATA_WRITER.loadActions();
		
		for (Action it : actions) {
			K key = fromBase64(it.getKey());
			switch (it.getOperation()) {
				case ADD:
					ret.put(key, fromBase64(it.getValue()));
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
		
		return ret;
	}

	@Override
	public void add(K key, V value) {
		String key64 = toBase64(key);
		String val64 = toBase64(value);
		
		DATA_WRITER.addAction(new Action(Operations.ADD, key64, val64));
	}

	@Override
	public void remove(K key) {
		String key64 = toBase64(key);
		
		DATA_WRITER.addAction(new Action(Operations.REMOVE, key64, null));
	}

	@Override
	public void save(Map<K, V> data) {
		for (K it : data.keySet()) {
			add(it, data.get(it));
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
