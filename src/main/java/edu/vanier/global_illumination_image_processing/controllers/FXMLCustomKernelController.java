package edu.vanier.global_illumination_image_processing.controllers;

import edu.vanier.global_illumination_image_processing.models.Convolution;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

/**
 *
 * @author Loovdrish Sujore
 */
public class FXMLCustomKernelController {
    @FXML
    ChoiceBox choiceBoxCK;
    @FXML
    Button generateBtn;
    @FXML
    Button getKernelBtn;
    @FXML
    BorderPane BPane;
    TextField[][] kernel;
    float[][] kernelFloat;
    @FXML
    public void initialize(){
        choiceBoxCK.getItems().addAll("1x1","3x3","5x5","7x7","9x9");
        generateBtn.setOnAction((event)->{
            String choice = choiceBoxCK.getValue().toString();
            GridPane gd = new GridPane();
            if(choice.equals("1x1")){
                kernel = new TextField[1][1];
                for(int i=0;i<kernel.length; i++){
                    for(int j=0; j<kernel[0].length;j++){
                        kernel[i][j] = new TextField();
                        gd.add(kernel[i][j], i,j);
                    }
                }
                BPane.setCenter(gd);
            }
            if(choice.equals("3x3")){
                kernel = new TextField[3][3];
                for(int i=0;i<kernel.length; i++){
                    for(int j=0; j<kernel[0].length;j++){
                        kernel[i][j] = new TextField();
                        gd.add(kernel[i][j], i,j);
                    }
                }
                BPane.setCenter(gd);
            }
            if(choice.equals("5x5")){
                kernel = new TextField[5][5];
                for(int i=0;i<kernel.length; i++){
                    for(int j=0; j<kernel[0].length;j++){
                        kernel[i][j] = new TextField();
                        gd.add(kernel[i][j], i,j);
                    }
                }
                BPane.setCenter(gd);
            }
            if(choice.equals("7x7")){
                kernel = new TextField[7][7];
                for(int i=0;i<kernel.length; i++){
                    for(int j=0; j<kernel[0].length;j++){
                        kernel[i][j] = new TextField();
                        gd.add(kernel[i][j], i,j);
                    }
                }
                BPane.setCenter(gd);
            }
            if(choice.equals("9x9")){
                kernel = new TextField[9][9];
                for(int i=0;i<kernel.length; i++){
                    for(int j=0; j<kernel[0].length;j++){
                        kernel[i][j] = new TextField();
                        gd.add(kernel[i][j], i,j);
                    }
                }
                BPane.setCenter(gd);
            }
            
        });
        getKernelBtn.setOnAction((event)->{
            kernelFloat = new float[kernel.length][kernel[0].length];
            //verify that all values have been initialized
            boolean kernelValid = verifyKernel();
            if(kernelValid)
                Convolution.print2DArray(kernelFloat);
            
        });
    }

    private boolean verifyKernel() {
        boolean valid = true;
        float value;
        for(int i=0; i<kernel.length; i++){
            for(int j=0; j<kernel[0].length; j++){
                try{
                    value = Float.valueOf(kernel[i][j].getText());
                    kernelFloat[i][j] = value;
                }catch(Exception e){
                    valid = false;
                }
            }
        }
        return valid;
    }
}
