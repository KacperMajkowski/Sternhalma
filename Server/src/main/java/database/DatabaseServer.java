package database;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example.server.Game;

@SpringBootApplication
public class DatabaseServer {

    public static void main(String[] args) {
        SpringApplication.run(DatabaseServer.class, args);
    }
    
    @Bean
    public CommandLineRunner run(MoveRepository moveRepository, GameRepository gameRepository) {
        return (args -> {
            Game.gr = gameRepository;
            Game.mr = moveRepository;
            InsertMove();
            System.out.println(moveRepository.findAll());
            InsertGame();
            System.out.println(gameRepository.findAll());
            System.out.println(findGame(1));
        });
    }

    public void InsertMove() {
        Game.mr.save(new Move(1,"A",1,1,1,2));
    }
    
    public void InsertGame() {
        Game.gr.save(new database.Game(2));
    }
    
    public database.Game findGame(int id) {
        return Game.gr.findGamesById(id);
    }

}
