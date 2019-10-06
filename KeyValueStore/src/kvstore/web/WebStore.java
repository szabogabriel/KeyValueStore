package kvstore.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import com.sun.istack.internal.logging.Logger;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import kvstore.Store;

public final class WebStore {
	
	private final Store<String, String> STORE;
	private final Logger LOGGER = Logger.getLogger(this.getClass());
	
	private HttpServer SERVER;
	
	private KvHttpHandler HANDLER;
	
	private String URL;
	
	private WebStore(WebStoreInitData initData) throws IOException {
		STORE = new Store<>(initData.getPersister());
		
		URL = initData.getHandlerUrl() + "/";
		LOGGER.log(Level.INFO, "Starting server at: " + URL);
		
		HANDLER = createKvHttpHandler();
		
		SERVER = HttpServer.create(initData.getServerAddress(), initData.getServerBacklog());
		SERVER.createContext(URL, createHandler());
		SERVER.setExecutor(initData.getExecutor());
	}
	
	private KvHttpHandler createKvHttpHandler() {
		KvHttpHandler first = new GetSingle(this);
		
		first
			.addNext(new PostSingle(this))
			.addNext(new PutSingle(this))
			.addNext(new DeleteSingle(this))
			.addNext(new DefaultHandler(this));
		
		return first;
	}
	
	private HttpHandler createHandler() {
		return new HttpHandler() {
			@Override
			public void handle(HttpExchange arg0) throws IOException {
				String method = arg0.getRequestMethod().toUpperCase();
				List<String> key = getKey((arg0.getRequestURI().toString()));
				
				HANDLER.handle(arg0, key, method);
			}
		};
	}
	
	private List<String> getKey(String requestUri) {
		List<String> ret = new ArrayList<>();
		String request = requestUri.startsWith(URL) ? requestUri.substring(URL.length()) : "";
		
		if (request.contains("*")) {
			request = request.replaceAll("\\*", ".*");
			Pattern pattern = Pattern.compile(request);
			STORE.getKeys().stream().filter(K -> pattern.matcher(K).matches()).forEach(K -> ret.add(K));
		} else {
			ret.add(request);
		}
		
		return ret;
	}
	
	private void start() {
		SERVER.start();
	}
	
	public Store<String, String> getStore() {
		return STORE;
	}
	
	public static void main(String [] args) throws IOException {
		new WebStore(new WebStoreInitData(args)).start();
	}

}
