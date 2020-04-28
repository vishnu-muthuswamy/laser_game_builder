package lasers.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import lasers.model.*;

/**
 * The main class that implements the JavaFX UI.   This class represents
 * the view/controller portion of the UI.  It is connected to the lasers.lasers.model
 * and receives updates from it.
 *
 * @author RIT CS
 * @author Lukowski, Matthew and Muthuswamy, Vishnu
 */
public class LasersGUI extends Application implements Observer<LasersModel, ModelData> {

    /** the eighteen types of pokemon we have */
    private enum TileType {
        EMPTY,
        ERROR,
        BEAM,
        LASER,
        PILLAR0,
        PILLAR1,
        PILLAR2,
        PILLAR3,
        PILLAR4,
        PILLARX,
    }

    /** The UI's connection to the lasers.lasers.model */
    private LasersModel model;

    private HashMap<String, TileType> tilesHM;
    private List<String> tilesPTUI = Arrays.asList(".", "error", "*", "L", "0", "1", "2", "3", "4", "X");

    private Label statusMessage;
    private GridPane safeGridPane;

    private Image white = new Image(this.getClass().getResourceAsStream(
            "resources/white.png"));
    private Image red = new Image(this.getClass().getResourceAsStream(
            "resources/red.png"));

    private Image beam = new Image(this.getClass().getResourceAsStream(
            "resources/beam.png"));
    private Image laser = new Image(this.getClass().getResourceAsStream(
            "resources/laser.png"));
    private Image pillar0 = new Image(this.getClass().getResourceAsStream(
            "resources/pillar0.png"));
    private Image pillar1 = new Image(this.getClass().getResourceAsStream(
            "resources/pillar1.png"));
    private Image pillar2 = new Image(this.getClass().getResourceAsStream(
            "resources/pillar2.png"));
    private Image pillar3 = new Image(this.getClass().getResourceAsStream(
            "resources/pillar3.png"));
    private Image pillar4 = new Image(this.getClass().getResourceAsStream(
            "resources/pillar4.png"));
    private Image pillarx = new Image(this.getClass().getResourceAsStream(
            "resources/pillarx.png"));

    /**
     * Create a HashMap for the pokemon and their corresponding letters on the server board.
     *
     * @param tile the Array of Pokemon enum.
     * @param DIM a dimension of the server board
     */
    private HashMap<String, TileType> makeTilesHM(TileType[] tileTypes) {
        HashMap<String, TileType> tilesHashMap= new HashMap<>();

        for (int i=0; i<tilesPTUI.size(); i++) {
            tilesHashMap.put(tilesPTUI.get(i), tileTypes[i]);
        }

        return tilesHashMap;
    }

    /**
     * A private utility function for setting the background of a button to
     * an image in the resources subdirectory.
     *
     * @param button the button control
     * @param bgImgName the name of the image file
     */
    private void setButtonBackground(Button button, String bgImgName) {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image( getClass().getResource("resources/" + bgImgName).toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);
        button.setBackground(background);
    }

    /**
     * The class that represents the PokemonButton.
     */
    private class Tile extends Button {

        /**
         * Initialize the PokemonButton with the Pokeball image and blue background.
         *
         * @param pokemon an element from Pokemon enum
         */
        public Tile() {
            setButtonBackground(this, "white.png");
        }

        /**
         * Change the image of a PokemonButton.
         *
         * @param value the character corresponding to the PokemonButton to be changed.
         * @param hidden    whether or not the PokemonButton to be changed is hidden.
         */
        public void setImage(String tilePTUI) {
            /** the type of this pokemon */
            TileType tile = tilesHM.get(tilePTUI);

            Image image;

            switch (tile) {
                case EMPTY:
                    image = white;
                    break;
                case ERROR:
                    image = red;
                    break;
                case BEAM:
                    image = beam;
                    break;
                case LASER:
                    image = laser;
                    break;
                case PILLAR0:
                    image = pillar0;
                    break;
                case PILLAR1:
                    image = pillar1;
                    break;
                case PILLAR2:
                    image = pillar2;
                    break;
                case PILLAR3:
                    image = pillar3;
                    break;
                case PILLAR4:
                    image = pillar4;
                    break;
                case PILLARX:
                default:
                    image = pillarx;
            }
            this.setGraphic(new ImageView(image));
        }
    }

