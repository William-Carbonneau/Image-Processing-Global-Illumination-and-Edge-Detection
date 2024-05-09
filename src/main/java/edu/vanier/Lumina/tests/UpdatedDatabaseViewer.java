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
            FXMLControllerViewerDatabase controller = new FXMLControllerViewerDatabase();
            loader.setController(controller);
            SplitPane root = loader.load();
            Scene scene = new Scene(root, 800, 800);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Database Viewer");
            primaryStage.sizeToScene();
            primaryStage.setAlwaysOnTop(true);
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
