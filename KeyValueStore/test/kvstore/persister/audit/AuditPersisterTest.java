package kvstore.persister.audit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;

import org.junit.Test;

import kvstore.persister.audit.AuditPersister;

public class AuditPersisterTest {
	
	private File testFile = new File("./test/kvstore/persister/createData.file");
	private String line_add_a_a = "add,rO0ABXQAAWE=,rO0ABXQAAWE=";
	private String line_remove_a = "remove,rO0ABXQAAWE=";
	
	@Test
	public void loadEmpty() {
		cleanup();
		
		AuditPersister<String, String> persister = new AuditPersister<>(testFile);
		Map<String, String> data = persister.load();
		
		assertTrue(data.size() == 0);
		
		cleanup();
	}
	
	@Test
	public void createData() {
		cleanup();
		
		AuditPersister<String, String> persister = new AuditPersister<>(testFile);
		persister.add("a", "a");
		persister.remove("a");
		
		try (BufferedReader in = new BufferedReader(new FileReader(testFile))) {		
			String line = in.readLine();
			
			assertEquals(line_add_a_a, line);
			
			line = in.readLine();
			
			assertEquals(line_remove_a, line);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		cleanup();
	}
	
	@Test
	public void loadRemovedData() {
		cleanup();
		
		try (BufferedWriter out = new BufferedWriter(new FileWriter(testFile))) {
			out.append(line_add_a_a)
				.append(System.lineSeparator())
				.append(line_remove_a)
				.append(System.lineSeparator());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		AuditPersister<String, String> persister = new AuditPersister<>(testFile);
		Map<String, String> data = persister.load();
		
		assertTrue(data.size() == 0);
	}
	
	@Test
	public void loadCreatedData() {
		cleanup();
		
		try (BufferedWriter out = new BufferedWriter(new FileWriter(testFile))) {
			out.append(line_add_a_a)
				.append(System.lineSeparator())
				.append(line_remove_a)
				.append(System.lineSeparator())
				.append(line_add_a_a)
				.append(System.lineSeparator());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		AuditPersister<String, String> persister = new AuditPersister<>(testFile);
		Map<String, String> data = persister.load();
		
		assertTrue(data.size() == 1);
		assertTrue(data.containsKey("a"));
		assertTrue(data.get("a").equals("a"));
		
		cleanup();
	}
	
	private void cleanup() {
		if (testFile.exists()) {
			testFile.delete();
		}
	}
	
}
