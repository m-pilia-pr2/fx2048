/* 
 * This file is part of 2048FXAuto
 * Copyright (C) 2014 Martino Pilia <m.pilia@gmail.com>
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * This file incorporates work from the project 2048FX 
 * https://github.com/brunoborges/fx2048
 * covered by the following copyright and permission notice:
 * 
 *   Copyright (C) 2014 Bruno Borges <bruno.borges@oracle.com>
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */ 

package game2048;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author bruno.borges@oracle.com
 */
public class Game2048 extends Application {

    private final int MAIN_WINDOW_WIDTH = 600;
    private final int MAIN_WINDOW_HEIGHT = 720;
    
    private GameManager gameManager;
    private Bounds gameBounds;
    private Logger log;
    private Thread aiThread;
    private Stage primaryStage = null;

    @Override
    public void start(Stage primaryStage) {
	List<String> args = this.getParameters().getRaw();
	log = Logger.getGlobal();
	log.setLevel(Level.OFF);
	for (String s : args) {
		if (s.equals("-d")) {
			log.setLevel(Level.ALL);
		}
	}

        this.primaryStage = primaryStage;
	gameManager = new GameManager(this);
        gameBounds = gameManager.getLayoutBounds();
        
        aiThread = gameManager.aiThread(); // using normal Thread
        //aiThread = new Thread(gameManager.aiTask()); // using JavaFX Task
        aiThread.start();

        //StackPane root = new StackPane(gameManager);
        BorderPane root = new BorderPane(gameManager);
        root.setPrefSize(gameBounds.getWidth(), gameBounds.getHeight());
        ChangeListener<Number> resize = (ov, v, v1) -> {
            gameManager.setLayoutX((root.getWidth() - gameBounds.getWidth()) / 2d);
            gameManager.setLayoutY((root.getHeight() - gameBounds.getHeight()) / 2d);
        };
        root.widthProperty().addListener(resize);
        root.heightProperty().addListener(resize);
                
        MenuBar menuBar = gameManager.createMenuBar();
        root.setTop(menuBar);
        
        Scene scene = new Scene(root, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
        scene.getStylesheets().add("game2048/game.css");
        addKeyHandler(scene);
        addSwipeHandlers(scene);

        if (isARMDevice()) {
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("");
        }

        if (Platform.isSupported(ConditionalFeature.INPUT_TOUCH)) {
            scene.setCursor(Cursor.NONE);
        }
        
        primaryStage.setOnCloseRequest( (we) -> {
            log.info("attempt to close primaryStage");
            exitGame();
            we.consume();
        });

        primaryStage.setTitle("2048FX");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(gameBounds.getWidth());
        primaryStage.setMinHeight(gameBounds.getHeight());
        primaryStage.show();
    }

    private boolean isARMDevice() {
        return System.getProperty("os.arch").toUpperCase().contains("ARM");
    }

    private void addKeyHandler(Scene scene) {
        scene.setOnKeyPressed(ke -> {
            KeyCode keyCode = ke.getCode();
            if (keyCode.equals(KeyCode.S)) {
                gameManager.saveSession();
                return;
            }
            if (keyCode.equals(KeyCode.R)) {
                gameManager.restoreSession();
                return;
            }
            if (keyCode.equals(KeyCode.H)) {
                gameManager.toggleAI();
            }
            if (keyCode.equals(KeyCode.A)) {
                if (gameManager.isAI())
                    gameManager.toggleAutoAI();
            }
            if (keyCode.equals(KeyCode.ESCAPE)) {
                exitGame();
            }
            if (gameManager.isAI() && keyCode.equals(KeyCode.N)) {
                log.info("Doing an auto move on manual request.");
                gameManager.autoMove();
            }
            if (keyCode.isArrowKey() == false) { // only arrows after this!!!
                return;
            }
            if (!gameManager.isAI()) { // umano può giocare solo se non c'è auto
                Direction direction = Direction.valueFor(keyCode);
                gameManager.move(direction);
            }
        });
    }

    private void addSwipeHandlers(Scene scene) {
        scene.setOnSwipeUp(e -> gameManager.move(Direction.UP));
        scene.setOnSwipeRight(e -> gameManager.move(Direction.RIGHT));
        scene.setOnSwipeLeft(e -> gameManager.move(Direction.LEFT));
        scene.setOnSwipeDown(e -> gameManager.move(Direction.DOWN));
    }
        
    public void exitGame() {
            log.info("Attempting to exit");
            final int POPUP_WIDTH = 200;
            final int POPUP_HEIGHT = 80;
            boolean aiWasAuto = gameManager.isAutoAI();
            Group popupRoot = new Group();
            Stage popup = new Stage();
            popup.setTitle("Confirmation");
            popup.initOwner(primaryStage); // always on top over main window
            popup.initModality(Modality.WINDOW_MODAL); // intercepts all input
            popup.setOnCloseRequest((ev) -> { // impedisce la chiusura
                ev.consume();
            });
            Scene popupScene = new Scene(popupRoot, POPUP_WIDTH, POPUP_HEIGHT);
            popup.setScene(popupScene);
            Text confirmText = new Text("Do you really want to quit?");
            Button yes = new Button("Yes");
            yes.setOnAction((ev) -> {
                log.info("Exiting.");
                aiThread.interrupt();
                popup.close();
                Platform.exit();
            });
            yes.setOnKeyPressed((ke) -> {
                if (ke.getCode().equals(KeyCode.ENTER))
                    yes.fire();
            });
            Button no = new Button("No");
            no.setOnAction((ev) -> {
                log.info("Exiting aborted.");
                if (aiWasAuto)
                    gameManager.toggleAutoAI();
                popup.close();
            });
            no.setOnKeyPressed((ke) -> {
                if (ke.getCode().equals(KeyCode.ENTER))
                    no.fire();
            });
            no.setLayoutX(50);
            no.setLayoutY(50);
            HBox buttons = new HBox(10);
            buttons.setAlignment(Pos.CENTER);
            buttons.getChildren().addAll(yes, no);
            VBox content = new VBox(10);
            content.getChildren().addAll(confirmText, buttons);
            popupRoot.getChildren().addAll(content);
            
            if (aiWasAuto)
                gameManager.toggleAutoAI();
            popup.show();
            
            content.setLayoutX((POPUP_WIDTH - content.getWidth()) / 2);
            content.setLayoutY((POPUP_HEIGHT - content.getHeight()) / 2);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
