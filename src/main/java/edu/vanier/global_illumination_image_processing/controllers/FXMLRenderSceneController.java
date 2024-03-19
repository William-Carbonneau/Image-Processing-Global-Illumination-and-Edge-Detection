/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.global_illumination_image_processing.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Zachary Tremblay
 */
public class FXMLRenderSceneController {
    @FXML
    VBox RootVBox;
    @FXML
    MenuItem BackToTitleMenuItem;
    @FXML
    public void initialize(){
        
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
        
    }
    
}
