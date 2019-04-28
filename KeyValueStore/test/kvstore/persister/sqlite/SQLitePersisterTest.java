package kvstore.persister.sqlite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class SQLitePersisterTest {
	
	private static File testFile = new File("./test/kvstore/persister/sqlite/createDataSqlite.db");
	
	@Test
	public void createData() {
		SQLitePersister<String, String> persister = new SQLitePersister<>(testFile, true);
		
		for (int i = 0; i < 10; i++) {
			System.out.println("Adding data No." + i);
			persister.add("key" + i, "theValue" + i);
		}
		
		persister.close();
		
		persister = new SQLitePersister<>(testFile, false);
		
		for (int i = 0; i < 10; i++) {
			String data = persister.read("key" + i);
			
			assertEquals("theValue" + i, data);
		}
		
		persister.close();
		
		cleanup();
	}
	
	@Test
	public void loadRemovedData() {
		SQLitePersister<String, String> persister = new SQLitePersister<>(testFile, true);
		
		for (int i = 0; i < 10; i++) {
			System.out.println("Adding data No." + i);
			persister.add("key" + i, "theValue" + i);
		}
		
		persister.close();
		
		persister = new SQLitePersister<>(testFile, false);
		
		persister.remove("key5");
		
		assertEquals(Boolean.TRUE, null == persister.read("key5"));

		persister.close();
		
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
		
		persister.close();
		
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
		persister.close();
		
		persister = new SQLitePersister<>(testFile, false);

		for (int i = 0; i < 100; i++) {
			assertEquals("value" + i, persister.read("key" + i));
		}
		
		persister.close();
		cleanup();
	}
	
	@Test
	public void performanceTest() {
		SQLitePersister<String, String> persister = new SQLitePersister<>(testFile, true, false, 0L);

		long start = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			persister.add("key" + i, "value" + i);
		}

		Map<String, String> data = persister.load();
		for (int j = 0; j < 10000; j++) {
			for (int i = 0; i < 100; i++) {
				data.get("key" + i);
			}
		}
		long stop = System.currentTimeMillis();
		
		long noCacheSpeed = stop - start;
		System.out.println("No cache speed: " + noCacheSpeed);
		
		persister.close();
		
		persister = new SQLitePersister<>(testFile, true, true, 1024 * 1024 * 32);
		start = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			persister.add("key" + i, "value" + i);
		}
		
		data = persister.load();
		for (int j = 0; j < 10000; j++) {
			for (int i = 0; i < 100; i++) {
				data.get("key" + i);
			}
		}
		stop = System.currentTimeMillis();
		
		long cacheSpeed = stop - start;
		
		System.out.println("Cache speed: " + cacheSpeed);
		
		assertTrue(cacheSpeed < noCacheSpeed);
		
		persister.close();
		
		cleanup();
	}
	
	private void cleanup() {
		if (testFile.exists()) {
			testFile.delete();
		}
	}
}
