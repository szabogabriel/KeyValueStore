package kvstore.dataStructures;

import java.util.Set;

public interface LazyMapDataProvider<K, V> {
	
	void clear();
	
	int size();
	
	Set<K> keys();
	
	boolean isKnownKey(K key);
	
	boolean isKnownData(V value);
	
	V read(K key);
	
	void store(K key, V value);
	
	void remove(K key);

}
