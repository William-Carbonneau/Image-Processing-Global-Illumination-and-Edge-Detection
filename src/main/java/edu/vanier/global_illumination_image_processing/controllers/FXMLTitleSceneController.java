/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.global_illumination_image_processing.controllers;

import edu.vanier.global_illumination_image_processing.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The title scene controller
 * 
 * This is the start and home page of the application. Enables the ability to switch between scenes.
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

    /**
     * Main constructor
     * 
     * @param primaryStage 
     */
    public FXMLTitleSceneController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Default constructor
     */
    public FXMLTitleSceneController() {
    }
    
    
    /**
     * Initialize the FXML controller and the buttons
     */
    @FXML
    public void initialize() {
        System.out.println("Title Scene being loaded");
        /**
         * Set the event listener for the button to switch to the convolutions scene
         */
        ImageProcessingBtn.setOnAction((event) -> {
            MainApp.switchScene(MainApp.FXMLConvolutionsScene, new FXMLConvolutionsSceneController(primaryStage));    
        });
        
        /**
         * Set the event listener for the button to switch to the render scene
         */
        GlobalIlluminationBtn.setOnAction((event) -> {
            MainApp.switchScene(MainApp.FXMLRenderScene, new FXMLRenderSceneController(primaryStage));  
        });
    }

}
