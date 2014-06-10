package game2048;

import giocatoreAutomatico.GiocatoreAutomatico;
import giocatoreAutomatico.Griglia;
import giocatoreAutomatico.MyGiocatoreAutomatico;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;
import java.util.logging.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * 
 * @author bruno
 */
public class GameManager extends Group {

    private Logger log = Logger.getGlobal();

    private static final int FINAL_VALUE_TO_WIN = 2048;
    public static final int CELL_SIZE = 128;
    private static final int DEFAULT_GRID_SIZE = 4;
    private static final int BORDER_WIDTH = (14 + 2) / 2;
    // grid_width=4*cell_size + 2*cell_stroke/2d (14px css)+2*grid_stroke/2d (2 px css)
    private static final int GRID_WIDTH = CELL_SIZE * DEFAULT_GRID_SIZE + BORDER_WIDTH * 2;
    private static final int TOP_HEIGHT = 92;

    private volatile boolean movingTiles = false;
    private final int gridSize;
    private final List<Integer> traversalX;
    private final List<Integer> traversalY;
    private final List<Location> locations = new ArrayList<>();
    //private final Map<Location, Tile> gameGrid;
    private Map<Location, Tile> gameGrid = null;
    private final BooleanProperty gameWonProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty gameOverProperty = new SimpleBooleanProperty(false);
    private final IntegerProperty gameScoreProperty = new SimpleIntegerProperty(0);
    private final IntegerProperty gameMovePoints = new SimpleIntegerProperty(0);
    private final Set<Tile> mergedToBeRemoved = new HashSet<>();
    private final ParallelTransition parallelTransition = new ParallelTransition();
    private final BooleanProperty layerOnProperty = new SimpleBooleanProperty(false);

    // User Interface controls
    private final VBox vGame = new VBox(20);
    private final Group gridGroup = new Group();

    private final HBox hTop = new HBox(10);
    private final Label lblScore = new Label("0");
    private final Label lblPoints = new Label();
    private final HBox hOvrLabel = new HBox();
    private final HBox hOvrButton = new HBox();
    
    private int moveGap = 1000;
    private boolean ai = false;
    private boolean autoAI = false;
    private final CheckBox autoAiCheckBox = new CheckBox("Auto move");
    private final CheckBox aiCheckBox = new CheckBox("Need help?");
    private Game2048 game2048 = null;
    private MyGiocatoreAutomatico giocatoreAutomatico = null;
    private VBox controls = null;
    private HBox speedControls = null;
        
    public GameManager(Game2048 game) {
        this(DEFAULT_GRID_SIZE, game);
    }

    public GameManager(int gridSize, Game2048 game) {
        this.game2048 = game;
        this.gameGrid = new HashMap<>();
        this.gridSize = gridSize;
        this.traversalX = IntStream.range(0, gridSize).boxed().collect(Collectors.toList());
        this.traversalY = IntStream.range(0, gridSize).boxed().collect(Collectors.toList());

        createMenuBar();
        createScore();
        createGrid();
        initGameProperties();

        initializeGrid();

        this.setManaged(false);
    }
    
