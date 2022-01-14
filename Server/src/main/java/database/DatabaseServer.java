package database;

import Replay.GameReplay;
import Replay.ServerReplay;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example.server.Game;

import java.util.List;

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
            GameReplay.gr = gameRepository;
            GameReplay.mr = moveRepository;
            ServerReplay.gr = gameRepository;
            ServerReplay.mr = moveRepository;
            //System.out.println(gameRepository.findAll());
            //System.out.println(moveRepository.findAll());
        });
    }

    public void InsertMove(int gameNumber, String command, String color, int x1, int y1, int x2, int y2) {
        Game.mr.save(new Move(gameNumber,command,color,x1,y1,x2,y2));
    }
    
    public void InsertGame(int playersNumber) {
        Game.gr.save(new Games(playersNumber));
    }
    
    public List<Move> getMoves(int id) {
        return GameReplay.mr.findMovesByGameNumber(id);
    }
    
    public Games getPlayerNumber(int num) {
        return ServerReplay.gr.findGamesByGameNumber(num);
    }

    public int getGameNo() {
        int max = 0;
        for (Games g : Game.gr.findAll()) {
            if (max < g.getGameNumber()) {
                max = g.getGameNumber();
            }
        }
        return max;
    }
}
