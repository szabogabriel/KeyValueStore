package kvstore.web;

import java.io.IOException;
import java.util.List;

import com.sun.istack.internal.logging.Logger;
import com.sun.net.httpserver.HttpExchange;

import kvstore.persister.TypedData;

public class PutSingle extends KvHttpHandler {

	private final Logger LOGGER = Logger.getLogger(this.getClass());
	
	public PutSingle(WebStore store) {
		super(store);
	}

	@Override
	public void handle(HttpExchange exchange, List<String> keys, KvRequest request) throws IOException {
		if ("PUT".equalsIgnoreCase(request.getMethod()) && keys != null && keys.size() == 1) {
			LOGGER.info("  Handling PUT.");
			try {
				if (getStore().get(keys.get(0)) == null) {
					LOGGER.info("  Value for update not found.");
					exchange.sendResponseHeaders(404, 0L);
				} else {
					String value = readBodyContent(exchange);
					
					if (value != null && value.length() > 0) {
						LOGGER.info("  Sent value received.");
						LOGGER.fine(value);
						getStore().remove(keys.get(0));
						getStore().add(keys.get(0), new TypedData<String>(value, getContentType(exchange)));
						exchange.sendResponseHeaders(200, 0L);
					} else {
						LOGGER.severe("  No value received.");
						exchange.sendResponseHeaders(406, 0L);
					}
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

}
