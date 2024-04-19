package service.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import service.controllers.WebSocketController;

@Component
public class WebSocketEventListener {

    private final WebSocketController webSocketController;

    @Autowired
    public WebSocketEventListener(WebSocketController webSocketController) {
        this.webSocketController = webSocketController;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        System.out.println("Received a new client connection");
        webSocketController.sendStringMessage("Welcome to the Bookie WebSocket service!");
    }
}
