package board;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import main.Controller;

import java.util.ArrayList;
import java.util.List;

public class Board {

    /** Reference to the pane containing Circle objects */
    @FXML
    private Pane boardPane;
    @FXML
    private Label playerColorLabel;
    @FXML
    private Label currentColorLabel;

    private Controller controller;

    private List<Field> fields = new ArrayList<>();

    private Field selectedField = null;

    private EventHandler<? super MouseEvent> OnFieldClickedListener = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            Circle circle = (Circle) mouseEvent.getSource();
            Field clickedField = getFieldfromCircle(circle);
            if (selectedField == clickedField) {
                deselectAllFields();
            }
            else if (selectedField == null) {
                handleSelectField(clickedField);
            }
            else {
                tryToMakeMove(clickedField);
            }
        }

        private void tryToMakeMove(Field clickedField) {
            if (controller.makeMove(selectedField.getX(),selectedField.getY(),clickedField.getX(),clickedField.getY())) {
                makeMove(controller.getPlayerColor(),selectedField.getX(),selectedField.getY(),clickedField.getX(),clickedField.getY());
                //chyba usunac metode i wywolywac waitForServer
            }
        }

        private void handleSelectField(Field clickedField) {
            if (controller.checkIfSelectPossible(clickedField.getColor())) {
                selectField(clickedField);
            }
        }
    };

    /**
     * Function launched at the start od the application
     */
    @FXML
    private void initialize() throws Exception {
        controller = new Controller();
        controller.newConnection();
        assignFields();
        setPlayerColor();
        waitForYourTurn();
    }

    private void setPlayerColor() {
        Color playerColor = controller.waitForColorSet();
        playerColorLabel.setBackground(new Background(new BackgroundFill(playerColor, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void setCurrentTurn() {
        Color color = Color.WHITE;
        currentColorLabel.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
    }
    private void waitForYourTurn() {
        //String response = controller.waitForServerResponse();
        //switch ()
    }

    private Field getFieldfromCircle(Circle circle) {
        for (Field f: fields){
            if (f.getCircle().equals(circle)) {
                return f;
            }
        }
        return null;
    }

    private void assignFields() {
        int x = 0;
        int y = 0;

        for( Node node : boardPane.getChildren() )
        {
            fields.add(new Field(x,y,(Circle) node));
            node.setOnMouseClicked(OnFieldClickedListener);

            if( x == 12 )
            {
                x = 0;
                y++;
            }
            else
            {
                x++;
            }
        }
    }

    public void selectField(Field field)
    {
        deselectAllFields();
        selectedField = field;

        if( selectedField != null )
        {
            selectedField.setSelected(true);
        }
    }

    private void deselectAllFields()
    {
        selectedField = null;
        for( Field field : fields )
            field.setSelected( false );
    }

    private Field getField( int x, int y )
    {
        for( Field field : fields )
        {
            if( field.getX() == x && field.getY() == y )
                return field;
        }
        return null;
    }

    private void makeMove(Color color, int x1, int y1 ,int x2, int y2) {
        addPiece(color,x1, y1);
        removePiece(x2,y2);
    }

    private void removePiece(int x2, int y2) {
        getField(x2, y2).setColor(Color.WHITE);
    }

    private void addPiece(Color color, int x1, int y1) {
        getField(x1,y1).setColor(color);
    }
}
