import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import service.core.Bet;
import service.core.Horse;
import service.core.Race;
import service.core.Winner;
import java.math.BigDecimal;
import java.math.RoundingMode;

import java.lang.reflect.Type;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    // ws://<minikube ip>:<bookie service port>/bookie-websocket
    private static final String URL = "ws://127.0.0.1:8080/bookie-websocket";
    private static StompSession session;
    private static String currentRaceEndpoint;
    private static Bet currentBet;
    private static final CountDownLatch latch = new CountDownLatch(1);
    private static final Scanner scanner = new Scanner(System.in);
    private static final Object scannerLock = new Object();
    static ExecutorService executorService = Executors.newFixedThreadPool(10);

    private static class MessageProcessor implements Runnable {
        private final Object message;

        public MessageProcessor(Object message) {
            this.message = message;
        }

        @Override
        public void run() {
            if (message instanceof Race) {
                processRace((Race) message);
            } else if (message instanceof Winner) {
                processBetResult((Winner) message);
            } else {
                System.err.println("Unhandled message type: " + message.getClass());
            }
        }
        private void processRace(Race race) {
            displayRace(race);
            currentRaceEndpoint = race.raceEndpoint;
            handleUserInput();
        }

        private void processBetResult(Winner winner) {
            System.out.println("The winning horse was "+winner.horse.horseName);
            if (currentBet.horseName.equals(winner.horse.horseName)){
                double amount = currentBet.amount * winner.odds;
                BigDecimal bd = new BigDecimal(amount);
                bd = bd.setScale(2, RoundingMode.HALF_UP);
                double amountWon = bd.doubleValue();
                System.out.println("Congratulations! You have won €"+amountWon+"\n");
            }else{
                System.out.println("Hard luck, you're horse did not win :(\n");
            }
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
                session.subscribe("/topic/raceUpdates", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return Race.class;
                    }
                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        executorService.execute(new MessageProcessor(payload));
                    }
                });
                System.out.println("Subscribed to /topic/raceUpdates");
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
        System.out.println("Enter your bet in the format 'HorseName Amount', or type 'no' to not bet on this race:");
        while (true) {
            String input = null;
            synchronized (scannerLock) {
                if (scanner.hasNextLine()) {
                    input = scanner.nextLine();
                }
            }
            if (input != null) {
                if ("no".equalsIgnoreCase(input)) {
                    System.out.println("No bet placed on this race\n");
                    break;
                }
                if (processInput(input)) {
                    break;
                }
            }
        }
    }


    private static boolean processInput(String input) {
        try {
            String[] parts = input.split(" ");
            String horseName = parts[0];
            int amount = Integer.parseInt(parts[1]);
            if (amount <= 0){
                System.out.println("Bet amounts must be > 0");
                throw new Exception("Invalid input amount");
            }
            currentBet = new Bet(horseName, amount);
            session.subscribe(currentRaceEndpoint, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return Winner.class;
                }
                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    executorService.execute(new MessageProcessor(payload));
                }
            });
            System.out.println("Bet of "+currentBet.amount+" placed on "+currentBet.horseName+"\n");
            return true; // Return true when processing is successful
        } catch (Exception e) {
            System.out.println("Invalid input. No bet placed\n");
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
