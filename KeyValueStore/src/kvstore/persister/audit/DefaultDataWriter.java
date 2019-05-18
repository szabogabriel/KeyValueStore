package kvstore.persister.audit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultDataWriter implements DataWriter {
	
	private final File AUDITFILE;
	
	private final ConcurrentLinkedQueue<Action> EVENT_BUFFER = new ConcurrentLinkedQueue<>();
	
	public DefaultDataWriter(File auditFile) {
		AUDITFILE = auditFile;
	}
	
	public void addAction(Action action) {
		EVENT_BUFFER.offer(action);
		writeBuffer();
	}
	
	public List<Action> loadActions() {
		List<Action> ret = new LinkedList<>();
		
		if (AUDITFILE.exists()) {
			try (BufferedReader in = new BufferedReader(new FileReader(AUDITFILE))) {
				String line;
				
				while ((line = in.readLine()) != null) {
					ret.add(new Action(line));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return ret;
	}

	private void writeBuffer() {
		Action tmpAction;
		while ((tmpAction = EVENT_BUFFER.poll()) != null) {
			writeToFile(tmpAction.toString());
		} 
	}
	
	private void writeToFile(String data) {
		try (FileOutputStream fos = new FileOutputStream(AUDITFILE, true)) {
			fos.write(data.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		//nothing to do.
	}

}
