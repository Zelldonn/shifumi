package com.kaspiotr.shifumi.network;

import java.io.IOException;
import java.net.Socket;

import com.kaspiotr.GameType;
import com.kaspiotr.Move;
import com.kaspiotr.networking.AsyncStreamReader;
import com.kaspiotr.networking.IOCommand;

public class ServerConnection {
	
	private ServerListener listener;
	private AsyncStreamReader reader;
	private Socket socket;
	private Thread readingThread;
	
	private boolean isConnected;
	
	public ServerConnection(Socket s, ServerListener l) throws IOException {
		this.listener = l;
		this.socket = s;
		ServerConnection self = this;
		
		reader = new AsyncStreamReader(socket.getInputStream()) {
			
			@Override
			public void onLine(String line) {
				
				try {
					String[] split = line.split(":");
					if(split.length != 2) {
						return;
					}
					
					String message = split[0];
					String data = split[1];
					
					//System.out.println(message + " --> " + data);
					if(message.equals("id")) {
						listener.onGameCreated(self, data);
					}
					else if(message.equals("gamelist")) {
						listener.onLobbiesList(self, data);
					}
					else if(message.equals("gamestart"))
					{
						GameType newGameType = GameType.parse(data);
						if(newGameType != null)
							listener.onGameStart(self, newGameType);
					}
					else if(message.equals("roundstart")) 
					{
						long time = Long.parseLong(data);	
						listener.onRoundStart(self, time);
					}
					else if(message.equals("roundend")) 
					{
						String[] sub = data.split(",");
						boolean isDraw = Boolean.parseBoolean(sub[0]);
						String name = sub[1];
						Move m = Move.parse(sub[2]);
						
						if(m != null) {
							listener.onRoundEnd(self, isDraw, name, m);
						}
					}
					else if(message.equals("gameend")) 
					{
						listener.onGameEnd(self, data);
					}
					else if(message.equals("err")) 
					{
						listener.onError(self, data);
					}
					else if(message.equals("connected")) 
					{
						listener.onNewPlayer(self, data);
					}else if(message.equals("joined")){
						listener.onJoined(self);
					}
				}catch(Exception e) {
					//Catch any exception, we don't wan't this to fail
					System.err.println("Error while parsing command from server : ");
					e.printStackTrace();
				}
			}
			
			@Override
			public void onIOException(Exception e) {
				listener.onConnectionClosed(self);
			}
			
			@Override
			public void onEOL() {
				listener.onConnectionClosed(self);
			}
		};
		
		isConnected = true;
		readingThread = new Thread(reader);
		readingThread.start();
	}	

	public void sendInfo(String name) {
		trySend("info " + name);
	}
	
	public void sendMove(Move m) {
		trySend("move " + m.toString().toLowerCase());
	}
	
	public void sendListRequest(GameType filter) {
		if(filter == null) {
			trySend("list");			
		}
		else {
			trySend("list " + filter.toString());
		}
	}
	
	public void sendCreate(GameType type) {
		if(type == null)
			throw new IllegalArgumentException("GameType argument cannot be null");
		
		trySend("create " + type.toString().toLowerCase() );
	}
	
	public void sendJoin(String id) {
		if(id == null)
			throw new IllegalArgumentException("ID argument cannot be null");
		
		if(id.isEmpty()) {
			throw new IllegalArgumentException("String 'id' cannot be empty");
		}
		
		trySend("join " + id );
	}
	
	private void trySend(String s) {
		try {
			IOCommand.sendLine(socket, s);
		} catch (IOException e) {
			disconnect();
		}
	}
	
	public void disconnect() {
		
		if(isConnected) {
			isConnected = false;
			try {
				socket.shutdownOutput();
				socket.shutdownInput();
			} catch (IOException e) {
			}
			listener.onConnectionClosed(this);
		}
	}

}
