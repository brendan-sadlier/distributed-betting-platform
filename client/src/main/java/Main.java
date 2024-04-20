import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import service.core.Bet;
import service.core.Horse;
import service.core.Race;
import java.lang.reflect.Type;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Main {

    private static final String URL = "ws://localhost:8080/bookie-websocket";
    private static StompSession session;
    private static final CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        stompClient.connect(URL, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.subscribe("/user/queue/personalRaceUpdate", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return Race.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        Race race = (Race) payload;
                        displayRace(race);
                        // Additional handling to display the race
                    }
                });
                session.subscribe("/topic/raceUpdates", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return Race.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        Race race = (Race) payload;
                        displayRace(race);
                        handleUserInput(session);
                    }
                });
                Main.session = session;
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers,
                                        byte[] payload, Throwable exception) {
                System.err.println("Got an exception: " + exception.getMessage());
            }
        });

        latch.await();  // Wait indefinitely, keeping the program running
    }

    private static void handleUserInput(StompSession session) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your bet in the format 'HorseName Amount', or type 'quit' to exit:");
        while (true) {
            String input = scanner.nextLine();
            if ("quit".equalsIgnoreCase(input)) {
                latch.countDown();  // Allow the application to terminate
                break;
            }
            try {
                String[] parts = input.split(" ");
                String horseName = parts[0];
                int amount = Integer.parseInt(parts[1]);
                Bet bet = new Bet(horseName, amount);
                session.send("/app/bet", bet);

                break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please try again.");
            }
        }
        scanner.close();
    }

    private static void displayRace(Race race) {
        System.out.println("|===============================================================================================|");
        System.out.println(String.format("| Race: %-20s Time of Race: %-30s |", race.raceName, race.dateAndTime));
        System.out.println("|-----------------------------------------------------------------------------------------------|");
        for (int i = 0; i < race.horses.size(); i++) {
            displayHorse(race.horses.get(i), race.horseOdds.get(i));
        }
        System.out.println("|===============================================================================================|");
    }

    private static void displayHorse(Horse horse, Double odds) {
        System.out.println(String.format("| Horse: %-15s Jockey: %-15s Trainer: %-15s Age: %-3d Odds: %-6.2f |",
                horse.horseName, horse.jockey, horse.trainer, horse.age, odds));
    }
}
