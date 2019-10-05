package kvstore.persister.empty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import kvstore.persister.Persister;
import kvstore.persister.TypedData;

public class EmptyPersister<K extends Serializable, V extends Serializable> implements Persister<K, V> {

	@Override
	public Map<K, TypedData<V>> load() {
		return new HashMap<>();
	}

	@Override
	public void add(K key, TypedData<V> value) {
	}

	@Override
	public void remove(K key) {
	}

	@Override
	public void save(Map<K, TypedData<V>> data) {
	}

	@Override
	public void close() {
		
	}

}
