package kvstore.persister.audit;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kvstore.persister.Persister;
import kvstore.persister.TypedData;
import kvstore.utils.SerializableUtils;

public class AuditPersister<K extends Serializable, V extends Serializable> implements Persister<K, V> {
	
	private final DataWriter DATA_WRITER;
	
	public AuditPersister(DataWriter dataWriter) {
		this.DATA_WRITER = dataWriter;
	}
	
	public AuditPersister(File persisterFile) {
		this(new DefaultDataWriter(persisterFile));
	}

	@Override
	public Map<K, TypedData<V>> load() {
		Map<K, TypedData<V>> ret = new HashMap<>();
		
		List<Action> actions = DATA_WRITER.loadActions();
		
		for (Action it : actions) {
			K key = SerializableUtils.fromBase64(it.getKey());
			switch (it.getOperation()) {
			case ADD:
				ret.put(key, new TypedData<V>(SerializableUtils.fromBase64(it.getValue()), it.getType()));
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
	public void add(K key, TypedData<V> value) {
		String key64 = SerializableUtils.toBase64(key);
		String val64 = SerializableUtils.toBase64(value.getData());
		
		DATA_WRITER.addAction(new Action(Operations.ADD, key64, val64, value.getMimeType()));
	}

	@Override
	public void remove(K key) {
		String key64 = SerializableUtils.toBase64(key);
		
		DATA_WRITER.addAction(new Action(Operations.REMOVE, key64, null, null));
	}

	@Override
	public void save(Map<K, TypedData<V>> data) {
		for (K it : data.keySet()) {
			add(it, data.get(it));
		}
	}

	@Override
	public void close() {
		DATA_WRITER.close();
	}
	
}
