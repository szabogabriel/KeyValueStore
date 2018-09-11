package kvstore.persister.sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SmallSQLiteTest {
	
	private static Connection c;
	
	public static void main(String [] args) throws Exception {
		File targetFile = new File("testDB.db");
		
		if (targetFile.exists()) {
			targetFile.delete();
		}
		
		Class.forName("org.sqlite.JDBC");
		
        c = DriverManager.getConnection("jdbc:sqlite:" + targetFile.getAbsolutePath());
        
        PreparedStatement ps = c.prepareStatement("CREATE TABLE SQLITEPERSISTER (ID INTEGER PRIMARY KEY AUTOINCREMENT, KEY TEXT, VALUE TEXT);");
        ps.executeUpdate();
        ps.close();
        ps = null;
        
        for (int i = 0; i < 10000; i++) {
        	System.out.println("Adding data No." + i);
        	insert("Key: " + i, "Data: " + i);
        }
        
	}
	
	
	private static void insert(String key, String data) throws SQLException {
		PreparedStatement ps = c.prepareStatement("INSERT INTO SQLITEPERSISTER (KEY, VALUE) VALUES (?, ?);");
		ps.setString(1, key);
		ps.setString(2, data);
		ps.executeUpdate();
		ps.close();
	}

}
