package kvstore.web;

import java.io.IOException;
import java.util.List;

import com.sun.istack.internal.logging.Logger;
import com.sun.net.httpserver.HttpExchange;

import kvstore.persister.TypedData;

public class DeleteSingle extends KvHttpHandler {

	private final Logger LOGGER = Logger.getLogger(this.getClass());
	
	public DeleteSingle(WebStore store) {
		super(store);
	}

	@Override
	public void handle(HttpExchange exchange, List<String> keys, String method) throws IOException {
		if ("DELETE".equalsIgnoreCase(method) && keys != null && keys.size() == 1) {
			LOGGER.info("  Handling DELETE.");
			try {
				TypedData<String> ret = getStore().get(keys.get(0));
				
				if (ret == null) {
					LOGGER.info("  Value not found.");
					exchange.sendResponseHeaders(404, 0L);
				} else {
					getStore().remove(keys.get(0));
					exchange.sendResponseHeaders(200, 0L);
				}
			} catch (Exception e) {
				LOGGER.severe("Error when handling request!" + e.getLocalizedMessage());
				e.printStackTrace();
				exchange.sendResponseHeaders(500, 0L);
			}
			exchange.getResponseBody().close();
		} else {
			next().handle(exchange, keys, method);
		}
	}

}
