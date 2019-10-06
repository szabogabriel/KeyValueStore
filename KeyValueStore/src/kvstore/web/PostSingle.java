package kvstore.web;

import java.io.IOException;
import java.util.List;

import com.sun.istack.internal.logging.Logger;
import com.sun.net.httpserver.HttpExchange;

import kvstore.persister.TypedData;

public class PostSingle extends KvHttpHandler {

	private final Logger LOGGER = Logger.getLogger(this.getClass());
	
	public PostSingle(WebStore store) {
		super(store);
	}

	@Override
	public void handle(HttpExchange exchange, List<String> keys, String method) throws IOException {
		if ("POST".equalsIgnoreCase(method) && keys != null && keys.size() == 1) {
			LOGGER.info("  Handling POST.");
			try {
				if (getStore().get(keys.get(0)) != null) {
					LOGGER.severe("  Value already exists.");
					exchange.sendResponseHeaders(404, 0L);
				} else {
					String value = readBodyContent(exchange);
					
					if (value != null && value.length() > 0) {
						LOGGER.info("  Value received.");
						LOGGER.fine(value);
						getStore().add(keys.get(0), new TypedData<String>(value, getContentType(exchange)));
						exchange.sendResponseHeaders(200, 0L);
					} else {
						LOGGER.severe("  No value received.");
						exchange.sendResponseHeaders(500, 0L);
					}
				}
				exchange.getResponseBody().close();
			} catch (Exception e) {
				LOGGER.severe("Error when handling request!" + e.getLocalizedMessage());
				e.printStackTrace();
				exchange.sendResponseHeaders(500, 0L);
			}
		} else {
			next().handle(exchange, keys, method);
		}
	}

}
