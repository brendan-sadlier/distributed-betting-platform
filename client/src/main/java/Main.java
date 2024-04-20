import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import service.core.Bet;
import service.core.Horse;
import service.core.Race;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {
    private static class ClientWebSocket extends WebSocketClient {
        private static final ObjectMapper objectMapper = new ObjectMapper();
        public Race currentRace;

        public ClientWebSocket(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            System.out.println("Connected to the server");
        }

        @Override
        public void onMessage(String message) {
            if (message.trim().startsWith("{")) {
                // Handle JSON message as Race object
                try {
                    Race race = objectMapper.readValue(message, Race.class);
                    currentRace = race;
                    displayRace(race);
                    inputBet();
                } catch (Exception e) {
                    System.err.println("Error processing JSON message: " + e.getMessage());
                }
            } else {
                // Bet results are received from the bookie as Strings that can be printed
                // straight to the command line
                System.out.println(message);
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            System.out.println("Disconnected from server: " + reason);
        }

        @Override
        public void onError(Exception ex) {
            System.out.println("An error occurred: " + ex.getMessage());
        }

        private static void displayRace(Race race) {
            System.out.println("|===============================================================================================================|");
            System.out.println("| Race: " + race.raceName +" \t\tTime of Race: " + race.dateAndTime + "\t\t\t\t\t\t\t\t\t|");
            System.out.println("|---------------------------------------------------------------------------------------------------------------|");
            for (int i = 0; i < race.horses.size(); i++){
                displayHorse(race.horses.get(i), race.horseOdds.get(i));
            }
            System.out.println("|===============================================================================================================|");
        }

        private static void displayHorse(Horse horse, Double odds){
            System.out.println(
                    "| Horse: " + horse.horseName + "\t\tJockey: " + horse.jockey + "\t\tTrainer: " + horse.trainer +
                            "\t\tAge: " + horse.age + "\t\tOdds: " + odds + "\t|");
        }

        private void inputBet(){
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Enter your bet in the format 'HorseName Amount'" +
                        " or type 'quit' to not place a bet:");
                String input = scanner.nextLine();
                if ("quit".equalsIgnoreCase(input)) {
                    break;
                }
                try {
                    String[] parts = input.split(" ");
                    String horseName = parts[0];
                    Integer amount = Integer.parseInt(parts[1]);

                    boolean horseExists = currentRace.horses.stream()
                            .anyMatch(horse -> horseName.equals(horse.horseName));

                    if (horseExists){
                        Bet bet = new Bet(horseName, amount);
                        String jsonBet = objectMapper.writeValueAsString(bet);
                        send(jsonBet);  // Sending bet details to the server
                        break;
                    }else{
                        System.out.println("Invalid horse name. Please enter a valid bet.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input. Please try again.");
                }
            }
            scanner.close();
        }

    }

    public static void main(String[] args) throws URISyntaxException {
        ObjectMapper objectMapper = new ObjectMapper();
            URI uri = new URI("ws://localhost:8080/bookie-websocket");
            ClientWebSocket client = new ClientWebSocket(uri);
            client.connect();
    }

}



