package kvstore.web;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

import kvstore.persister.Persister;
import kvstore.persister.sqlite.SQLitePersister;

public class WebStoreInitData {
	
	private final Persister<String, String> PERSISTER;
	private final InetSocketAddress SERVER_ADDRESS;
	private final int BACKLOG;
	private final String HANDLER_URL;
	private final Executor EXECUTOR;
	
	public WebStoreInitData(String [] args) {
		File targetDbFile = null;
		String serverHost = null;
		String serverPort = null;
		int backlog = 0;
		String handlerUrl = null;
		Executor executor = null;
		
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "-dbFile" : targetDbFile = new File(args[++i]); break;
			case "-serverHost" : serverHost = args[++i]; break;
			case "-serverPort" : serverPort = args[++i]; break;
			case "-backlog" : backlog = Integer.parseInt(args[++i]); break; 
			case "-listenUrl" : handlerUrl = args[++i]; break;
			}
		}
		
		PERSISTER = new SQLitePersister<>(targetDbFile, !targetDbFile.exists());
		SERVER_ADDRESS = new InetSocketAddress(serverHost, Integer.parseInt(serverPort));
		BACKLOG = backlog;
		HANDLER_URL = handlerUrl;
		EXECUTOR = executor;
	}
	
	public Persister<String, String> getPersister() {
		return PERSISTER;
	}
	
	public InetSocketAddress getServerAddress() {
		return SERVER_ADDRESS;
	}
	
	public int getServerBacklog() {
		return BACKLOG;
	}
	
	public String getHandlerUrl() {
		return HANDLER_URL;
	}
	
	public Executor getExecutor() {
		return EXECUTOR;
	}

}
