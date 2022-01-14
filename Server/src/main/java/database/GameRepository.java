package database;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameRepository extends CrudRepository<Games, Integer> {
    
    Games findGamesByGameNumber(int num);
    
}
