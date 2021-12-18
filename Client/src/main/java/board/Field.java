package board;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

public class Field {

    final private int x;
    final private int y;
    final private Circle circle;
    private Color color = Color.WHITE;

    public Field(int x, int y, Circle circle) {
        this.x = x;
        this.y = y;
        this.circle = circle;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Circle getCircle() {
        return circle;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        circle.setFill(color);
    }

    /**
     * Changing strokeType so that the field looks like it's selected or not.
     * @param isSelected is the field selected.
     */
    public void setSelected(boolean isSelected) {
        StrokeType strokeType;
        if( isSelected ) {
            strokeType = StrokeType.OUTSIDE;
        }
        else {
            strokeType = StrokeType.INSIDE;
        }
        circle.setStrokeType( strokeType );
    }
}
