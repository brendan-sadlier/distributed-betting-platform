package service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SpringBootWebApplication extends SpringBootServletInitializer implements CommandLineRunner {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SpringBootWebApplication.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringBootWebApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        while (true){
            try{
                String service_host = args[0];
                String bookie_host = args[1];
                String bookie_port = args[2];
                String oddsBuilderServiceUrl = "http://"+getHost(service_host)+"/odds";
                RestTemplate template = new RestTemplate();
                ResponseEntity<String> response = template.postForEntity("http://"+bookie_host+":"+bookie_port+"/services", oddsBuilderServiceUrl, String.class);
                if (response.getStatusCode().equals(HttpStatus.CREATED)) {
                    System.out.println("Successfully connected to Bookie");
                    break;
                }
            }catch (Exception e){
                System.out.println("Failed to connect to Bookie. Will try again in 1 second");
                Thread.sleep(1000);
            }
        }
    }

    @Value("${server.port}")
    private int port;

    private String getHost(String service_host) {
        try {
            return service_host + ":" + port;
        } catch (Exception e) {
            return "localhost:" + port;
        }
    }
}