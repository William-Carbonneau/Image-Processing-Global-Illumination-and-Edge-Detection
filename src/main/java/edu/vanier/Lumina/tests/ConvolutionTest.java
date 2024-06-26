package edu.vanier.Lumina.tests;

import edu.vanier.Lumina.controllers.FXMLConvolutionsSceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


/**
 * Test Class for Convolution
 */
public class ConvolutionTest extends Application{
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLConvolutionsScene.fxml"));
        loader.setController(new FXMLConvolutionsSceneController(primaryStage));
        Pane root = loader.load();
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.setAlwaysOnTop(true);            
        primaryStage.show();
    }
}
