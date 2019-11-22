package kvstore.web;

import java.io.IOException;
import java.util.List;

import com.sun.istack.internal.logging.Logger;
import com.sun.net.httpserver.HttpExchange;

import hsu.http.HsuHttpExchange;

public class GetMultiple extends KvHttpHandler {

	private final Logger LOGGER = Logger.getLogger(this.getClass());
	
	public GetMultiple(WebStore store) {
		super(store);
	}

	@Override
	public void handle(HttpExchange exchange, List<String> keys, KvRequest request) throws IOException {
		HsuHttpExchange hhe = new HsuHttpExchange(exchange);
		if ("GET".equalsIgnoreCase(request.getMethod()) && keys != null && keys.size() > 1) {
			LOGGER.info("  Handling GET");
			try {
				String ret = mapKeysToReturnString(keys);
				
				LOGGER.info("  Values found.");
				if (hhe.getAllParameters().containsKey("KEYS")) {
					LOGGER.fine("  Returning: " + ret);
					exchange.getResponseHeaders().set("Content-type", "Text/Plain");
					exchange.sendResponseHeaders(200, ret.getBytes().length);
					exchange.getResponseBody().write(ret.getBytes());
				} else {
					LOGGER.fine("  Returning values: ");
					exchange.getResponseHeaders().set("Content-type", "Application/Json");
					exchange.sendResponseHeaders(200, 0);
					exchange.getResponseBody().write(new byte[0]);
				}
			} catch (Exception e) {
				LOGGER.severe("Error when handling request!" + e.getLocalizedMessage());
				e.printStackTrace();
				exchange.sendResponseHeaders(500, 0L);
			}
			exchange.getResponseBody().close();
		} else {
			next().handle(exchange, keys, request);
		}
	}
	
	private String mapKeysToReturnString(List<String> keys) {
		String ret = "";
		
		for (String key : keys) {
			ret += "\n" + key;
		}
		
		return ret.trim();
	}

}
