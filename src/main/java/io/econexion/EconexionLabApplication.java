package io.econexion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Econexion Lab Spring Boot application.
 * <p>
 * This class bootstraps the entire application context and starts the
 * embedded server (e.g. Tomcat) using {@link SpringApplication#run}.
 * </p>
 */
@SpringBootApplication
public class EconexionLabApplication {

    /**
     * Application bootstrap method.
     * <p>
     * Invoked by the JVM. Delegates to Spring Boot's {@link SpringApplication#run}
     * to initialize the application context and start the web environment.
     * </p>
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(EconexionLabApplication.class, args);
    }
}
