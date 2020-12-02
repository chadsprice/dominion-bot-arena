package server;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class PlayerWebSocketHandler {

	Session outbound;

	@OnWebSocketConnect
	public void onConnect(Session session) {
		outbound = session;
		GameServer.INSTANCE.addConnection(this);
	}

	@OnWebSocketMessage
	public void onMessage(String message) {
		if ((outbound != null) && outbound.isOpen()) {
			GameServer.INSTANCE.receiveMessage(this, message);
		}
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		outbound = null;
		GameServer.INSTANCE.removeConnection(this);
	}

	@OnWebSocketError
	public void onError(Throwable cause) {
		cause.printStackTrace(System.err);
	}
	
	public void send(String message) {
		if ((outbound != null) && outbound.isOpen()) {
			try {
				outbound.getRemote().sendString(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
