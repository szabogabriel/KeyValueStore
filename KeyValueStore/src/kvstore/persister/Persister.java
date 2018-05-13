package kvstore.persister;

import java.io.Serializable;
import java.util.Map;

public interface Persister<K extends Serializable, V extends Serializable> {
	
	Map<K, V> load();
	
	void add(K key, V value);
	
	void remove(K key);
	
	void save(Map<K, V> data);
	
}
