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
    public CommandLineRunner run(MoveRepository moveRepository) {
        return (args -> {
            Insert(moveRepository);
            System.out.println(moveRepository.findAll());
        });
    }

    private  void Insert(MoveRepository gameRepository) {
        gameRepository.save(new Move(1,"A",1,1,1,2));
        //gameRepository.save(new Game(2));
        //gameRepository.save(new Game(6));
    }

}
