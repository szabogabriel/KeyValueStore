package kvstore.dataStructures;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface Cache<K, V> {
	
	void clear();
	
	boolean containsKey(Object key);
	
	boolean containsValue(Object value);
	
	V get(Object key);
	
	V put(K key, V value);
	
	V remove(Object key);
	
	public static class EmptyCache<K, V> implements Cache<K, V> {
		@Override public void clear() { }
		@Override public boolean containsKey(Object key) { return false; }
		@Override public boolean containsValue(Object value) { return false; }
		@Override public V get(Object key) { return null; }
		@Override public V put(K key, V value) { return null; }
		@Override public V remove(Object key) { return null; }
	}
	
	public static class CacheImpl<K, V> implements Cache<K, V> {
	
		private final long MAXSIZE;
		
		private Map<K, V> cache = new HashMap<K, V>();
		
		public CacheImpl(long maxsize) {
			MAXSIZE = maxsize;
		}
		
		@Override
		public void clear() {
			cache.clear();
		}
		
		@Override
		public boolean containsKey(Object key) {
			return cache.containsKey(key);
		}

		@Override
		public boolean containsValue(Object value) {
			return cache.containsKey(value);
		}

		@Override
		public V get(Object key) {
			return cache.get(key);
		}

		@Override
		public V put(K key, V value) {
			maintainCacheSize();
			cache.put(key, value);
			return value;
		}

		@Override
		public V remove(Object key) {
			return cache.remove(key);
		}
		
		private void maintainCacheSize() {
			while (cacheIsTooBig()) {
				removeRandomValue();
			}
		}
		
		private boolean cacheIsTooBig() {
			return cache.size() > MAXSIZE;
		}
		
		private void removeRandomValue() {
			List<K> keys = cache.keySet().stream().collect(Collectors.toList());
			int id = (int)(Math.random() * ((double)keys.size()));
			K toRemove = keys.get(id);
			cache.remove(toRemove);
		}
	
	}

}
