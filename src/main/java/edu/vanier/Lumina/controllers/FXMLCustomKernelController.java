package edu.vanier.Lumina.controllers;

import static edu.vanier.Lumina.controllers.FXMLConvolutionsSceneController.showAlertConfirmation;
import static edu.vanier.Lumina.controllers.FXMLRenderSceneController.textFormatterDoubleRegex;
import edu.vanier.Lumina.models.Convolution;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 *
 * @author Loovdrish Sujore
 * This class is the controller for the custom kernel. The window appears when the custom kernel option is selected.
 */
public class FXMLCustomKernelController {
    /**
     * choice box of the different dimensions of kernel available
     */
    @FXML
    ChoiceBox choiceBoxCK;
    /**
     * this button generates the textfields for the user to enter the values of the kernel
     */
    @FXML
    Button generateBtn;
    /**
     * this button takes the kernel that was created by the user and sets it to that of the current class object
     */
    @FXML
    Button setKernelBtn;
    /**
     * Root of the scene graph.
     */
    @FXML
    BorderPane BPane;
    /**
     * 2 dimensional textfield array representing the input kernel of the user.
     */
    TextField[][] kernel;
    /**
     * 2 dimensional float array representing the input kernel of the user, after it has been converted from Textfield (String).
     */
    float[][] kernelFloat;
    /**
     * Stage being used.
     */
    Stage stage;
    /**
     * Boolean value representing if the input array is valid or not.
     */
    boolean valid = false;
    /**
     * Getter of valid attribute.
     * @return valid - if the kernel is valid-> If we can proceed with the custom kernel convolution
     */
    public boolean isValid() {
        return valid;
    }
    /**
     * Getter that returns the custom kernel to be used by the FXMLConvolutionsSceneController to perform the convolution.
     * @return the custom kernel of the user in float numbers.
     */
    public float[][] getKernelFloat() {
        return kernelFloat;
    }
    /**
     * Constructor of this class with the stage it is using as a parameter.
     * @param stage 
     */
    public FXMLCustomKernelController(Stage stage) {
        this.stage = stage;
    }

    /**
     * Initialize method
     */
    @FXML
    public void initialize(){
        /**
         * Add the options of dimensions in the choicebox
         */
        choiceBoxCK.getItems().addAll("3x3","5x5","7x7","9x9");
        /**
         * Assuming a choice from the choicebox has been selected, we need to generate textfields in rows and columns, representing the kernel, so that the user can enter the values for each index
         */
        generateBtn.setOnAction((event)->{
            //Get the value from the choicebox
            //If it is null, do not do anything
            if(choiceBoxCK.getValue()==null) return;
            //If not, then get the dimensions
            String choice = choiceBoxCK.getValue().toString();
            //Create a geidpane in which the textfields are going to be arranged
            GridPane gd = new GridPane();
            //Get the dimensions of the rows and the columns, which is the first index of the choice string
            int dim = Integer.parseInt(choice.substring(0,1));
            //Set the dimensions of the kernel
            kernel = new TextField[dim][dim];
            //Create textfields in rows and columns to represent the kernel
            for(int i=0;i<kernel.length; i++){
                for(int j=0; j<kernel[0].length;j++){
                    kernel[i][j] = new TextField();
                    gd.add(kernel[i][j], i,j);
                    kernel[i][j].setTextFormatter(new TextFormatter <> (input -> input.getControlNewText().matches(textFormatterDoubleRegex) ? input : null));
                }
            }
            //add to the root node
            BPane.setCenter(gd);
            //Set the padding
            gd.setPadding(new Insets(10));
            //Set the alignment
            gd.setAlignment(Pos.CENTER);
            
        });
        /**
         * To set the kernel, we need to verify that it is valid and we need to convert the strings to floats with appropriate exception handling and confirmation from user.
         */
        setKernelBtn.setOnAction((event)->{
            //Set the dimensions of the kernelFloat
            kernelFloat = new float[kernel.length][kernel[0].length];
            //verify that all values have been initialized. The method verifyKernel also initializes them
            boolean kernelValid = verifyKernel();
            //If all the values have been initialized, we can close the window, and the kernel will be returned properly using the getter
            if(kernelValid){
                Convolution.print2DArray(kernelFloat);
                stage.close();
            }
            //If not, that means that some values have not yet been set (all other possibilities are being handled with the textformatter)
            //We can convert these values to 0, but we need confirmation from the user that this is what he wants to do
            else{
                stage.setAlwaysOnTop(false);
                valid = showAlertConfirmation("Your kernels contains values that have not been set. By default, they have been changed to 0.");
                stage.setAlwaysOnTop(true);
            }
        });
    }
    /**
     * This method takes the values from the textfield[][] and converts it to a 2 dimensional array of floats
     * It also verifies if the values are valid or not, and returns the answer as a boolean
     * @return 
     */
    private boolean verifyKernel() {
        //Set the result to true, by default
        valid = true;
        //float value of the kernel at the specified index
        float value;
        for(int i=0; i<kernel.length; i++){
            for(int j=0; j<kernel[0].length; j++){
                try{
                    //Convert String to float
                    value = Float.valueOf(kernel[i][j].getText());
                    kernelFloat[i][j] = value;
                }catch(Exception e){
                    //If an error occurs, it means that the value has not yet been specified
                    //We convert it to 0, and set the result boolean to false, meaning that a value has been set
                    kernelFloat[i][j] = 0;
                    valid = false;
                }
            }
        }
        return valid;
    }
}
