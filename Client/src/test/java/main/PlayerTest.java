package main;

import board.Board;
import board.Field;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    private final Field selectedField = new Field(3,3, new Circle());
    private final Board board = createTestBoard();

    @Test
    public void handleMoveClick() {
        Player player = createTestPlayer();
        board.selectField(selectedField);
        player.handleMouseClicked(selectedField);
        assertNull(board.getSelectedField());
    }

    @Test
    public void handleSelectClick() {
        Player player = createTestPlayer();
        selectedField.setColor(Color.RED);
        player.handleMouseClicked(selectedField);
        assertEquals(board.getSelectedField(),selectedField);
    }

    @Test
    public void getCurrentPlayerColor() {
        Player player = createTestPlayer();

        assertEquals(player.getCurrentPlayerColor(), Color.RED);

    }

    @Test
    public void setPlayerColor() {
        Player player = createTestPlayer();

        player.setCurrentPlayerColor(Color.AQUA);
        assertEquals(player.getCurrentPlayerColor(), Color.AQUA);
    }

    private Board createTestBoard()
    {
        List<Field> fields = new ArrayList<>();

        fields.add( new Field( 0, 0, new Circle() ) );
        fields.add( new Field( 0, 1, new Circle() ) );
        fields.add( new Field( 0, 2, new Circle() ) );
        fields.add( new Field( 1, 0, new Circle() ) );
        fields.add( new Field( 1, 1, new Circle() ) );
        fields.add( new Field( 1, 2, new Circle() ) );
        fields.add(selectedField);

        return new Board( fields );
    }

    private Player createTestPlayer() {
        /*PrintWriter out = null;
        BufferedReader in = null;
        Socket socket = null;
        try {
            socket = new Socket("localhost", 4444 );
            out = new PrintWriter( socket.getOutputStream(), true );
            in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return new Player(board, null, null , Color.RED);
    }
}
