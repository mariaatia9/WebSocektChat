/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.springbootbuch.websockets;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 *
 * @author trainer
 */
public class MyWebSocketHandler extends TextWebSocketHandler {

	private Map<String, String> userNames = new ConcurrentHashMap<>();
	private CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String userName = session.getUri().getQuery();  // Annahme: Benutzername wird als Query-Parameter übergeben
		if (userName != null) {
			userNames.put(session.getId(), userName);
		}
		sessions.add(session);
		System.out.println("Neue Sitzung geöffnet: " + session.getId() + " Benutzername: " + userName);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String userName = userNames.get(session.getId());
		String personalizedMessage = (userName != null ? userName : "Unbekannter Benutzer") + ": " + message.getPayload();
		broadcastMessage(personalizedMessage);
//		String clientMessage = message.getPayload();
//		System.out.println("Nachricht empfangen: " + clientMessage);
//		broadcastMessage(session, clientMessage);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
		userNames.remove(session.getId());
		System.out.println("Sitzung geschlossen: " + session.getId());
	}

	private void broadcastMessage(String message) throws Exception {
		TextMessage textMessage = new TextMessage(message);
		System.out.println("BroadCoast aufgerufen!");
		for (WebSocketSession session : sessions) {
			if (session.isOpen()) {
				session.sendMessage(textMessage);
			}
		}
	}
}
