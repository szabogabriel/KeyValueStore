package kvstore.persister;

import java.util.HashMap;
import java.util.Map;

public class EmptyPersister implements Persister {

	@Override
	public Map<String, String> load() {
		return new HashMap<>();
	}

	@Override
	public void add(String key, String value) {
	}

	@Override
	public void remove(String key) {
	}

	@Override
	public void save(Map<String, String> data) {
	}

}
