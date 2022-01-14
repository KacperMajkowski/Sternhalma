package database;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameRepository extends CrudRepository<Games, Integer> {
    //List<Games> findGamesByGameNumberAfter(int i);

}
