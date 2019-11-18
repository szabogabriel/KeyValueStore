package kvstore.web;

import java.io.IOException;
import java.util.List;

import com.sun.istack.internal.logging.Logger;
import com.sun.net.httpserver.HttpExchange;

import kvstore.persister.TypedData;

public class GetSingle extends KvHttpHandler {
	
	private final Logger LOGGER = Logger.getLogger(this.getClass());
	
	public GetSingle(WebStore parent) {
		super(parent);
	}

	@Override
	public void handle(HttpExchange exchange, List<String> keys, KvRequest request) throws IOException {
		if ("GET".equalsIgnoreCase(request.getMethod()) && keys != null && keys.size() == 1) {
			LOGGER.info("  Handling GET");
			try {
				TypedData<String> ret = getStore().get(keys.get(0));
				
				if (ret == null) {
					LOGGER.info("  Value not found.");
					exchange.sendResponseHeaders(404, 0L);
				} else {
					LOGGER.info("  Value found.");
					LOGGER.fine("  Returning: " + ret);
					exchange.getResponseHeaders().set("Content-type", ret.getMimeType());
					exchange.sendResponseHeaders(200, ret.getData().getBytes().length);
					exchange.getResponseBody().write(ret.getData().getBytes());
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
