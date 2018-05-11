package kvstore.persister;

import java.util.Map;

public interface Persister {
	
	Map<String, String> load();
	
	void add(String key, String value);
	
	void remove(String key);
	
	void save(Map<String, String> data);
	
}
