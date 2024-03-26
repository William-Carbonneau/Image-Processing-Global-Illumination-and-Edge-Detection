/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.global_illumination_image_processing.controllers;

import edu.vanier.global_illumination_image_processing.MainApp;
import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Zachary Tremblay
 */
public class FXMLTitleSceneController {
    
    @FXML
    VBox RootVBox;
    @FXML
    Button ImageProcessingBtn;
    @FXML
    Button GlobalIlluminationBtn;
    
    Stage primaryStage;

    public FXMLTitleSceneController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public FXMLTitleSceneController() {
    }
    
    

    @FXML
    public void initialize() {
        System.out.println("Title Scene being loaded");
        ImageProcessingBtn.setOnAction((event) -> {
            MainApp.switchScene(MainApp.FXMLConvolutionsScene, new FXMLConvolutionsSceneController(primaryStage));
            
            
        });

        GlobalIlluminationBtn.setOnAction((event) -> {
            MainApp.switchScene(MainApp.FXMLRenderScene, new FXMLRenderSceneController(primaryStage));
            
        });
    }

}
