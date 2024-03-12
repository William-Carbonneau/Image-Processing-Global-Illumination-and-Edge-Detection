package edu.vanier.Global_Illumination_Image_Processing.tests;

import edu.vanier.global_illumination_image_processing.controllers.FXMLConvolutionController;
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
        loader.setController(new FXMLConvolutionController(primaryStage));
        Pane root = loader.load();
        //-- 2) Create and set the scene to the stage.
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        // We just need to bring the main window to front.
        primaryStage.setAlwaysOnTop(true);            
        primaryStage.show();
    }
}
