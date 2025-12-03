package io.econexion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EconexionLabApplication {
    public static void main(String[] args) {
        System.out.println("Starting Econexion Lab Application...");
        System.out.println("------------------------------");

        SpringApplication.run(EconexionLabApplication.class, args);
    }
}
