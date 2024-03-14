package edu.vanier.Global_Illumination_Image_Processing.tests;

import edu.vanier.global_illumination_image_processing.controllers.FXMLConvolutionsSceneController;
import edu.vanier.global_illumination_image_processing.controllers.FXMLMainAppController;
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
