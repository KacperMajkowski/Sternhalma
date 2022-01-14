package database;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Games {
    @Id
    @GeneratedValue
    private Integer gameNumber;
    private int playersNumber;

    public Games(int playersNumber) {
        this.playersNumber = playersNumber;
    }

    public Games() {

    }

    public int getPlayersNumber() {
        return playersNumber;
    }

    public void setPlayersNumber(int playersNumber) {
        this.playersNumber = playersNumber;
    }

    public Integer getGameNumber() {
        return gameNumber;
    }

    public void setGameNumber(Integer id) {
        this.gameNumber = id;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + gameNumber +
                ", playersNumber=" + playersNumber +
                '}';
    }
}
