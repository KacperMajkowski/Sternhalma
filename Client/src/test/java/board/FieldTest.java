package board;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FieldTest
{
    private Circle circle;
    @Test
    public void getCircle() {
        Field field = createTestField();

        assertEquals(field.getCircle(), circle);
    }

    @Test
    public void getX()
    {
        Field field = createTestField();
        assertEquals( 3, field.getX() );
    }

    @Test
    public void getY()
    {
        Field field = createTestField();
        assertEquals( 8, field.getY() );
    }

    @Test
    public void getColor()
    {
        Field field = createTestField();

        assertEquals( Color.WHITE, field.getColor() );
    }

    @Test
    public void setColor() {
        Field field = createTestField();

        field.setColor(Color.RED);
        assertEquals( Color.RED, field.getColor() );

        field.setColor( Color.BLUE );
        assertEquals( Color.BLUE, field.getColor() );
    }

    @Test
    public void setSelected()
    {
        Field field = createTestField();

        field.setSelected(false);
        assertEquals(field.getCircle().getStrokeType(), StrokeType.INSIDE);

        field.setSelected(true);

        assertEquals(field.getCircle().getStrokeType(), StrokeType.OUTSIDE);
    }

    private Field createTestField()
    {
        return new Field( 3, 8, circle );
    }
}