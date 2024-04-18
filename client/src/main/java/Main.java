import service.core.Horse;
import service.core.Race;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URISyntaxException;

public class Main {
    private static class WebsocketClientEndpoint extends WebSocketClient {
        public WebsocketClientEndpoint(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            System.out.println("Opened connection with Bookie");
        }

        @Override
        public void onMessage(String message) {
            System.out.println("Received: " + message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            System.out.println("Connection closed with exit code " + code + " and reason: " + reason);
        }

        @Override
        public void onError(Exception ex) {
            System.out.println("An error occurred:" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            URI uri = new URI("ws://bookie.example.com/socket");
            WebsocketClientEndpoint client = new WebsocketClientEndpoint(uri);
            client.connect();
            // Wait for the client to establish a connection
            while (!client.isOpen()) {
                Thread.sleep(100);
            }
            // Send a message or interact with the connection
            // Example: client.send("Hello, World!");
        } catch (URISyntaxException | InterruptedException e) {
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
