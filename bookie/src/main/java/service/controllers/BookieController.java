package service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import service.core.Bet;
import service.core.Horse;
import service.core.Race;
import service.core.Winner;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class BookieController {
    @Value("${server.port}")
    private int port;

    private List<String> servicesUrls = new ArrayList<>();

    private final SimpMessagingTemplate messagingTemplate;
    private Race currentRace;
    private int currentRaceId = 0;
    RestTemplate template = new RestTemplate();
    public static final String RACE_UPDATES_TOPIC = "/topic/raceUpdates";

    @Autowired
    public BookieController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        broadCastNewRace();
    }


    @PostMapping(value = "/services", consumes = "application/json")
    public ResponseEntity<Void> registerService(@RequestBody String url) {
        servicesUrls.add(url);
        return ResponseEntity
                .ok()
                .build();
    }

    @EventListener
    public void handleClientConnection(SessionConnectedEvent event) {
        String sessionId = SimpMessageHeaderAccessor.getSessionId(event.getMessage().getHeaders());
        if (sessionId != null && currentRace != null) {
            System.out.println("New client connected: "+ sessionId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();
        assert sessionId != null;
        System.out.println("Client disconnected: " + sessionId);
    }

    @Scheduled(fixedRate = 60000, initialDelay = 10000)
    public void simulateCurrentRace() throws InterruptedException {
        System.out.println("Simulating current race");
        if (currentRace == null) {
            currentRace = getNewRace();  // Ensure there is always a race to simulate
        }

        for (Horse horse : currentRace.horses){
            System.out.println("Horse in race: "+horse.horseName);
        }
        String url = "http://racesimulator-service:8081/races";
        // Create an HttpEntity object that wraps the currentRace object to be sent as request body
        HttpEntity<Race> requestEntity = new HttpEntity<>(currentRace);
        ResponseEntity<Horse> response = template.postForEntity(url, requestEntity, Horse.class);
        Horse winningHorse = response.getBody();
        assert winningHorse != null;

        System.out.println("The winner is: "+winningHorse.horseName+"!");
        int i = 0;
        while (!currentRace.horses.get(i).horseName.equals(winningHorse.horseName)){
            i++;
        }
        Winner winner = new Winner(winningHorse, currentRace.horseOdds.get(i));
        messagingTemplate.convertAndSend(currentRace.raceEndpoint, winner);

        Thread.sleep(3000);
        currentRace = getNewRace();
        currentRaceId += 1;
        currentRace.raceEndpoint = RACE_UPDATES_TOPIC + "/" + currentRaceId;
        messagingTemplate.convertAndSend(RACE_UPDATES_TOPIC, currentRace);
    }

    private void broadCastNewRace(){
        currentRace = getNewRace();
        currentRaceId += 1;
        currentRace.raceEndpoint = RACE_UPDATES_TOPIC + "/" + currentRaceId;
        messagingTemplate.convertAndSend(RACE_UPDATES_TOPIC, currentRace);
    }

    private Race getNewRace(){
        String urlRace = "http://racegenerator-service:8083/generate-races";
        ResponseEntity<Race> response = template.getForEntity(urlRace, Race.class);
        Race race = response.getBody();
        assert race != null;
        System.out.println("New race created.");
        System.out.println("Horses in Race:");
        for (Horse horse : race.horses){
            System.out.println(horse.horseName);
        }
        return race;
    }

    private String getHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress() + ":";
        } catch (UnknownHostException e) {
            return "localhost" + port;
        }
    }
}
