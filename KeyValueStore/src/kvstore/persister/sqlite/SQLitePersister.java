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

import kvstore.persister.Persister;
import kvstore.utils.SerializableUtils;

public class SQLitePersister<K extends Serializable, V extends Serializable> implements Persister<K, V>, LazyMapDataProvider<K, V> {
	
	private Connection CONN;
	
	public SQLitePersister(File targetDB) throws IllegalArgumentException {
		try {
			Class.forName("org.sqlite.JDBC");
			boolean create = !targetDB.exists();
			
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
			ps = prepare("CREATE TABLE DATA (KEY TEXT, VALUE TEXT);");
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
	public Map<K, V> load() {
		return new LazyMap<K, V>(this);
	}

	@Override
	public void add(K key, V value) {
		PreparedStatement ps = null;
		try {
			ps = prepare("INSERT INTO DATA (KEY, VALUE) VALUES (?, ?);");
			ps.setString(1, SerializableUtils.toBase64(key));
			ps.setString(2, SerializableUtils.toBase64(value));
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
	public void remove(K key) {
		PreparedStatement ps = null;
		try {
			ps = prepare("DELETE FROM DATA WHERE KEY=?;");
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
	public void save(Map<K, V> data) {
		data.keySet().stream().forEach(k -> add(k, data.get(k)));
	}

	@Override
	public void clear() {
		PreparedStatement ps = null;
		try {
			ps = prepare("DELETE FROM DATA;");
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
			ps = prepare("SELECT COUNT(*) FROM DATA;");
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
			ps = prepare("SELECT KEY FROM DATA;");
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
	public boolean isKnownData(V value) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean ret = false;
		try {
			ps = prepare("SELECT KEY FROM DATA WHERE VALUE=?;");
			ps.setString(1, SerializableUtils.toBase64(value));
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
	public V read(K key) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		V ret = null;;
		try {
			ps = prepare("SELECT VALUE FROM DATA WHERE KEY=?;");
			ps.setString(1, SerializableUtils.toBase64(key));
			rs = ps.executeQuery();
			rs.next();
			ret = SerializableUtils.fromBase64(rs.getString(1));
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
	public void store(K key, V value) {
		add(key, value);
	}

}
