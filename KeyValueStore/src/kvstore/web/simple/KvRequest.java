package kvstore.web.simple;

import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

public class KvRequest {
	
	private final String KEY;
	private final String METHOD;
	private final Map<String, String> PARAMETER = new HashMap<>();
	
	public KvRequest(String prefix, HttpExchange request) {
		METHOD = request.getRequestMethod().toUpperCase();
		String requestUri = request.getRequestURI().toString();
		String r = requestUri.startsWith(prefix) ? requestUri.substring(prefix.length()) : "";
		if (r.contains("?")) {
			String qs = r.substring(r.indexOf("?"));
			parseQueryString(qs);
			r = r.substring(0, r.indexOf("?"));
		}
		KEY = r;
	}
	
	private void parseQueryString(String qs) {
		String[] keyValue = qs.split("&");
		for (String it : keyValue) {
			String[] tmp = it.split("=");
			String key = "", value = "";
			if (tmp.length >= 1) {
				key = tmp[0];
			}
			if (tmp.length == 2) {
				value = tmp[1];
			}
			PARAMETER.put(key, value);
		}
	}
	
	public String getKey() {
		return KEY;
	}
	
	public String getMethod() {
		return METHOD;
	}

}
