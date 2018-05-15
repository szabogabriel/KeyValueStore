package kvstore.persister.audit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataWriter implements Runnable {
	
	private final File AUDITFILE;
	
	private final ConcurrentLinkedQueue<Action> EVENT_BUFFER = new ConcurrentLinkedQueue<>();
	
	public DataWriter(File auditFile) {
		AUDITFILE = auditFile;
	}
	
	public void addAction(Action action) {
		EVENT_BUFFER.offer(action);
	}
	
	public List<Action> loadActions() {
		List<Action> ret = new LinkedList<>();
		
		try (BufferedReader in = new BufferedReader(new FileReader(AUDITFILE))) {
			String line;
			
			while ((line = in.readLine()) != null) {
				ret.add(new Action(line));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}

	@Override
	public void run() {
		while (true) {
			Action tmpAction = EVENT_BUFFER.poll();
			if (tmpAction != null) {
				writeToFile(tmpAction.toString());
			} else {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void writeToFile(String data) {
		try (FileOutputStream fos = new FileOutputStream(AUDITFILE, true)) {
			fos.write(data.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