    public void move(Direction direction) {
        if (layerOnProperty.get()) {
            return;
        }

        synchronized (gameGrid) {
            if (movingTiles) {
                return;
            }
        }

        gameMovePoints.set(0);

        Collections.sort(traversalX, direction.getX() == 1 ? Collections.reverseOrder() : Integer::compareTo);
        Collections.sort(traversalY, direction.getY() == 1 ? Collections.reverseOrder() : Integer::compareTo);
        final int tilesWereMoved = traverseGrid((int x, int y) -> {
            Location thisloc = new Location(x, y);
            Tile tile = gameGrid.get(thisloc);
            if (tile == null) {
                return 0;
            }

            Location farthestLocation = findFarthestLocation(thisloc, direction); // farthest available location
            Location nextLocation = farthestLocation.offset(direction); // calculates to a possible merge
            Tile tileToBeMerged = nextLocation.isValidFor(gridSize) ? gameGrid.get(nextLocation) : null;

            if (tileToBeMerged != null && tileToBeMerged.getValue().equals(tile.getValue()) && !tileToBeMerged.isMerged()) {
                tileToBeMerged.merge(tile);

                gameGrid.put(nextLocation, tileToBeMerged);
                gameGrid.replace(tile.getLocation(), null);

                parallelTransition.getChildren().add(animateExistingTile(tile, tileToBeMerged.getLocation()));
                parallelTransition.getChildren().add(hideTileToBeMerged(tile));
                mergedToBeRemoved.add(tile);

                gameMovePoints.set(gameMovePoints.get() + tileToBeMerged.getValue());
                gameScoreProperty.set(gameScoreProperty.get() + tileToBeMerged.getValue());

                if (tileToBeMerged.getValue() == FINAL_VALUE_TO_WIN) {
                    gameWonProperty.set(true);
                }
                return 1;
            } else if (farthestLocation.equals(tile.getLocation()) == false) {
                parallelTransition.getChildren().add(animateExistingTile(tile, farthestLocation));

                gameGrid.put(farthestLocation, tile);
                gameGrid.replace(tile.getLocation(), null);

                tile.setLocation(farthestLocation);

                return 1;
            }

            return 0;
        });

        if (gameMovePoints.get() > 0) {
            animateScore(gameMovePoints.getValue().toString()).play();
        }

        parallelTransition.setOnFinished(e -> {
            synchronized (gameGrid) {
                movingTiles = false;
            }

            gridGroup.getChildren().removeAll(mergedToBeRemoved);

            // game is over if there is no more moves
            Location randomAvailableLocation = findRandomAvailableLocation();
            if (randomAvailableLocation == null && !mergeMovementsAvailable()) {
                gameOverProperty.set(true);
            } else if (randomAvailableLocation != null && tilesWereMoved > 0) {
                addAndAnimateRandomTile(randomAvailableLocation);
            }

            mergedToBeRemoved.clear();

            // reset merged after each movement
            gameGrid.values().stream().filter(Objects::nonNull).forEach(Tile::clearMerge);
        });

        synchronized (gameGrid) {
            movingTiles = true;
        }

        parallelTransition.play();
        parallelTransition.getChildren().clear();
    }
    
    public void autoMove() {
	    log.info("Doing an auto move.");
            Direction move = null;
            switch (giocatoreAutomatico.prossimaMossa(
                    newRandomTile.getLocation(),
                    newRandomTile.getValue())) {
                case 0: move = Direction.UP; break;
                case 1: move = Direction.RIGHT; break;
                case 2: move = Direction.DOWN; break;
                case 3: move = Direction.LEFT; break;
                default: 
                    throw new InvalidMoveException(
                            "Invalid move from the GiocatoreAutomatico");
            }
            move(move);
	    return;
    }
    
    public Griglia creaGriglia() {
        Griglia griglia = new MyGriglia();
        for (int i = 0; i < gridSize; i++ ) {
            for (int j = 0; j < gridSize; j++) {
                Location l = new Location(i, j);
                if (this.gameGrid.containsKey(l) && this.gameGrid.get(l) != null) {
                    griglia.put(l, this.gameGrid.get(l).getValue());
                }
                else {
                    griglia.put(l, -1);
                }
            }
        }
        log.log(Level.INFO, "Grid: \n{0}", griglia.toString());
        return griglia;
    }

    private Location findFarthestLocation(Location location, Direction direction) {
        Location farthest;

        do {
            farthest = location;
            location = farthest.offset(direction);
        } while (location.isValidFor(gridSize) && gameGrid.get(location) == null);

        return farthest;
    }

    private int traverseGrid(IntBinaryOperator func) {
        AtomicInteger at = new AtomicInteger();
        traversalX.forEach(t_x -> {
            traversalY.forEach(t_y -> {
                at.addAndGet(func.applyAsInt(t_x, t_y));
            });
        });

        return at.get();
    }

    private boolean mergeMovementsAvailable() {
        final SimpleBooleanProperty foundMergeableTile = new SimpleBooleanProperty(false);

        Stream.of(Direction.UP, Direction.LEFT).parallel().forEach(direction -> {
            int mergeableFound = traverseGrid((x, y) -> {
                Location thisloc = new Location(x, y);
                Tile tile = gameGrid.get(thisloc);

                if (tile != null) {
                    Location nextLocation = thisloc.offset(direction); // calculates to a possible merge
                    if (nextLocation.isValidFor(gridSize)) {
                        Tile tileToBeMerged = gameGrid.get(nextLocation);
                        if (tile.isMergeable(tileToBeMerged)) {
                            return 1;
                        }
                    }
                }

                return 0;
            });

            if (mergeableFound > 0) {
                foundMergeableTile.set(true);
            }
        });

        return foundMergeableTile.getValue();
    }
    
