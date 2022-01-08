package main;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageBuilderTest {

    @Test
    void addString() {
        MessageBuilder mb = new MessageBuilder();

        mb.add("TEST");

        assertEquals(mb.build(),"TEST");
    }

    @Test
    void addInt() {
        MessageBuilder mb = new MessageBuilder();

        mb.add(123);

        assertEquals(mb.build(),String.valueOf(123));
    }

    @Test
    void addColor() {
        MessageBuilder mb = new MessageBuilder();

        mb.add(Color.AQUA);

        assertEquals(mb.build(),String.valueOf(Color.AQUA));
    }

    @Test
    void addCombined() {
        MessageBuilder mb = new MessageBuilder();

        mb.add(Color.AQUA).add(123).add("TEST").add(Color.AZURE);

        assertEquals(mb.build(),String.valueOf(Color.AQUA)+" "+String.valueOf(123)+" TEST "+String.valueOf(Color.AZURE));
    }

    @Test
    void clear() {
        MessageBuilder mb = new MessageBuilder();
        mb.add("TEST").clear();
        assertEquals("",mb.build());
    }
}
