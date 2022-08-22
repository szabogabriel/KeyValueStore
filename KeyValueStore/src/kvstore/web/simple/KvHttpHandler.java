package kvstore.web.simple;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import kvstore.Store;

public abstract class KvHttpHandler {
	
	public abstract void handle(HttpExchange exchange, List<String> keys, KvRequest request) throws IOException ;
	
	private final WebStore STORE;
	private KvHttpHandler next;
	
	public KvHttpHandler(WebStore store) {
		STORE = store;
	}
	
	protected Store<String, String> getStore() {
		return STORE.getStore();
	}
	
	public KvHttpHandler addNext(KvHttpHandler handler) {
		next = handler;
		return handler;
	}
	
	protected KvHttpHandler next() {
		return next;
	}
	
	protected String readBodyContent(HttpExchange arg0) {
		String value = "";
		
		try (InputStream in = arg0.getRequestBody()) {
			StringBuilder sb = new StringBuilder();
			byte [] buffer = new byte[8096];
			int read;
			
			while ((read = in.read(buffer)) != -1) {
				sb.append(new String(buffer, 0, read));
			}
			
			value = sb.toString();
		} catch (Exception e) {
			value = null;
		}
		
		return value;
	}
	
	protected String getContentType(HttpExchange arg0) throws IOException {
		String ret = arg0.getRequestHeaders().entrySet().stream().filter(E -> E.getKey().equalsIgnoreCase("content-type")).map(E -> E.getValue().toString()).findFirst().orElse("text/plain");
		return ret;
	}

}
