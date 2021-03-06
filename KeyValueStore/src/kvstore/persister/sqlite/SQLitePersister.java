package kvstore.persister.sqlite;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kvstore.dataStructures.LazyMap;
import kvstore.dataStructures.LazyMapDataProvider;
import kvstore.persister.Persister;
import kvstore.persister.TypedData;
import kvstore.utils.SerializableUtils;

public class SQLitePersister<K extends Serializable, V extends Serializable> implements Persister<K, V>, LazyMapDataProvider<K, TypedData<V>> {

	private static final String TABLE_NAME = "SQLITEAUDITTABLE";
	
	private final boolean CACHE_KEYS;
	private final long MAX_CACHE_SIZE;
	
	private Connection CONN;
	
	public SQLitePersister(File targetDB, boolean create) throws IllegalArgumentException {
		this(targetDB, create, true, 1024 * 1024 * 32);
	}
	
	public SQLitePersister(File targetDB, boolean create, boolean cacheKeys, long maxsize) {		
		CACHE_KEYS = cacheKeys;
		MAX_CACHE_SIZE = maxsize;
		try {
			if (create && targetDB.exists()) {
				boolean deleted = targetDB.delete();
				if (!deleted) {
					throw new RuntimeException("Cannot delete the existing database: " + targetDB.getAbsolutePath());
				}
			}
			Class.forName("org.sqlite.JDBC");
			
	        CONN = DriverManager.getConnection("jdbc:sqlite:" + targetDB.getAbsolutePath());
	        
	        if (create) {
	        	createTable();
	        }
		} catch (Exception e) {
			throw new IllegalArgumentException("Can't create connection with database file " + targetDB);
		}
	}
	
	private void createTable() {
		PreparedStatement ps = null;
		try {
			ps = prepare("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, KKEY TEXT, VVALUE TEXT, TYPE TEXT);");
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private PreparedStatement prepare(String statement) throws SQLException {
		return CONN.prepareStatement(statement);
	}

	@Override
	public Map<K, TypedData<V>> load() {
		return new LazyMap<K, TypedData<V>>(this, CACHE_KEYS, MAX_CACHE_SIZE);
	}

	@Override
	public void add(K key, TypedData<V> value) {
		PreparedStatement ps = null;
		try {
			ps = prepare("INSERT INTO " + TABLE_NAME + " (KKEY, VVALUE, TYPE) VALUES (?, ?, ?);");
			ps.setString(1, SerializableUtils.toBase64(key));
			ps.setString(2, SerializableUtils.toBase64(value.getData()));
			ps.setString(3, value.getMimeType());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void remove(K key) {
		PreparedStatement ps = null;
		try {
			ps = prepare("DELETE FROM " + TABLE_NAME + " WHERE KKEY=?;");
			ps.setString(1, SerializableUtils.toBase64(key));
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void save(Map<K, TypedData<V>> data) {
		data.keySet().stream().forEach(k -> add(k, data.get(k)));
	}

	@Override
	public void clear() {
		PreparedStatement ps = null;
		try {
			ps = prepare("DELETE FROM " + TABLE_NAME + ";");
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public int size() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		int ret = 0;
		try {
			ps = prepare("SELECT COUNT(*) FROM " + TABLE_NAME + ";");
			rs = ps.executeQuery();
			rs.next();
			ret = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	@Override
	public Set<K> keys() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Set<K> ret = new HashSet<>();;
		try {
			ps = prepare("SELECT KKEY FROM " + TABLE_NAME + ";");
			rs = ps.executeQuery();
			while (rs.next()) {
				ret.add(SerializableUtils.fromBase64(rs.getString(1)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}
	
	@Override
	public boolean isKnownKey(K key) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean ret = false;
		try {
			ps = prepare("SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE KEY=?;");
			ps.setString(1, SerializableUtils.toBase64(key));
			rs = ps.executeQuery();
			rs.next();
			int count = rs.getInt(1);
			ret = count == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	@Override
	public boolean isKnownData(TypedData<V> value) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean ret = false;
		try {
			ps = prepare("SELECT KKEY FROM " + TABLE_NAME + " WHERE VVALUE=?;");
			ps.setString(1, SerializableUtils.toBase64(value.getData()));
			rs = ps.executeQuery();
			rs.next();
			ret = SerializableUtils.fromBase64(rs.getString(1)) != null; 
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	@Override
	public TypedData<V> read(K key) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		TypedData<V> ret = null;;
		try {
			ps = prepare("SELECT VVALUE, TYPE FROM " + TABLE_NAME + " WHERE KKEY=?;");
			ps.setString(1, SerializableUtils.toBase64(key));
			rs = ps.executeQuery();
			if (rs.next()) {
				String value = rs.getString(1);
				String type = rs.getString(2);
				ret = new TypedData<V>(SerializableUtils.fromBase64(value), type);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	@Override
	public void store(K key, TypedData<V> value) {
		add(key, value);
	}

	@Override
	public void close() {
		try {
			CONN.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
