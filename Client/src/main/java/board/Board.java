package board;

import javafx.scene.paint.Color;

import java.util.List;
import java.util.Objects;

public class Board {

    private List<Field> fieldList;
    private Field selectedField = null;

    public Board( List<Field> fieldList )
    {
        this.fieldList = fieldList;
    }

    public Field getSelectedField() {
        return selectedField;
    }

    /**
     * Selecting field.
     * @param field field to select
     */
    public void selectField(Field field)
    {
        deselectAllFields();
        selectedField = field;
        field.setSelected(true);
    }

    /**
     * Deselecting all fields.
     */
    public void deselectAllFields()
    {
        selectedField = null;
        for( Field field : fieldList )
            field.setSelected( false );
    }

    /**
     * Getting field by its coordinates.
     * @param x coordinate x.
     * @param y coordinate y.
     * @return field
     */
    public Field getField( int x, int y )
    {
        for( Field field : fieldList )
        {
            if( field.getX() == x && field.getY() == y )
                return field;
        }
        return null;
    }

    /**
     * Making move on the board.
     * @param color color of the moved piece.
     * @param x1 x coordinate of the moved piece.
     * @param y1 y coordinate of the moved piece.
     * @param x2 x coordinate of where the piece has been moved.
     * @param y2 y coordinate of where the piece has been moved.
     */
    public void makeMove(Color color, int x1, int y1 , int x2, int y2) {

        deselectAllFields();
        addPiece(color,x2, y2);
        removePiece(x1,y1);
    }

    /**
     * Removing piece from field.
     * @param x2 x coordinate of the removed piece.
     * @param y2 y coordinate of the removed piece.
     */
    private void removePiece(int x2, int y2) {
        Objects.requireNonNull(getField(x2, y2)).setColor(Color.WHITE);
    }

    /**
     * Adding piece to a field.
     * @param color color of added piece.
     * @param x1 x coordinate of the added piece.
     * @param y1 y coordinate of the added piece.
     */
    public void addPiece(Color color, int x1, int y1) {
        Objects.requireNonNull(getField(x1, y1)).setColor(color);
    }
}
