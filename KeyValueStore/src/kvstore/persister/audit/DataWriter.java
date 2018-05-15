package kvstore.persister.audit;

import java.util.List;

public interface DataWriter extends Runnable {
	
	void addAction(Action action);
	
	List<Action> loadActions();

}