    private void createMenuBar() {
        
        ArrayList<Double> speedValues = new ArrayList<>();
        speedValues.add(0.5);
        speedValues.add(1.0);
        ChoiceBox<Double> gapCB = new ChoiceBox<>();
        gapCB.getItems().addAll(speedValues);
        gapCB.setValue(1.0);
        // interfaccia ChangeListener<Number>
        // metodo changed(ObservableValue ov, Number value, Number newValue)
        gapCB.getSelectionModel().selectedIndexProperty()
                .addListener((ov, v, nv) -> {
            moveGap = (int) (1000 * (Double) gapCB.getItems().get((Integer) nv));
            log.info("Move gap changed to " + moveGap);
        });
        
        menuBar.getMenus().addAll(gameMenu);
        menuBar.setLayoutX(0);
        menuBar.setLayoutY(0);
        
        speedControls = new HBox(5);
        Text speedControlsLabel = new Text("Time between moves (s):");
        speedControls.setAlignment(Pos.CENTER);
        speedControls.getChildren().addAll(speedControlsLabel, gapCB);
        autoAiCheckBox.setAllowIndeterminate(false);
        autoAiCheckBox.setOnAction((e) -> {
            toggleAutoAI();
        });
        
        aiCheckBox.setAllowIndeterminate(false);
        aiCheckBox.setOnAction((e) -> {
            toggleAI();
        });
        controls.setMaxWidth(240);
        controls.setMinWidth(240);
        controls.setMaxHeight(60);
        controls.setMinHeight(60);
        
        controls.getChildren().addAll(aiCheckBox);
        hTop.getChildren().add(controls);       
    }
    
    public void toggleAI() {
        if (!ai) {
            log.info("Creating bot.");
            giocatoreAutomatico = new MyGiocatoreAutomatico(creaGriglia());
            ai = true;
            aiCheckBox.setSelected(true);
            controls.getChildren().add(autoAiCheckBox);
        } else {
            log.info("Destroying bot.");
            giocatoreAutomatico = null;
            ai = false;
            aiCheckBox.setSelected(false);
            if (autoAI) {
                autoAiCheckBox.fire();
            }
            controls.getChildren().remove(autoAiCheckBox);
        }

    }
    
    public void toggleAutoAI() {
         if (autoAI) {
            autoAI = false;
            autoAiCheckBox.setSelected(false);
            controls.getChildren().remove(speedControls);
        } else {
            autoAI = true;
            autoAiCheckBox.setSelected(true);
            controls.getChildren().add(speedControls);
        }
    }
    
    public boolean isAutoAI() {
        return autoAI;
    }
    
    public boolean isAI() {
        return this.ai;
    } 
    