    /**
     * Create the GridPane for the GUI.
     */
    private GridPane makeSafeGridPane(){
        GridPane safeGridPane = new GridPane();
        safeGridPane.setAlignment(Pos.TOP_CENTER);

        for (int row = 0; row < model.getRowDim(); ++row) {
            for (int col = 0; col < model.getColDim(); ++col) {
                int rowCoordinate = row;
                int colCoordinate = col;

                Tile tile = new Tile();
                tile.setImage(model.getBoard()[row][col]);
                tile.setOnAction(event ->
                        model.add(rowCoordinate, colCoordinate));

                // JavaFX uses (x, y) pixel coordinates instead of
                // (row, col), so must invert when adding
                safeGridPane.add(tile, col, row);
            }
        }

        return safeGridPane;
    }

    private void load(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("."));
        File safeFile = fileChooser.showOpenDialog(stage);

        try {
            if (safeFile != null) {

                model = new LasersModel(safeFile.getPath());
                model.addObserver(this);
                model.makeBoard();

                start(stage);
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void init() throws Exception {
        // the init method is run before start.  the file name is extracted
        // here and then the model is created.
        try {
            Parameters params = getParameters();
            String filename = params.getRaw().get(0);
            this.model = new LasersModel(filename);
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            System.exit(-1);
        }
        this.model.addObserver(this);
        this.model.makeBoard();

        TileType[] tileTypes = TileType.values();
        tilesHM = makeTilesHM(tileTypes);
    }

    /**
     * The initialization of all GUI component happens here.
     *
     * @param stage the stage to add UI components into
     */
    private void init(Stage stage) {
        BorderPane borderPane = new BorderPane();

        statusMessage = new Label( model.getSafeFilename() + " loaded");
        borderPane.setTop(statusMessage);
        BorderPane.setAlignment(statusMessage, Pos.TOP_CENTER);

        // get the grid pane from the helper method
        safeGridPane = makeSafeGridPane();
        borderPane.setCenter(safeGridPane);
        BorderPane.setAlignment(safeGridPane, Pos.CENTER);

        Button check = new Button("Check");
        check.setOnAction(event -> model.verify());

        Button solve = new Button("Solve");
        solve.setOnAction(event -> model.solve());

        Button restart = new Button("Restart");
        restart.setOnAction(event -> model.restart());

        Button load = new Button("Load");
        load.setOnAction(event -> load(stage));

        GridPane buttonGridPane = new GridPane();
        buttonGridPane.setAlignment(Pos.CENTER);
        buttonGridPane.add(check, 0, 0);
        buttonGridPane.add(solve, 1, 0);
        buttonGridPane.add(restart, 2, 0);
        buttonGridPane.add(load, 3, 0);
        borderPane.setBottom(buttonGridPane);
        BorderPane.setAlignment(buttonGridPane, Pos.BOTTOM_CENTER);

        Scene scene = new Scene(borderPane);

        stage.setTitle("Lasers GUI");
        stage.setResizable(false);
        stage.setScene(scene);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // TODO
        init(stage);  // do all your UI initialization here

        stage.show();
    }

    /**
     * Use to operate on View using Model.
     *
     * @param model the model in MVC architecture
     * @param card the card of the model
     */
    private void refresh(LasersModel model, ModelData data) {

        for (Node node : safeGridPane.getChildren()) {
            Tile tile = (Tile) node;

            int row = safeGridPane.getRowIndex(tile);
            int col = safeGridPane.getColumnIndex(tile);

            if (model.getBoard()[row][col].equals(tilesPTUI.get(3))) {
                tile.setImage(model.getBoard()[row][col]);
                setButtonBackground(tile, "yellow.png");
                tile.setOnAction(event ->
                        model.remove(row, col));
            }
            else {
                tile.setImage(model.getBoard()[row][col]);
                setButtonBackground(tile, "white.png");
                tile.setOnAction(event ->
                        model.add(row, col));
            }

            statusMessage.setText(data.getStatusMessage());

            if (row == data.getRow() && col == data.getCol()
                    && data.getStatus() == ModelData.Status.ERROR_VERIFYING) {
                if (model.getBoard()[row][col].equals(tilesPTUI.get(0))) {
                    tile.setImage("error");
                    tile.setOnAction(event ->
                            model.add(row, col));
                }
                else {
                    if (model.getBoard()[row][col].equals(tilesPTUI.get(3))) {
                        tile.setImage(model.getBoard()[row][col]);
                        tile.setOnAction(event ->
                                model.remove(row, col));
                    }
                    else {
                        tile.setImage(model.getBoard()[row][col]);
                        tile.setOnAction(event ->
                                model.add(row, col));
                    }

                    setButtonBackground(tile, "red.png");
                }
            }
        }
    }

    /**
     * The method that updates the View through the Model.
     */
    @Override
    public void update(LasersModel model, ModelData data) {
        if (data != null) {
            if ( Platform.isFxApplicationThread() ) {
                refresh(model, data);
            }
            else {
                Platform.runLater( () -> refresh(model, data));
            }
        }
    }
}
