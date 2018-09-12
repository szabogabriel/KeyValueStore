package kvstore.dataStructures;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class LazyMap<K, V> implements Map<K, V> {
	
	private final LazyMapDataProvider<K, V> PROVIDER;
	
	private final boolean CACHE_KEYS;
	
	private final Cache<K, V> CACHE_IMPL;
	
	private Set<K> keys = null;
	
	public LazyMap(LazyMapDataProvider<K, V> provider) {
		this(provider, true);
	}
	
	public LazyMap(LazyMapDataProvider<K, V> provider, boolean cacheKeys) {
		this(provider, cacheKeys, 0);
	}
	
	public LazyMap(LazyMapDataProvider<K, V> provider, boolean cacheKeys, long maxsize) {
		PROVIDER = provider;
		CACHE_KEYS = cacheKeys;
		CACHE_IMPL = (maxsize > 0) ? new Cache.CacheImpl<>(maxsize) : new Cache.EmptyCache<>();
	}
	
	@Override
	public int size() {
		return PROVIDER.size();
	}

	@Override
	public boolean isEmpty() {
		return keySet().isEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean containsKey(Object key) {
		boolean ret = CACHE_IMPL.containsKey(key);
		if (!ret) {
			if (CACHE_KEYS) {
				ret = keySet().contains((K)key);
			} else {
				ret = PROVIDER.isKnownKey((K)key);
			}
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean containsValue(Object value) {
		boolean ret = CACHE_IMPL.containsValue(value);
		if (!ret) {
			ret = PROVIDER.isKnownData((V)value);
		}
		return ret;
	}

	@Override
	public V get(Object key) {
		return get(key, true);
	}

	@Override
	public V put(K key, V value) {
		CACHE_IMPL.put(key, value);
		PROVIDER.store(key, value);
		if (CACHE_KEYS) { keys.add(key); }
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(Object key) {
		V ret = get(key, false);
		CACHE_IMPL.remove(key);
		PROVIDER.remove((K) key);
		if (CACHE_KEYS) { keys.remove(key); }
		return ret;
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		m.keySet().stream().forEach(k -> put(k, m.get(k)));
	}

	@Override
	public void clear() {
		CACHE_IMPL.clear();
		PROVIDER.clear();
		if (CACHE_KEYS) { keys.clear(); }
	}

	@Override
	public Set<K> keySet() {
		Set<K> ret = keys;
		
		if (ret == null && CACHE_KEYS) {
			keys = PROVIDER.keys();
			ret = keys;
		} else {
			ret = PROVIDER.keys();
		}
		
		return ret;
	}

	@Override
	public Collection<V> values() {
		return null;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private V get(Object key, boolean updateCache) {
		V ret = CACHE_IMPL.get(key);
		
		if  (ret == null) {
			ret = PROVIDER.read((K)key);
			if (updateCache) {
				CACHE_IMPL.put((K)key, ret);
			}
		}
		
		return ret;
	}

}
