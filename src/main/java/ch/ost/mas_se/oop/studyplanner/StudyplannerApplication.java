package ch.ost.mas_se.oop.studyplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StudyplannerApplication {

    static void main(String[] args) {
        SpringApplication.run(StudyplannerApplication.class, args);
    }

    @Bean
    public CommandLineRunner openBrowser() {
        return args -> {
            try {
                String url = "http://localhost:8080";
                String osName = System.getProperty("os.name").toLowerCase();

                if (osName.contains("win")) {
                    new ProcessBuilder("cmd", "/c", "start", url).start();
                } else if (osName.contains("mac")) {
                    new ProcessBuilder("open", url).start();
                } else if (osName.contains("nix") || osName.contains("nux")) {
                    new ProcessBuilder("firefox", url).start();
                }
            } catch (Exception e) {
                System.err.println("Could not open browser: " + e.getMessage());
            }
        };
    }
}