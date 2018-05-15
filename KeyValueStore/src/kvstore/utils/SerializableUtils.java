package kvstore.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

public class SerializableUtils {
	
	public static String toBase64(Serializable object) {
		String ret = null;
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ret = Base64.getEncoder().encodeToString(baos.toByteArray());
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static <D> D fromBase64(String base64) {
		D ret = null;
		
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object o = ois.readObject();
			ret = (D)o;
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}

}
