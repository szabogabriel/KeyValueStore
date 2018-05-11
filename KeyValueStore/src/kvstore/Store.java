package kvstore;

import java.util.Map;

import kvstore.persister.EmptyPersister;
import kvstore.persister.Persister;

public class Store {
	
	private Persister persister = new EmptyPersister();
	
	private Map<String, String> data;
	
	public Store() {
		this (new EmptyPersister());
	}
	
	public Store(Persister persister) {
		this.persister = persister;
		data = persister.load();
	}
	
	public void setPersister(Persister persister) {
		this.persister = persister;
		this.persister.save(data);
	}
	
	public void add(String key, String data) {
		this.data.put(key, data);
		persister.add(key, data);
	}
	
	public void remove(String key) {
		this.data.remove(key);
		persister.remove(key);;
	}
	
	public void update(String key, String data) {
		if (this.data.containsKey(key)) {
			remove(key);
		}
		
		add(key, data);
	}

}
