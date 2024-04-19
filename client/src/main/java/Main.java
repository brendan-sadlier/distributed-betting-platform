import com.fasterxml.jackson.databind.ObjectMapper;
import service.core.Horse;
import service.core.Race;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Main {
    private static class Client extends WebSocketClient {

        public Client(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            System.out.println("Opened connection with Bookie");
            // You can request the server to send a race object right after connection
            send("send me a race");
        }

        @Override
        public void onMessage(String message) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode rootNode = mapper.readTree(message);
                String type = rootNode.get("type").asText();
                switch (type) {
                    case "race":
                        Race race = mapper.treeToValue(rootNode.get("data"), Race.class);
                        displayRace(race);
                        break;
                    case "message":
                        String textMessage = rootNode.get("data").asText();
                        System.out.println("Received message: " + textMessage);
                        break;
                    default:
                        System.out.println("Unknown message type received.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            System.out.println("Closed with exit code " + code + " and reason: " + reason);
        }

        @Override
        public void onError(Exception ex) {
            System.out.println("An error occurred:" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            URI uri = new URI("ws://bookie.example.com/socket"); // Replace with your actual WebSocket URI
            Client client = new Client(uri);
            client.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void displayRace(Race race) {
        System.out.println("|===============================================================================================================|");
        System.out.println("| Race: " + race.raceName +" \t\tTime of Race: " + race.dateAndTime + "\t\t\t\t\t\t\t\t\t|");
        System.out.println("|---------------------------------------------------------------------------------------------------------------|");
        for (int i = 0; i < race.horses.size(); i++){
            displayHorse(race.horses.get(i), race.horseOdds.get(i));
        }
        System.out.println("|===============================================================================================================|");
    }

    public static void displayHorse(Horse horse, Double odds){
        System.out.println(
                "| Horse: " + horse.horseName + "\t\tJockey: " + horse.jockey + "\t\tTrainer: " + horse.trainer +
                        "\t\tAge: " + horse.age + "\t\tOdds: " + odds + "\t|");
    }
}
