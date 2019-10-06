package kvstore.web;

import java.io.IOException;
import java.util.List;

import com.sun.istack.internal.logging.Logger;
import com.sun.net.httpserver.HttpExchange;

public class DefaultHandler extends KvHttpHandler{

	private final Logger LOGGER = Logger.getLogger(this.getClass());
	private final String NOT_FOUND = "Not found.";
	
	public DefaultHandler(WebStore store) {
		super(store);
	}

	@Override
	public void handle(HttpExchange exchange, List<String> keys, String method) throws IOException {
		LOGGER.info("  Handling default.");
		exchange.sendResponseHeaders(404, NOT_FOUND.length());
		exchange.getResponseBody().write(NOT_FOUND.getBytes());
		exchange.getResponseBody().close();
	}

}
