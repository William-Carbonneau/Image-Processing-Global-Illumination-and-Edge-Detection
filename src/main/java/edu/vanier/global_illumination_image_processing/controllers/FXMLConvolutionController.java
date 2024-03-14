/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.global_illumination_image_processing.controllers;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @Loovdrish Sujore
 * 
 */
public class FXMLConvolutionController {
    @FXML
    ImageView imageImgView;
    @FXML
    Button getFromFileBtn;
    Stage primaryStage;

    public FXMLConvolutionController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    @FXML
    public void initialize(){
        getFromFileBtn.setOnAction((event)->{
            System.out.println("Button file clicked");
            File file = getFileFromFChooser();
        });
    }
    
    public File getFileFromFChooser(){
        Stage stage = new Stage();
        FileChooser f = new FileChooser();
        stage.setAlwaysOnTop(true);
        this.primaryStage.setAlwaysOnTop(false);
        File file = f.showOpenDialog(stage);
        stage.close();
        return file;
    }
}
