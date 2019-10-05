package kvstore.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import com.sun.istack.internal.logging.Logger;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import kvstore.Store;
import kvstore.persister.TypedData;

public final class WebStore {
	
	private final Store<String, String> STORE;
	private final Logger LOGGER = Logger.getLogger(this.getClass());
	
	private HttpServer SERVER;
	
	private WebStore(WebStoreInitData initData) throws IOException {
		STORE = new Store<>(initData.getPersister());
		SERVER = HttpServer.create(initData.getServerAddress(), initData.getServerBacklog());
		
		String url = initData.getHandlerUrl() + "/";
		LOGGER.log(Level.INFO, "Starting server at: " + url);
		
		SERVER.createContext(url, createDataHandler(url));
		
		SERVER.setExecutor(initData.getExecutor());
	}
	
	private HttpHandler createDataHandler(String path) {
		return new HttpHandler() {
			
			private final String NOT_FOUND = "Not found.";
			
			private final String PATH = path;
			
			@Override
			public void handle(HttpExchange arg0) throws IOException {
				String method = arg0.getRequestMethod().toUpperCase();
				String key = getKey((arg0.getRequestURI().toString()));
				arg0.getResponseHeaders().set("Content-Type", "text/plain");
				
				LOGGER.info("Handling request method: " + method + " key: " + key);
				
				if (key.length() > 0) {
					switch (method) {
					case "GET" : 	doGet(arg0, key); break;
					case "POST" : 	doPost(arg0, key); break;
					case "PUT": 	doPut(arg0, key); break;
					case "DELETE" : doDelete(arg0, key); break;
					default : 		doDefault(arg0); break;
					}
				} else {
					doDefault(arg0);
				}
			}
			
			private void doGet(HttpExchange arg0, String key) throws IOException {
				try {
					TypedData<String> ret = STORE.get(key);
					
					if (ret == null) {
						LOGGER.info("  Value not found.");
						arg0.sendResponseHeaders(404, 0L);
					} else {
						LOGGER.info("  Value found.");
						LOGGER.fine("  Returning: " + ret);
						arg0.getResponseHeaders().set("Content-type", ret.getMimeType());
						arg0.sendResponseHeaders(200, ret.getData().getBytes().length);
						arg0.getResponseBody().write(ret.getData().getBytes());
					}
				} catch (Exception e) {
					LOGGER.severe("Error when handling request!" + e.getLocalizedMessage());
					e.printStackTrace();
					arg0.sendResponseHeaders(500, 0L);
				}
				arg0.getResponseBody().close();
			}
			
			private void doPost(HttpExchange arg0, String key) throws IOException {
				try {
					if (STORE.get(key) != null) {
						LOGGER.severe("  Value already exists.");
						arg0.sendResponseHeaders(404, 0L);
					} else {
						String value = readBodyContent(arg0);
						
						if (value != null && value.length() > 0) {
							LOGGER.info("  Value received.");
							LOGGER.fine(value);
							STORE.add(key, new TypedData<String>(value, getContentType(arg0)));
							arg0.sendResponseHeaders(200, 0L);
						} else {
							LOGGER.severe("  No value received.");
							arg0.sendResponseHeaders(500, 0L);
						}
					}
					arg0.getResponseBody().close();
				} catch (Exception e) {
					LOGGER.severe("Error when handling request!" + e.getLocalizedMessage());
					e.printStackTrace();
					arg0.sendResponseHeaders(500, 0L);
				}
			}
			
			private void doPut(HttpExchange arg0, String key) throws IOException {
				try {
					if (STORE.get(key) == null) {
						LOGGER.info("  Value for update not found.");
						arg0.sendResponseHeaders(404, 0L);
					} else {
						String value = readBodyContent(arg0);
						
						if (value != null && value.length() > 0) {
							LOGGER.info("  Sent value received.");
							LOGGER.fine(value);
							STORE.remove(key);
							STORE.add(key, new TypedData<String>(value, getContentType(arg0)));
							arg0.sendResponseHeaders(200, 0L);
						} else {
							LOGGER.severe("  No value received.");
							arg0.sendResponseHeaders(406, 0L);
						}
					}
				} catch (Exception e) {
					LOGGER.severe("Error when handling request!" + e.getLocalizedMessage());
					e.printStackTrace();
					arg0.sendResponseHeaders(500, 0L);
				}
				arg0.getResponseBody().close();
			}
			
			private void doDelete(HttpExchange arg0, String key) throws IOException {
				try {
					TypedData<String> ret = STORE.get(key);
					
					if (ret == null) {
						LOGGER.info("  Value not found.");
						arg0.sendResponseHeaders(404, 0L);
					} else {
						STORE.remove(key);
						arg0.sendResponseHeaders(200, 0L);
					}
				} catch (Exception e) {
					LOGGER.severe("Error when handling request!" + e.getLocalizedMessage());
					e.printStackTrace();
					arg0.sendResponseHeaders(500, 0L);
				}
				arg0.getResponseBody().close();
			}
			
			private String getContentType(HttpExchange arg0) throws IOException {
				String ret = arg0.getRequestHeaders().entrySet().stream().filter(E -> E.getKey().equalsIgnoreCase("content-type")).map(E -> E.getValue().toString()).findFirst().orElse("text/plain");
				return ret;
			}
			
			private void doDefault(HttpExchange arg0) throws IOException {
				LOGGER.info("  Handling default.");
				arg0.sendResponseHeaders(404, NOT_FOUND.length());
				arg0.getResponseBody().write(NOT_FOUND.getBytes());
			}
			
			private String getKey(String requestUri) {
				String ret = requestUri.startsWith(PATH) ? requestUri.substring(PATH.length()) : "";
				return ret;
			}
			
			private String readBodyContent(HttpExchange arg0) {
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
			
		};
	}
	
	private void start() {
		SERVER.start();
	}
	
	public static void main(String [] args) throws IOException {
		new WebStore(new WebStoreInitData(args)).start();
	}

}
