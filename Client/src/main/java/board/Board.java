package board;

import javafx.scene.paint.Color;

import java.util.List;

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

    public void selectField(Field field)
    {
        System.out.println("3");
        deselectAllFields();
        selectedField = field;
        field.setSelected(true);
    }

    public void deselectAllFields()
    {
        selectedField = null;
        for( Field field : fieldList )
            field.setSelected( false );
    }

    private Field getField( int x, int y )
    {
        for( Field field : fieldList )
        {
            if( field.getX() == x && field.getY() == y )
                return field;
        }
        return null;
    }

    public void makeMove(Color color, int x1, int y1 , int x2, int y2) {
        addPiece(color,x1, y1);
        removePiece(x2,y2);
    }

    private void removePiece(int x2, int y2) {
        getField(x2, y2).setColor(Color.WHITE);
    }

    public void addPiece(Color color, int x1, int y1) {
        getField(x1,y1).setColor(color);
    }
}
