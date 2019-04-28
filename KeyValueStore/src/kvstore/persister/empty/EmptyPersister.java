package kvstore.persister.empty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import kvstore.persister.Persister;

public class EmptyPersister<K extends Serializable, V extends Serializable> implements Persister<K, V> {

	@Override
	public Map<K, V> load() {
		return new HashMap<>();
	}

	@Override
	public void add(K key, V value) {
	}

	@Override
	public void remove(K key) {
	}

	@Override
	public void save(Map<K, V> data) {
	}

	@Override
	public void close() {
		
	}

}
