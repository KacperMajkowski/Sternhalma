package database;

import org.springframework.data.repository.CrudRepository;

import java.util.*;
import java.net.Inet4Address;

public interface MoveRepository extends CrudRepository<Move, Integer> {

	List<Move> findMovesByGameNumber(int id);
	
}
