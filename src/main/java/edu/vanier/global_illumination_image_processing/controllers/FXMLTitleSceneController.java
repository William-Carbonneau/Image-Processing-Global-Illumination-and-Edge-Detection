/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.global_illumination_image_processing.controllers;

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
            try {
                System.out.println("Image Button clicked");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLConvolutionsScene.fxml"));
                loader.setController(new FXMLConvolutionsSceneController(primaryStage));
                Pane root = loader.load();
                RootVBox.getChildren().setAll(root);
            } catch (IOException ex) {
                Logger.getLogger(FXMLTitleSceneController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        });

        GlobalIlluminationBtn.setOnAction((event) -> {
            try {
                System.out.println("Illumination Button clicked");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLRenderScene.fxml"));
                loader.setController(new FXMLRenderSceneController());
                Pane root = loader.load();
                RootVBox.getChildren().setAll(root);
            } catch (IOException ex) {
                Logger.getLogger(FXMLTitleSceneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

}
