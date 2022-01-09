package board;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest
{
    @Test
    public void  addPiece()
    {
        Board board = createTestBoard();

        board.addPiece(Color.RED, 0, 1 );
        assertEquals(board.getField(0,1).getColor(), Color.RED);
    }

    @Test
    public void selectField() {

        Board board = createTestBoard();

        board.selectField(board.getField(0,0));
        board.selectField(board.getField(0,1));
        assertEquals(board.getSelectedField(),board.getField(0,1));
    }

    @Test
    public void deselectAllFields()
    {
        Board board = createTestBoard();

        board.selectField(board.getField(0,1));
        board.deselectAllFields();
        assertNull( board.getSelectedField() );
    }

    @Test
    public void makeMove() {
        Board board = createTestBoard();

        board.addPiece(Color.RED, 0, 1 );
        board.makeMove(Color.RED,0,1,1,1);
        assertEquals(board.getField(0,1).getColor(),Color.WHITE);
        assertEquals(board.getField(1,1).getColor(),Color.RED);
    }

    @Test
    public void getNotExistingField()
    {
        Board board = createTestBoard();

        assertNull(board.getField(-1, -1));
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

        return new Board( fields );
    }

}