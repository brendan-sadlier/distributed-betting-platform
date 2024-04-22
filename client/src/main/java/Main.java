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
    private static final Scanner scanner = new Scanner(System.in);

    private static class MessageProcessor implements Runnable {
        private final Object message;

        public MessageProcessor(Object message) {
            this.message = message;
        }

        @Override
        public void run() {
            if (message instanceof Race) {
                processRace((Race) message);
            } else if (message instanceof String) {
                processBetResult((String) message);
            } else {
                System.err.println("Unhandled message type: " + message.getClass());
            }
        }
        private void processRace(Race race) {
            displayRace(race);
            handleUserInput();
        }

        private void processBetResult(String message) {
            System.out.println("Bet Result: " + message);
        }
    }


    public static void main(String[] args) throws InterruptedException {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        stompClient.connect(URL, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                System.out.println("Connected to bookie at: " + URL);
                Main.session = session;
                session.subscribe("/user/queue/personalRaceUpdate", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return Race.class;
                    }
                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        Thread processingThread = new Thread(new MessageProcessor(payload));
                        processingThread.start();
                    }
                });
                System.out.println("Subscribed to /user/queue/personalRaceUpdate");
                session.subscribe("/topic/raceUpdates", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return Race.class;
                    }
                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        Thread processingThread = new Thread(new MessageProcessor(payload));
                        processingThread.start();
                    }
                });
                System.out.println("Subscribed to /topic/raceUpdates");
                session.subscribe("/user/queue/results", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return String.class;
                    }
                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        System.out.println("Bet message received from bookie");
                        Thread processingThread = new Thread(new MessageProcessor(payload));
                        processingThread.start();
                    }
                });
                System.out.println("Subscribed to /user/queue/results");
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                exception.printStackTrace();
            }
            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                System.err.println("Error in WebSocket transport: " + exception.getMessage());
            }
        });

        latch.await();  // Wait indefinitely, keeping the program running
    }

    private static void handleUserInput() {
        System.out.println("Enter your bet in the format 'HorseName Amount', or type 'quit' to exit:");
        while (true) {
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                if ("quit".equalsIgnoreCase(input)) {
                    System.out.println("Exiting...");
                    latch.countDown();  // Allow the application to terminate
                    break;
                }
                if (processInput(input)) {
                    break;
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted: " + e.getMessage());
                break;
            }
        }
    }



    private static boolean processInput(String input) {
        try {
            String[] parts = input.split(" ");
            String horseName = parts[0];
            int amount = Integer.parseInt(parts[1]);
            Bet bet = new Bet(horseName, amount);
            if (session != null) {
                session.send("/app/bet", bet);
                System.out.println("Bet of " + amount + " on horse " + horseName + " successfully placed.");
            }
            return true; // Return true when processing is successful
        } catch (Exception e) {
            System.out.println("Invalid input. Please try again.");
            return false; // Return false when an error occurs
        }
    }



    private static void displayRace(Race race) {
        System.out.println("|===============================================================================================|");
        System.out.println(String.format("| Race: %-20s Time of Race: %-52s |", race.raceName, race.dateAndTime));
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
