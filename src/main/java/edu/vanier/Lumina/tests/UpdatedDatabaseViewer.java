package edu.vanier.Lumina.tests;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

/**
 * Test version of the database viewer, do not run alone
 * @author 2265724
 */
public class UpdatedDatabaseViewer extends Application{
    public static void main(String[] args) {
        launch(args);
    }
    

    @Override
    public void start(Stage primaryStage) throws Exception {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLUpdatedDatabaseViewer.fxml"));
            // Create the controller by calling the constructor of FXMLMainAppController
            // Since the game has just begun, the level is 1, and the initial score of the player is 0
            FXMLControllerViewerDatabase controller = new FXMLControllerViewerDatabase();
            // Set the controller of the loader
            loader.setController(controller);
            // Load the pane from the fxml
            SplitPane root = loader.load();
            //-- 2) Create and set the scene to the stage.
            Scene scene = new Scene(root, 800, 800);
            // Set the scene of the primary stage
            primaryStage.setScene(scene);
            // Set the title of the application
            primaryStage.setTitle("Database Viewer");
            primaryStage.sizeToScene();
            primaryStage.setAlwaysOnTop(true);
            // Show the primary stage
            primaryStage.show();
        }
        catch(Exception e){System.out.println("Error Caught");}
    }
}
class FXMLControllerViewerDatabase{
    @FXML ScrollPane ScrollPaneItems;
    @FXML TilePane tilePane;
    @FXML Button chooseBtn;
    @FXML Button deleteBtn;
    @FXML
    public void initialize(){
        chooseBtn.setOnAction((event)->{
            System.out.println("Take button clicked");
        });
        deleteBtn.setOnAction((event)->{
            System.out.println("Delete button clicked");
        });
    }
}