    public Thread aiThread() {
        return new Thread( () -> {
            log.info("Started AI thread.");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.info("AI thread manager interrupted.");
                System.exit(1);
            }
            while (true) {
                try {
                    Thread.sleep(moveGap);
                } catch (InterruptedException e) {
                    log.info("AI thread manager interrupted.");
                    System.exit(1);
                }
                if (ai && autoAI) {
                    Platform.runLater(() -> {
                        autoMove();
                    });
                }
            }
        });
    }
    
    private void createScore() {
        Label lblTitle = new Label("2048");
        lblTitle.getStyleClass().add("title");
        Label lblSubtitle = new Label("FX");
        lblSubtitle.getStyleClass().add("subtitle");
        HBox hFill = new HBox();
        HBox.setHgrow(hFill, Priority.ALWAYS);
        hFill.setAlignment(Pos.CENTER);
        HBox hFill2 = new HBox();
        HBox.setHgrow(hFill2, Priority.ALWAYS);
        hFill2.setAlignment(Pos.CENTER);
        VBox vScore = new VBox();
        vScore.setAlignment(Pos.CENTER);
        HBox hTitle = new HBox();
        hTitle.getChildren().addAll(lblTitle, lblSubtitle);
        VBox vTitle = new VBox();
        VBox vFill = new VBox();
        VBox.setVgrow(vFill, Priority.ALWAYS);
        vFill.setAlignment(Pos.CENTER);
        VBox vFill2 = new VBox();
        VBox.setVgrow(vFill2, Priority.ALWAYS);
        vFill2.setAlignment(Pos.CENTER);
        vTitle.getChildren().addAll(vFill, hTitle, vFill2);
        vScore.getStyleClass().add("vbox");
        Label lblTit = new Label("SCORE");
        lblTit.getStyleClass().add("titScore");
        lblScore.getStyleClass().add("score");
        lblScore.textProperty().bind(gameScoreProperty.asString());
        vScore.getChildren().addAll(lblTit, lblScore);
        lblTitle.setAlignment(Pos.CENTER);
        lblSubtitle.setAlignment(Pos.CENTER);

        hTop.getChildren().addAll(hFill2, vTitle, hFill, vScore);
        hTop.setMinSize(GRID_WIDTH, TOP_HEIGHT);
        hTop.setPrefSize(GRID_WIDTH, TOP_HEIGHT);
        hTop.setMaxSize(GRID_WIDTH, TOP_HEIGHT);

        vGame.getChildren().add(hTop);
        vGame.setAlignment(Pos.TOP_CENTER);
        //vGame.setLayoutX(-40); // sistemazione provvisoria
        //vGame.setLayoutY(-40); // idem
        getChildren().add(vGame);

        lblPoints.getStyleClass().add("points");

        getChildren().add(lblPoints);
    }

    private void createGrid() {
        final double arcSize = CELL_SIZE / 6d;

        IntStream.range(0, gridSize)
                .mapToObj(i -> IntStream.range(0, gridSize).mapToObj(j -> {
                    Location loc = new Location(i, j);
                    locations.add(loc);

                    Rectangle rect2 = new Rectangle(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                    rect2.setArcHeight(arcSize);
                    rect2.setArcWidth(arcSize);
                    rect2.getStyleClass().add("grid-cell");
                    return rect2;
                }))
                .flatMap(s -> s)
                .forEach(gridGroup.getChildren()::add);

        gridGroup.getStyleClass().add("grid");
        gridGroup.setManaged(false);
        gridGroup.setLayoutX(BORDER_WIDTH);
        gridGroup.setLayoutY(BORDER_WIDTH);

        HBox hBottom = new HBox();
        hBottom.getStyleClass().add("backGrid");
        hBottom.setMinSize(GRID_WIDTH, GRID_WIDTH);
        hBottom.setPrefSize(GRID_WIDTH, GRID_WIDTH);
        hBottom.setMaxSize(GRID_WIDTH, GRID_WIDTH);

        hBottom.getChildren().add(gridGroup);

        vGame.getChildren().add(hBottom);
    }

    private void initGameProperties() {
        gameOverProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (autoAI)
                    toggleAutoAI();
                if (ai)
                    toggleAI();
                layerOnProperty.set(true);
                hOvrLabel.getStyleClass().setAll("over");
                hOvrLabel.setMinSize(GRID_WIDTH, GRID_WIDTH);
                Label lblOver = new Label("Game over!");
                lblOver.getStyleClass().add("lblOver");
                hOvrLabel.setAlignment(Pos.CENTER);
                hOvrLabel.getChildren().setAll(lblOver);
                hOvrLabel.setTranslateY(TOP_HEIGHT + vGame.getSpacing());
                this.getChildren().add(hOvrLabel);

                hOvrButton.setMinSize(GRID_WIDTH, GRID_WIDTH / 2);
                Button bTry = new Button("Try again");
                bTry.getStyleClass().setAll("try");

                bTry.setOnTouchPressed(e -> resetGame());
                bTry.setOnAction(e -> resetGame());

                hOvrButton.setAlignment(Pos.CENTER);
                hOvrButton.getChildren().setAll(bTry);
                hOvrButton.setTranslateY(TOP_HEIGHT + vGame.getSpacing() + GRID_WIDTH / 2);
                this.getChildren().add(hOvrButton);
            }
        });

        gameWonProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (autoAI)
                    toggleAutoAI();
                if (ai)
                    toggleAI();
                layerOnProperty.set(true);
                hOvrLabel.getStyleClass().setAll("won");
                hOvrLabel.setMinSize(GRID_WIDTH, GRID_WIDTH);
                Label lblWin = new Label("You win!");
                lblWin.getStyleClass().add("lblWon");
                hOvrLabel.setAlignment(Pos.CENTER);
                hOvrLabel.getChildren().setAll(lblWin);
                hOvrLabel.setTranslateY(TOP_HEIGHT + vGame.getSpacing());
                this.getChildren().add(hOvrLabel);

                hOvrButton.setMinSize(GRID_WIDTH, GRID_WIDTH / 2);
                hOvrButton.setSpacing(10);
                Button bContinue = new Button("Keep going");
                bContinue.getStyleClass().add("try");
                bContinue.setOnAction(e -> {
                    layerOnProperty.set(false);
                    getChildren().removeAll(hOvrLabel, hOvrButton);
                });
                Button bTry = new Button("Try again");
                bTry.getStyleClass().add("try");
                bTry.setOnTouchPressed(e -> resetGame());
                bTry.setOnAction(e -> resetGame());
                hOvrButton.setAlignment(Pos.CENTER);
                hOvrButton.getChildren().setAll(bContinue, bTry);
                hOvrButton.setTranslateY(TOP_HEIGHT + vGame.getSpacing() + GRID_WIDTH / 2);
                this.getChildren().add(hOvrButton);
            }
        });
    }

    private void clearGame() {
        List<Node> collect = gridGroup.getChildren().filtered(c -> c instanceof Tile).stream().collect(Collectors.toList());
        gridGroup.getChildren().removeAll(collect);
        gameGrid.clear();
        getChildren().removeAll(hOvrLabel, hOvrButton);

        layerOnProperty.set(false);
        gameScoreProperty.set(0);
        gameWonProperty.set(false);
        gameOverProperty.set(false);

        initializeLocationsInGameGrid();
    }

    private void resetGame() {
        clearGame();
        initializeGrid();
    }

    /**
     * Clears the grid and redraws all tiles in the <code>gameGrid</code> object
     */
    private void redrawTilesInGameGrid() {
        gameGrid.values().stream().filter(Objects::nonNull).forEach(t -> {
            double layoutX = t.getLocation().getLayoutX(CELL_SIZE) - (t.getMinWidth() / 2);
            double layoutY = t.getLocation().getLayoutY(CELL_SIZE) - (t.getMinHeight() / 2);

            t.setLayoutX(layoutX);
            t.setLayoutY(layoutY);
            gridGroup.getChildren().add(t);
        });
    }

    private Timeline animateScore(String v1) {
        final Timeline timeline = new Timeline();
        lblPoints.setText("+" + v1);
        lblPoints.setOpacity(1);
        lblPoints.setLayoutX(400);
        lblPoints.setLayoutY(20);
        final KeyValue kvO = new KeyValue(lblPoints.opacityProperty(), 0);
        final KeyValue kvY = new KeyValue(lblPoints.layoutYProperty(), 100);

        Duration animationDuration = Duration.millis(600);
        final KeyFrame kfO = new KeyFrame(animationDuration, kvO);
        final KeyFrame kfY = new KeyFrame(animationDuration, kvY);

        timeline.getKeyFrames().add(kfO);
        timeline.getKeyFrames().add(kfY);

        return timeline;
    }

    interface AddTile {

        void add(int value, int x, int y);
    }

    /**
     * Initializes all cells in gameGrid map to null
     */
    private void initializeLocationsInGameGrid() {
        traverseGrid((x, y) -> {
            Location thisloc = new Location(x, y);
            gameGrid.put(thisloc, null);
            return 0;
        });
    }

    private void initializeGrid() {
        initializeLocationsInGameGrid();

        Tile tile0 = Tile.newRandomTile();
        newRandomTile = tile0; // pass new tile for the autoplayer
        List<Location> randomLocs = new ArrayList<>(locations);
        Collections.shuffle(randomLocs);
        Iterator<Location> locs = randomLocs.stream().limit(2).iterator();
        tile0.setLocation(locs.next());

        Tile tile1 = null;
        if (new Random().nextFloat() <= 0.8) { // gives 80% chance to add a second tile
            tile1 = Tile.newRandomTile();
            newRandomTile = tile1; // pass new tile for the autoplayer
            if (tile1.getValue() == 4 && tile0.getValue() == 4) {
                tile1 = Tile.newTile(2);
            }
            tile1.setLocation(locs.next());
        }

        Arrays.asList(tile0, tile1).forEach(t -> {
            if (t == null) {
                return;
            }
            gameGrid.put(t.getLocation(), t);
        });

        redrawTilesInGameGrid();
    }

    /**
     * Finds a random location or returns null if none exist
     *
     * @return a random location or <code>null</code> if there are no more
     * locations available
     */
    private Location findRandomAvailableLocation() {
        List<Location> availableLocations = locations.stream().filter(l -> gameGrid.get(l) == null).collect(Collectors.toList());

        if (availableLocations.isEmpty()) {
            return null;
        }

        Collections.shuffle(availableLocations);
        Location randomLocation = availableLocations.get(new Random().nextInt(availableLocations.size()));
        return randomLocation;
    }

    private void addAndAnimateRandomTile(Location randomLocation) {
        Tile tile = Tile.newRandomTile();
        tile.setLocation(randomLocation);
        log.log(Level.INFO, "Added random tile. {0}", tile.toString());
        newRandomTile = tile;

        double layoutX = tile.getLocation().getLayoutX(CELL_SIZE) - (tile.getMinWidth() / 2);
        double layoutY = tile.getLocation().getLayoutY(CELL_SIZE) - (tile.getMinHeight() / 2);

        tile.setLayoutX(layoutX);
        tile.setLayoutY(layoutY);
        tile.setScaleX(0);
        tile.setScaleY(0);

        gameGrid.put(tile.getLocation(), tile);
        gridGroup.getChildren().add(tile);

        animateNewlyAddedTile(tile).play();
    }

    private static final Duration ANIMATION_EXISTING_TILE = Duration.millis(125);

    private Timeline animateExistingTile(Tile tile, Location newLocation) {
        Timeline timeline = new Timeline();
        KeyValue kvX = new KeyValue(tile.layoutXProperty(), newLocation.getLayoutX(CELL_SIZE) - (tile.getMinHeight() / 2));
        KeyValue kvY = new KeyValue(tile.layoutYProperty(), newLocation.getLayoutY(CELL_SIZE) - (tile.getMinHeight() / 2));

        KeyFrame kfX = new KeyFrame(ANIMATION_EXISTING_TILE, kvX);
        KeyFrame kfY = new KeyFrame(ANIMATION_EXISTING_TILE, kvY);

        timeline.getKeyFrames().add(kfX);
        timeline.getKeyFrames().add(kfY);

        return timeline;
    }

    // after last movement on full grid, check if there are movements available
    private EventHandler<ActionEvent> onFinishNewlyAddedTile = e -> {
        if (this.gameGrid.values().parallelStream().noneMatch(Objects::isNull) && !mergeMovementsAvailable()) {
            this.gameOverProperty.set(true);
        }
    };

    private static final Duration ANIMATION_NEWLY_ADDED_TILE = Duration.millis(125);

    private Timeline animateNewlyAddedTile(Tile tile) {
        Timeline timeline = new Timeline();
        KeyValue kvX = new KeyValue(tile.scaleXProperty(), 1);
        KeyValue kvY = new KeyValue(tile.scaleYProperty(), 1);

        KeyFrame kfX = new KeyFrame(ANIMATION_NEWLY_ADDED_TILE, kvX);
        KeyFrame kfY = new KeyFrame(ANIMATION_NEWLY_ADDED_TILE, kvY);

        timeline.getKeyFrames().add(kfX);
        timeline.getKeyFrames().add(kfY);
        timeline.setOnFinished(onFinishNewlyAddedTile);
        return timeline;
    }

    private static final Duration ANIMATION_TILE_TO_BE_MERGED = Duration.millis(150);

    private Timeline hideTileToBeMerged(Tile tile) {
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(tile.opacityProperty(), 0);
        KeyFrame kf = new KeyFrame(ANIMATION_TILE_TO_BE_MERGED, kv);
        timeline.getKeyFrames().add(kf);
        return timeline;
    }

    public void saveSession() {
        SessionManager sessionManager = new SessionManager(DEFAULT_GRID_SIZE);
        sessionManager.saveSession(gameGrid, gameScoreProperty.getValue());
    }

    public void restoreSession() {
        SessionManager sessionManager = new SessionManager(DEFAULT_GRID_SIZE);

        clearGame();
        int score = sessionManager.restoreSession(gameGrid);
        if (score >= 0) {
            gameScoreProperty.set(score);
            redrawTilesInGameGrid();
        } else {
            // not session found, restart again
            resetGame();
        }
    }
}
