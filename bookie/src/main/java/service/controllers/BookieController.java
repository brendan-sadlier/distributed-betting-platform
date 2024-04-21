package service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import service.core.Bet;
import service.core.Horse;
import service.core.Race;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class BookieController {
    @Value("${server.port}")
    private int port;
    private final ConcurrentHashMap<String, Bet> clientBets = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;
    private Race currentRace;
    RestTemplate template = new RestTemplate();
    public static final String RACE_UPDATES_TOPIC = "/topic/raceUpdates";

    @Autowired
    public BookieController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        currentRace = getNewRace();
    }

    @EventListener
    public void handleClientConnection(SessionConnectedEvent event) {
        String sessionId = SimpMessageHeaderAccessor.getSessionId(event.getMessage().getHeaders());
        if (sessionId != null && currentRace != null) {
            System.out.println("New client connected: "+ sessionId);
            messagingTemplate.convertAndSendToUser(sessionId, "/queue/personalRaceUpdate", currentRace);
        }
    }


    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();
        assert sessionId != null;
        clientBets.remove(sessionId);
        System.out.println("Client disconnected: " + sessionId);
    }

    @MessageMapping("/bet")
    public void receivedBet(StompHeaderAccessor headerAccessor, Bet bet) {
        String sessionId = headerAccessor.getSessionId();
        assert sessionId != null;
        clientBets.put(sessionId, bet);
        System.out.println("Received bet from " + sessionId + ": " + bet);
    }

    @Scheduled(fixedRate = 20000, initialDelay = 20000)
    public void simulateCurrentRace() throws InterruptedException {
        System.out.println("Simulating current race");
        if (currentRace == null) {
            currentRace = getNewRace();  // Ensure there is always a race to simulate
        }

        for (Horse horse : currentRace.horses){
            System.out.println("Horse in race: "+horse.horseName);
        }
        String url = "http://localhost:8081/races/";
        // Create an HttpEntity object that wraps the currentRace object to be sent as request body
        HttpEntity<Race> requestEntity = new HttpEntity<>(currentRace);
        ResponseEntity<Horse> response = template.postForEntity(url, requestEntity, Horse.class);
        Horse winner = response.getBody();
        assert winner != null;

        System.out.println("The winner is: "+winner.horseName+"!");
        int i = 0;
        while (!currentRace.horses.get(i).horseName.equals(winner.horseName)){
            i++;
        }
        Double odds = currentRace.horseOdds.get(i);

        notifyWinnersAndLosers(winner, odds);
        Thread.sleep(1000);
        currentRace = getNewRace();
        messagingTemplate.convertAndSend(RACE_UPDATES_TOPIC, currentRace);
    }

    private Race getNewRace(){
        String urlRace = "http://localhost:8083/generate-races";
        ResponseEntity<Race> response = template.getForEntity(urlRace, Race.class);
        Race race = response.getBody();
        assert race != null;
        return race;
    }

    private void notifyWinnersAndLosers(Horse winner, double odds){
        // notify each client that has placed a bet on what amount they won, or if their horse lost
        for (Map.Entry<String, Bet> clientBet : clientBets.entrySet()){
            StringBuilder message = new StringBuilder("The winning horse is ").append(winner.horseName);
            if (clientBet.getValue().horseName.equals(winner.horseName)){
                double amountWon = getReward(clientBet.getValue().amount, odds);
                message.append("\nCongratulations! You have won ").append(amountWon);
            }else{
                message.append("\nUnfortunately your horse did not win :(");
            }
            System.out.println("Sending bet result to user "+ clientBet.getKey());
            messagingTemplate.convertAndSendToUser(clientBet.getKey(), "/queue/results", message.toString());
            clientBets.remove(clientBet.getKey());
        }
    }

    private Double getReward(Integer betSize, Double odds) {
        return betSize.doubleValue() * odds;
    }

    private String getHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress() + ":";
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }
}
