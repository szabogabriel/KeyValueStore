package kvstore.persister.audit;

import java.util.List;

public interface DataWriter {
	
	void addAction(Action action);
	
	List<Action> loadActions();
	
	void close();

}
