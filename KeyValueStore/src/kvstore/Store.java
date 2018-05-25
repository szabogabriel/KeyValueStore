package kvstore;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import kvstore.persister.Persister;
import kvstore.persister.empty.EmptyPersister;

public class Store<K extends Serializable, V extends Serializable> {
	
	private final Persister<K, V> PERSISTER;
	
	private Map<K, V> data;
	
	public Store() {
		this (new EmptyPersister<K, V>());
	}
	
	public Store(Persister<K, V> persister) {
		this.PERSISTER = persister;
		data = persister.load();
	}
	
	public void add(K key, V data) {
		this.data.put(key, data);
		PERSISTER.add(key, data);
	}
	
	public void remove(K key) {
		this.data.remove(key);
		PERSISTER.remove(key);;
	}
	
	public void update(K key, V data) {
		if (this.data.containsKey(key)) {
			remove(key);
		}
		
		add(key, data);
	}
	
	public V get(K key) {
		return get(key, null);
	}
	
	public V get(K key, V def) {
		V ret = def;
		
		if (data.containsKey(key)) {
			ret = data.get(key);
		}
		
		return ret;
	}
	
	public Set<K> getKeys() {
		return data.keySet();
	}
	
}
