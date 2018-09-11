package kvstore.persister.sqlite;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class SQLitePersisterTest {
	
	private static File testFile = new File("createDataSqlite.db");
	
	@Test
	public void createData() {
		SQLitePersister<String, String> persister = new SQLitePersister<>(testFile, true);
		
		for (int i = 0; i < 10; i++) {
			System.out.println("Adding data No." + i);
			persister.add("key" + i, "theValue" + i);
		}
		
		persister = new SQLitePersister<>(testFile, false);
		
		for (int i = 0; i < 10; i++) {
			String data = persister.read("key" + i);
			
			assertEquals("theValue" + i, data);
		}
		
		cleanup();
	}
	
	@Test
	public void loadRemovedData() {
		SQLitePersister<String, String> persister = new SQLitePersister<>(testFile, true);
		
		for (int i = 0; i < 10; i++) {
			System.out.println("Adding data No." + i);
			persister.add("key" + i, "theValue" + i);
		}
		
		persister = new SQLitePersister<>(testFile, false);
		
		persister.remove("key5");
		
		assertEquals(Boolean.TRUE, null == persister.read("key5"));
		
		cleanup();
	}
	
	@Test
	public void saveMap() {
		SQLitePersister<String, String> persister = new SQLitePersister<>(testFile, true);
		Map<String, String> toPersist = new HashMap<>();

		for (int i = 0; i < 100; i++) {
			toPersist.put("key" + i, "value" + i);
		}
		
		persister.save(toPersist);
		
		int storedSize = persister.keys().size();
		
		assertEquals(100, storedSize);
		
		cleanup();
	}
	
	@Test
	public void loadMap() {
		SQLitePersister<String, String> persister = new SQLitePersister<>(testFile, true);
		Map<String, String> toPersist = new HashMap<>();

		for (int i = 0; i < 100; i++) {
			toPersist.put("key" + i, "value" + i);
		}
		
		persister.save(toPersist);
		
		persister = new SQLitePersister<>(testFile, false);

		for (int i = 0; i < 100; i++) {
			assertEquals("value" + i, persister.read("key" + i));
		}
		
		cleanup();
	}
	
	private void cleanup() {
		if (testFile.exists()) {
			testFile.delete();
		}
	}
}
