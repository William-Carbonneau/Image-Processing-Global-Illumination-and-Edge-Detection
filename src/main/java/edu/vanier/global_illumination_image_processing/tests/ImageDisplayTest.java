package edu.vanier.Global_Illumination_Image_Processing.tests;

import edu.vanier.global_illumination_image_processing.controllers.FXMLMainAppController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 */
public class ImageDisplayTest extends Application{
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLImageSceneTest.fxml"));
            loader.setController(new FXMLImageTestController());
            BorderPane root = loader.load();
            Scene scene = new Scene(root, 1000, 700);
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();   
            primaryStage.show();
    }
}
class FXMLImageTestController{
    @FXML
    ImageView imgView;
    
    @FXML
    public void initialize() {
        imgView.setImage(new Image("C:\\Users\\shalini\\Downloads\\Github\\Image-Processing-Global-Illumination-and-Edge-Detection\\src\\main\\resources\\Images\\landscape2.bmp"));
    }
}
