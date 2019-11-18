package kvstore.persister;

import java.io.Serializable;
import java.util.Map;

public interface Persister<K extends Serializable, V extends Serializable> {
	
	Map<K, TypedData<V>> load();
	
	void add(K key, TypedData<V> value);
	
	void remove(K key);
	
	void save(Map<K, TypedData<V>> data);
	
	void close();
	
}
