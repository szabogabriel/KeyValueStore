package kvstore.persister.sqlite;

import java.util.Set;

public interface LazyMapDataProvider<K, V> {
	
	void clear();
	
	int size();
	
	Set<K> keys();
	
	boolean isKnownData(V value);
	
	V read(K key);
	
	void store(K key, V value);
	
	void remove(K key);

}
