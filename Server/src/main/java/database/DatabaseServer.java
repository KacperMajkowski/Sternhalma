package database;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DatabaseServer {

    public static void main(String[] args) {
        SpringApplication.run(DatabaseServer.class, args);
    }

    @Bean
    public CommandLineRunner run(GameRepository gameRepository) {
        return (args -> {
            Insert(gameRepository);
            System.out.println(gameRepository.findAll());
        });
    }

    private  void Insert(GameRepository gameRepository) {
        gameRepository.save(new Game(4));
        gameRepository.save(new Game(2));
        gameRepository.save(new Game(6));
    }

}
