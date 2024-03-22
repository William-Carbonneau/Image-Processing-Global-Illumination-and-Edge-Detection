package edu.vanier.global_illumination_image_processing.controllers;

import edu.vanier.global_illumination_image_processing.rendering.Scene;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * The controller for the rendering scene
 * 
 * @author Zachary Tremblay and Willima Carbonneau
 */
public class FXMLRenderSceneController {
    @FXML VBox RootVBox;
    @FXML MenuItem BackToTitleMenuItem;
    @FXML ListView listObjectList;
    
    public void initialize(){
        // create the render scene
        Scene mainScene = new Scene();
        // create list of elements - needs a new class wrapper
        //ObservableList<ObjWrapper>
        
        BackToTitleMenuItem.setOnAction((event)->{
            try {
                System.out.println("Back to Title clicked");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLTitleScene.fxml"));
                loader.setController(new FXMLTitleSceneController());
                Pane root = loader.load();
                RootVBox.getChildren().setAll(root);
            } catch (IOException ex) {
                Logger.getLogger(FXMLConvolutionsSceneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
//        listObjectList.setOnMouseClicked();
        
    }
    
    class ObjWrapper {
    
    }
    
}
