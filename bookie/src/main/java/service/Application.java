package service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import service.controllers.BookieController;

@SpringBootApplication
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(BookieController controller) {
        return args -> {
            controller.registerService("http://localhost:8080/quotations");
            controller.registerService("http://localhost:8082/quotations");
            controller.registerService("http://localhost:8081/quotations");
        };
    }
}
