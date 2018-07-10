package kvstore.persister.sqlite;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class LazyMap<K, V> implements Map<K, V> {
	
	private LazyMapDataProvider<K, V> PROVIDER;
	
	private Set<K> keys = null;
	
	public LazyMap(LazyMapDataProvider<K, V> provider) {
		PROVIDER = provider;
	}
	
	@Override
	public int size() {
		return PROVIDER.size();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean containsKey(Object key) {
		return keySet().contains((K)key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean containsValue(Object value) {
		return PROVIDER.isKnownData((V)value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		return PROVIDER.read((K)key);
	}

	@Override
	public V put(K key, V value) {
		PROVIDER.store(key, value);
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(Object key) {
		V ret = get(key);
		PROVIDER.remove((K) key);
		return ret;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		m.keySet().stream().forEach(k -> PROVIDER.store(k, m.get(k)));
	}

	@Override
	public void clear() {
		PROVIDER.clear();
	}

	@Override
	public Set<K> keySet() {
		if (keys == null) {
			keys = PROVIDER.keys();
		}
		return keys;
	}

	@Override
	public Collection<V> values() {
		return null;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return null;
	}

}
