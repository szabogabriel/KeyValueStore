package kvstore.persister;

public class TypedData<T> {
	
	public final T DATA;
	public final String MIME_TYPE;
	
	public TypedData(T data, String mimeType) {
		DATA = data;
		MIME_TYPE = mimeType;
	}
	
	public String getMimeType() {
		return MIME_TYPE;
	}
	
	public T getData() {
		return DATA;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((DATA == null) ? 0 : DATA.hashCode());
		result = prime * result + ((MIME_TYPE == null) ? 0 : MIME_TYPE.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypedData other = (TypedData) obj;
		if (DATA == null) {
			if (other.DATA != null)
				return false;
		} else if (!DATA.equals(other.DATA))
			return false;
		if (MIME_TYPE == null) {
			if (other.MIME_TYPE != null)
				return false;
		} else if (!MIME_TYPE.equals(other.MIME_TYPE))
			return false;
		return true;
	}
	
	

}
