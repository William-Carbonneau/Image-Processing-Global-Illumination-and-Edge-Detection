package edu.vanier.Lumina.controllers;

import edu.vanier.Lumina.MainApp;
import static edu.vanier.Lumina.controllers.FXMLRenderSceneController.textFormatterIntegerRegex;
import edu.vanier.Lumina.models.Convolution;
import edu.vanier.Lumina.models.Database;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The image processing scene
 * used to perform image convolutions and manipulate the database
 * 
 * @Loovdrish Sujore
 */
public class FXMLConvolutionsSceneController {

    /**
     * Button to go back to the main menu
     */
    @FXML
    MenuItem menuItemBackToTitle;
    /**
     * The about menu item
     */
    @FXML
    MenuItem menuItemAboutConvolution;
    /**
     * Button to retrieve an image from the database
     */
    @FXML
    Button btnGetFromDatabase;
    /**
     * Button to save an image on the computer
     */
    @FXML
    Button btnSaveToFile;
    /**
     * Image view used to show the main active image
     */
    @FXML
    ImageView imageImgView;
    /**
     * Button to get an image from a file chooser
     */
    @FXML
    Button btnGetFromFile;
    /**
     * Button to start a convolution, to convolve an image
     */
    @FXML
    Button btnConvolve;
    /**
     * Choice box which contains the convolution options
     */
    @FXML
    ChoiceBox choiceBoxConvolution;
    /**
     * Textfield that enables the user to set a threshold value for certain
     * convolutions
     */
    @FXML
    TextField txtThreshold;
    /**
     * Textfield if the user wants to perform the same convolution multiple times in a row
     */
    @FXML
    TextField txtIterations;
    /**
     * Button to save the active image into the database, in the form of byte[]
     */
    @FXML
    Button btnSaveToDatabase;
    /**
     * The stackpane to center the imageview
     */
    @FXML
    StackPane stackImageHolder;
    /**
     * The ScrollPane to pan the StackPane
     */
    @FXML
    ScrollPane scrollImageHolder;
    /**
     * image zooming slider
     */
    @FXML 
    Slider sliderZoom;
    /**
     *  label displaying the default height of the loaded image
     */
    @FXML
    Label lblDefaultHeight;
    /**
     *  label displaying the default width of the loaded image
     */
    @FXML
    Label lblDefaultWidth;
    /**
     *  TextField to specify the height of the partial convolution
     */
    @FXML
    TextField txtPartialHeight;
    /**
     *  TextField to specify the width of the partial convolution
     */
    @FXML
    TextField txtPartialWidth;
    /**
     * bytes corresponding to the data of an image
     */
    byte[] b;
    /**
     * Temporary file, located in the convolutions folder of the project, which
     * is used to (1) contain the image being showed on the main image view, (2)
     * contain that image after a convolution has been performed, and (3) create
     * images when opening the database viewer.
     */
    File temp = new File("src\\main\\resources\\Images\\Convolutions\\temp.bmp");
    /**
     * FileOutputStream used throughout this controller to write byte[] into an
     * image. This is necessary whenever we use databases, since we are
     * representing these images in the form of bytes in the database,
     */
    FileOutputStream FOS;
    /**
     * When performing convolutions that require a threshold, if the user does
     * not specify one, a value of 100 will be attributed to that threshold.
     */
    float defaultThreshold = 100;
    /**
     * Threshold value when performing certain convolutions.
     */
    float threshold;
    /**
     * Kernel for the 3x3 Gaussian Convolution:
     * Taken from: https://youtu.be/C_zFhWdM4ic?si=CH3JvuO9mSfVmleJ (Pound, 2015)
     */
    float[][] rulesGaussian3x3 = {
        {1, 2, 1}, 
        {2, 4, 2}, 
        {1, 2, 1}};
    /**
     * Kernel for the 5x5 Gaussian Convolution:
     * Taken from: https://www.researchgate.net/figure/Discrete-approximation-of-the-Gaussian-kernels-3x3-5x5-7x7_fig2_325768087 (Shipitko)
     */
    float[][] rulesGaussian5x5 = {
        {1,  4,  7,  4, 1}, 
        {4, 16, 26, 16, 4}, 
        {7, 26, 41, 26, 7}, 
        {4, 16, 26, 16, 4}, 
        {1,  4,  7,  4, 1}};
    /**
     * Kernel for the 7x7 Gaussian Convolution:
     * Taken from: https://www.researchgate.net/figure/Discrete-approximation-of-the-Gaussian-kernels-3x3-5x5-7x7_fig2_325768087 (Shipitko)
     */
    float[][] rulesGaussian7x7 = {
        {0,  0,  1,   2,  1,  0, 0}, 
        {0,  3, 13,  22, 13,  3, 0}, 
        {1, 13, 59,  97, 59, 13, 1}, 
        {2, 22, 97, 159, 97, 22, 2}, 
        {1, 13, 59,  97, 59, 13, 1}, 
        {0,  3, 13,  22, 13,  3, 0}, 
        {0,  0,  1,   2,  1,  0, 0}};
    /**
     * Kernel for the Sharpening Algorithm:
     * Taken from: https://pro.arcgis.com/en/pro-app/latest/help/analysis/raster-functions/convolution-function.htm#:~:text=The%20Convolution%20function%20performs%20filtering,or%20other%20kernel%2Dbased%20enhancements. (Esri)
     */
    float[][] rulesSharp1 = {
        {-0.25f, -0.25f, -0.25f}, 
        {-0.25f,  3.00f, -0.25f}, 
        {-0.25f, -0.25f, -0.25f}};
    
    /**
     * main window being used to interact with the user. We will need to access it
     * when we need to open new windows that will take priority over the primary
     * one.
     */
    Stage primaryStage;
    /**
     * File corresponding to the one being showed on the main image view.
     */
    File imageBeingDisplayedOnIV;
    /**
     * Custom user kernel that will be initialized once the user specifies its dimensions.
     */
    float[][] kernelWithMoreDimensions;
    /**
     * default width used with the Zoom functionality
     */
    Double defaultWidth = 800.0;
    /**
     * default height used with the Zoom functionality
     */
    Double defaultHeight = 800.0;

    /**
     * Parameterized constructor
     *
     * @param primaryStage - The main window that is being used to show the main
     * image and interact with the user
     */
    public FXMLConvolutionsSceneController(Stage primaryStage) {
        this.primaryStage = primaryStage;

    }
    /**
     * This method verifies if a method is of type .bmp, jpg. or neither.
     * That is necessary because we are limiting the program's compatibility to only these 2 formats.
     * 
     * @param file - The input file
     * @return boolean corresponding to whether the file is of type .bmp (true) or .jpg (true), or neither (false)
     */
    public boolean verifyImageFormat(File file){
        String path = file.getAbsolutePath();
        String type = path.substring(path.length()-3, path.length());
        if(type.equals("bmp")||type.equals("jpg")){
            return true;
        }
        else{
            primaryStage.setAlwaysOnTop(false);
            showAlertWarning("The file is of type "+type+". Choose a file of type bmp or jpg");
            primaryStage.setAlwaysOnTop(true);
            return false;
        }
    }

    /**
     * initialization method of the FXML
     *
     * @throws IOException
     */
    @FXML
    public void initialize() throws IOException {
        //Need to erase the content in the temp file, if content is present.
        initializeTempFile();
        //When a user will use the Iterations function to specify the number of times he wants to convolve an image, the text formatter will make sure that only valid numerical input are written
        txtIterations.setTextFormatter(new TextFormatter <> (input -> input.getControlNewText().matches(textFormatterIntegerRegex) ? input : null));
        
        /**
         * Update the current partial Height - only integers allowed by textFormatter
         */
        txtPartialHeight.setOnKeyTyped((event) -> {
            int temp;
            try {
                temp = Integer.parseInt((txtPartialHeight.getText()));
            }catch (NumberFormatException e) {
                temp = Integer.MAX_VALUE;
            }
            Convolution.partialHeight = temp;
        });
        txtPartialHeight.setTextFormatter(new TextFormatter <> (input -> input.getControlNewText().matches(textFormatterIntegerRegex) ? input : null));
        /**
         * Update the current partial Width - only integers allowed by textFormatter
         */
        txtPartialWidth.setOnKeyTyped((event) -> {
            int temp;
            try {
                temp = Integer.parseInt((txtPartialWidth.getText()));
            }catch (NumberFormatException e) {
                temp = Integer.MAX_VALUE;
            }
            Convolution.partialWidth = temp;
        });
        txtPartialWidth.setTextFormatter(new TextFormatter <> (input -> input.getControlNewText().matches(textFormatterIntegerRegex) ? input : null));
        //Initialize the choices in the choice box
        choiceBoxConvolution.getItems().addAll("Custom Kernel", "Gaussian Blur 3x3", "Gaussian Blur 5x5", "Gaussian Blur 7x7", "Sharpening", "Grayscale", "Sobel X", "Sobel Y", "Sobel Classic","Sobel Colored", "Prewitt","Prewitt - Pure", "Laplacian3x3","Laplacian9x9", "Colored Edge Angles");
        
        // bind image holder to center it using double binding to get value of viewport dimensions as function of viewport modified
        stackImageHolder.minHeightProperty().bind(Bindings.createDoubleBinding(() -> 
        scrollImageHolder.getViewportBounds().getHeight(), scrollImageHolder.viewportBoundsProperty()));
        stackImageHolder.minWidthProperty().bind(Bindings.createDoubleBinding(() -> 
        scrollImageHolder.getViewportBounds().getWidth(), scrollImageHolder.viewportBoundsProperty()));

        //When someone clicks on the convolve button
        btnConvolve.setOnAction((event) -> {
            //To convolve an image, we need an image and a convolution choice
            //Convolution choice: Check if a choice has been made
            boolean convolutionIsSelected = false;
            String choice;
            //Set the variable to true if a selection has been made
            if (choiceBoxConvolution.getValue() != null) {
                choice = choiceBoxConvolution.getValue().toString();
                convolutionIsSelected = true;
            } //If not, show an alert to the user
            else {
                //Show message to user to choose a convolution
                primaryStage.setAlwaysOnTop(false);
                showAlertWarning("Please choose a convolution from the choice box");
                primaryStage.setAlwaysOnTop(true);
                return;
            }
            //Image: Check if an image has been selected. If selected, it will be displayed on the main image view.
            boolean imageIsSelected = false;
            Image image = imageImgView.getImage();
            if (image != null) {
                imageIsSelected = true;
            } //If not, show an alert to the user
            else {
                //Show message to user to choose an image
                primaryStage.setAlwaysOnTop(false);
                showAlertWarning("Please choose an image from the database or the file chooser");
                primaryStage.setAlwaysOnTop(true);
                return;
            }
            //If both conditions are accepted, then we can proceed with the convolution
            if (convolutionIsSelected == true && imageIsSelected == true) {
                //Verify the iterationsTxtField to see of the user wants to repeat a convolution multiple times in a row
                int iterations = 1;
                try{
                    //Get the integer from the textfield
                    iterations = Integer.parseInt(txtIterations.getText());
                    //If the number is equal to or smaller than 0, set to 1
                    if(iterations<=0) iterations=1;
                }
                catch(Exception notAnInt){
                    //If the number is invalid, simply set to 1
                    iterations=  1;
                }
                //Set the choice value from the choicebox
                choice = choiceBoxConvolution.getValue().toString();
                //Determine the value from the choice box and  perform the convolution using the temp file and the imageBeingDisplayedOnIV file
                //Then, after each convolution is completed, let the user know that the convolution has been completed with an alert
                if (choice.equals("Gaussian Blur 3x3")) {
                    // 3x3 gausian convolution
                    try {
                        //The for-loop is used to repeat the convolution as many times as the user has specified, given that the number specified is valid.
                        for(int i=0;i<iterations;i++)
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian3x3);
                        //Display the image on the Image View after it is completed
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                        
                    } catch (IOException | NullPointerException ex) {
                        //If the convolution did not work, inform the user with an alert
                        primaryStage.setAlwaysOnTop(false);
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                        primaryStage.setAlwaysOnTop(true);
                        return;
                    }
                } else if (choice.equals("Gaussian Blur 5x5")) {
                    // 5x5 gausian convolution
                    try {
                        //The for-loop is used to repeat the convolution as many times as the user has specified, given that the number specified is valid.
                        for(int i=0;i<iterations;i++)
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian5x5);
                        //Display the image on the Image View after it is completed
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                        
                    } catch (IOException | NullPointerException ex) {
                        //If the convolution did not work, inform the user with an alert
                        primaryStage.setAlwaysOnTop(false);
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                        primaryStage.setAlwaysOnTop(true);
                        return;
                    }
                } else if (choice.equals("Gaussian Blur 7x7")) {
                    // 7x7 gausian convolution
                    try {
                        //The for-loop is used to repeat the convolution as many times as the user has specified, given that the number specified is valid.
                        for(int i=0;i<iterations;i++)
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                        //Display the image on the Image View after it is completed
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                    } catch (IOException | NullPointerException ex) {
                        //If the convolution did not work, inform the user with an alert
                        primaryStage.setAlwaysOnTop(false);
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                        primaryStage.setAlwaysOnTop(true);
                        return;
                    }
                } else if (choice.equals("Sharpening")) {
                    // Sharpening convolution
                    try {
                        //The for-loop is used to repeat the convolution as many times as the user has specified, given that the number specified is valid.
                        for(int i=0;i<iterations;i++)
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesSharp1);
                        //Display the image on the Image View after it is completed
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                    } catch (IOException | NullPointerException ex) {
                        //If the convolution did not work, inform the user with an alert
                        primaryStage.setAlwaysOnTop(false);
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                        primaryStage.setAlwaysOnTop(true);
                        return;
                    }
                } else if (choice.equals("Grayscale")) {
                    // Grayscale convolution convolution
                    try {
                        //The for-loop is used to repeat the convolution as many times as the user has specified, given that the number specified is valid.
                        for(int i=0;i<iterations;i++)
                        Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        //Display the image on the Image View after it is completed
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                    } catch (IOException | NullPointerException ex) {
                        //If the convolution did not work, inform the user with an alert
                        primaryStage.setAlwaysOnTop(false);
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                        primaryStage.setAlwaysOnTop(true);
                        return;
                    }
                } else if (choice.equals("Sobel X")) {
                    // Sobel X component grayscaled convolution
                    //With Sobel we need to apply a gaussian filter and grayscale it first, then we can apply Sobel on the image.
                    try {
                        int tempHeight;
                        int tempWidth;
                        //Save value of partial hieght and width
                        tempHeight = Convolution.partialHeight;
                        tempWidth = Convolution.partialWidth;
                        //Change partial value to max to ignore partial convolution in preliminary steps
                        Convolution.partialHeight = Integer.MAX_VALUE;
                        Convolution.partialWidth = Integer.MAX_VALUE;
                        
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                        Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        //change partial height and width back to original values
                        Convolution.partialHeight = tempHeight;
                        Convolution.partialWidth = tempWidth;
                        //The for-loop is used to repeat the convolution as many times as the user has specified, given that the number specified is valid.
                        for(int i=0;i<iterations;i++)
                        Convolution.performSobelX(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        //Display the image on the Image View after it is completed
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                    } catch (IOException | NullPointerException ex) {
                        //If the convolution did not work, inform the user with an alert
                        primaryStage.setAlwaysOnTop(false);
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                        primaryStage.setAlwaysOnTop(true);
                        return;
                    }
                } else if (choice.equals("Sobel Y")) {
                    // Sobel Y component grayscaled convolution
                    //With Sobel we need to apply a gaussian filter and grayscale it first, then we can apply Sobel on the image.
                    try {
                        int tempHeight;
                        int tempWidth;
                        //Save value of partial hieght and width
                        tempHeight = Convolution.partialHeight;
                        tempWidth = Convolution.partialWidth;
                        //Change partial value to max to ignore partial convolution in preliminary steps
                        Convolution.partialHeight = Integer.MAX_VALUE;
                        Convolution.partialWidth = Integer.MAX_VALUE;
                        
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                        Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        //change partial height and width back to original values
                        Convolution.partialHeight = tempHeight;
                        Convolution.partialWidth = tempWidth;
                        //The for-loop is used to repeat the convolution as many times as the user has specified, given that the number specified is valid.
                        for(int i=0;i<iterations;i++)
                        Convolution.performSobelY(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        //Display the image on the Image View after it is completed
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                    } catch (IOException | NullPointerException ex) {
                        //If the convolution did not work, inform the user with an alert
                        primaryStage.setAlwaysOnTop(false);
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                        primaryStage.setAlwaysOnTop(true);
                        return;
                    }
                } else if (choice.equals("Sobel Classic")) {
                    // Sobel X-Y converged grayscaled convolution
                    //With Sobel we need to apply a gaussian filter and grayscale it first, then we can apply Sobel on the image.
                    try {
                        
                        int tempHeight;
                        int tempWidth;
                        //Save value of partial hieght and width
                        tempHeight = Convolution.partialHeight;
                        tempWidth = Convolution.partialWidth;
                        //Change partial value to max to ignore partial convolution in preliminary steps
                        Convolution.partialHeight = Integer.MAX_VALUE;
                        Convolution.partialWidth = Integer.MAX_VALUE;
                        
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                        Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        //change partial height and width back to original values
                        Convolution.partialHeight = tempHeight;
                        Convolution.partialWidth = tempWidth;
                        //The for-loop is used to repeat the convolution as many times as the user has specified, given that the number specified is valid.
                        for(int i=0;i<iterations;i++)
                        Convolution.performSobel(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        //Display the image on the Image View after it is completed
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                    } catch (IOException | NullPointerException ex) {
                        //If the convolution did not work, inform the user with an alert
                        primaryStage.setAlwaysOnTop(false);
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                        primaryStage.setAlwaysOnTop(true);
                        return;
                    }
                } else if (choice.equals("Prewitt")) {
                    // Prewitt convolution
                    //Try to get the threshold specified by the user. If the process does not work, simply set it to the default threshold
                    try {
                        float threshold = 100;
                        if (txtThreshold.getText() == null) {
                            threshold = defaultThreshold;
                        } else {
                            try {
                                threshold = Float.parseFloat(txtThreshold.getText());
                            } catch (Exception e) {
                                threshold = defaultThreshold;
                            }
                        }
                        int tempHeight;
                        int tempWidth;
                        //Save value of partial hieght and width
                        tempHeight = Convolution.partialHeight;
                        tempWidth = Convolution.partialWidth;
                        //Change partial value to max to ignore partial convolution in preliminary steps
                        Convolution.partialHeight = Integer.MAX_VALUE;
                        Convolution.partialWidth = Integer.MAX_VALUE;
                        
                        //Like Sobel, the image to be blurred and grayscaled first
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                        Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        //change partial height and width back to original values
                        Convolution.partialHeight = tempHeight;
                        Convolution.partialWidth = tempWidth;
                        //The for-loop is used to repeat the convolution as many times as the user has specified, given that the number specified is valid.
                        for(int i=0;i<iterations;i++) {
                            Convolution.performPrewitt(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), threshold);
                        }
                        //Display the image on the Image View after it is completed
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                    } catch (IOException | NullPointerException ex) {
                        //If the convolution did not work, inform the user with an alert
                        primaryStage.setAlwaysOnTop(false);
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                        primaryStage.setAlwaysOnTop(true);
                        return;
                    }
                    } else if (choice.equals("Prewitt - Pure")) {
                    // Prewitt convolution - pure no threshold
                    //The for-loop is used to repeat the convolution as many times as the user has specified, given that the number specified is valid.
                    try {
                        for(int i=0;i<iterations;i++) {
                            Convolution.performPrewittPure(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        }
                        //Display the image on the Image View after it is completed
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                    } catch (IOException | NullPointerException ex) {
                        //If the convolution did not work, inform the user with an alert
                        primaryStage.setAlwaysOnTop(false);
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                        primaryStage.setAlwaysOnTop(true);
                        return;
                    }
                } 
                    else if(choice.equals("Laplacian3x3")){
                        // Laplacian convolution - This is the 3x3 kernel version. It uses, like Sobel, a blur and a grayscale filter before being performed
                    try {
                        int tempHeight;
                        int tempWidth;
                        //Save value of partial hieght and width
                        tempHeight = Convolution.partialHeight;
                        tempWidth = Convolution.partialWidth;
                        //Change partial value to max to ignore partial convolution in preliminary steps
                        Convolution.partialHeight = Integer.MAX_VALUE;
                        Convolution.partialWidth = Integer.MAX_VALUE;
                        
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                        Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        //change partial height and width back to original values
                        Convolution.partialHeight = tempHeight;
                        Convolution.partialWidth = tempWidth;
                        //The for-loop is used to repeat the convolution as many times as the user has specified, given that the number specified is valid.
                        for(int i=0;i<iterations;i++)
                        Convolution.performLaplacianOperator3x3(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        //Display the image on the Image View after it is completed
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                    } catch (IOException | NullPointerException ex) {
                        //If the convolution did not work, inform the user with an alert
                        primaryStage.setAlwaysOnTop(false);
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                        primaryStage.setAlwaysOnTop(true);
                        return;
                    }
                        
                    }
                    else if (choice.equals("Laplacian9x9")) {
                    //Laplacian convolution - This is the 9x9 kernel version. It uses, like Sobel, a grayscale filter before being performed. However, it does not need a blurring filter first.
                    try {
                        int tempHeight;
                        int tempWidth;
                        //Save value of partial hieght and width
                        tempHeight = Convolution.partialHeight;
                        tempWidth = Convolution.partialWidth;
                        //Change partial value to max to ignore partial convolution in preliminary steps
                        Convolution.partialHeight = Integer.MAX_VALUE;
                        Convolution.partialWidth = Integer.MAX_VALUE;
                        
                        Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        //change partial height and width back to original values
                        Convolution.partialHeight = tempHeight;
                        Convolution.partialWidth = tempWidth;
                        //The for-loop is used to repeat the convolution as many times as the user has specified, given that the number specified is valid.
                        for(int i=0;i<iterations;i++)
                        Convolution.performLaplacianOperator9x9(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        //Display the image on the Image View after it is completed
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                    } catch (IOException | NullPointerException ex) {
                        //If the convolution did not work, inform the user with an alert
                        primaryStage.setAlwaysOnTop(false);
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                        primaryStage.setAlwaysOnTop(true);
                        return;
                    }
                } else if (choice.equals("Custom Kernel")) {
                    // Let the user create a custom kernel with the popup then convolve
                    primaryStage.setAlwaysOnTop(false);
                    Stage stage = new Stage();
                    FXMLLoader loaderCK = new FXMLLoader(getClass().getResource("/fxml/FXMLCustomKernel.fxml"));
                    //Controller class. Will initialize the kernel that will be used.
                    FXMLCustomKernelController controllerCK = new FXMLCustomKernelController(stage);
                    loaderCK.setController(controllerCK);
                    try {
                        BorderPane rootBP = loaderCK.load();
                        Scene scene = new Scene(rootBP);
                        stage.setScene(scene);
                        stage.setAlwaysOnTop(true);
                        stage.showAndWait();
                        //If the kernel is invalid, let the user know that the kernel has beeen dropped.
                        if(controllerCK.valid==false){ 
                            showAlertInfo("The kernel has been dropped.");
                            return;
                        }
                        //The controller has a kernel as attribute. We get it and we perform the convolution.
                        kernelWithMoreDimensions = controllerCK.getKernelFloat();
                        try {
                            //The for-loop is used to repeat the convolution as many times as the user has specified, given that the number specified is valid.
                            for(int i=0;i<iterations;i++)
                            Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), kernelWithMoreDimensions);
                            //Display the image on the Image View after it is completed
                            displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                            
                        } catch (IOException | NullPointerException ex) {
                            //If the convolution did not work, inform the user with an alert
                            primaryStage.setAlwaysOnTop(false);
                            showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                        }

                    } catch (IOException ex) {
                        Logger.getLogger("Could not load FXML");
                    }
                    primaryStage.setAlwaysOnTop(true);
                    return;

                } else if (choice.equals("Colored Edge Angles")) {
                    // Do sobel converged with grascale then color the edge angles
                    //With Sobel we need to apply a gaussian filter and grayscale it first, then we can apply Sobel on the image.
                    try {
                        int tempHeight;
                        int tempWidth;
                        //Save value of partial hieght and width
                        tempHeight = Convolution.partialHeight;
                        tempWidth = Convolution.partialWidth;
                        //Change partial value to max to ignore partial convolution in preliminary steps
                        Convolution.partialHeight = Integer.MAX_VALUE;
                        Convolution.partialWidth = Integer.MAX_VALUE;
                        
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                        Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        //change partial height and width back to original values
                        Convolution.partialHeight = tempHeight;
                        Convolution.partialWidth = tempWidth;
                        //The for-loop is used to repeat the convolution as many times as the user has specified, given that the number specified is valid.
                        for(int i=0;i<iterations;i++)
                        Convolution.performEdgeAngles(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        //Display the image on the Image View after it is completed
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                    } catch (IOException ex) {
                        //If the convolution did not work, inform the user with an alert
                        primaryStage.setAlwaysOnTop(false);
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                        primaryStage.setAlwaysOnTop(true);
                        return;
                    }
                }
                else if (choice.equals("Sobel Colored")){
                    // Sobel converged non-grayscale convolution
                    try {
                        int tempHeight;
                        int tempWidth;
                        //Save value of partial hieght and width
                        tempHeight = Convolution.partialHeight;
                        tempWidth = Convolution.partialWidth;
                        //Change partial value to max to ignore partial convolution in preliminary steps
                        Convolution.partialHeight = Integer.MAX_VALUE;
                        Convolution.partialWidth = Integer.MAX_VALUE;
                        
                        //With Sobel we need to apply a gaussian filter, then we can apply Sobel on the image. We do not grayscale it, because the sobel will be done for all red, green and blue values.
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                        //change partial height and width back to original values
                        Convolution.partialHeight = tempHeight;
                        Convolution.partialWidth = tempWidth;
                        //The for-loop is used to repeat the convolution as many times as the user has specified, given that the number specified is valid.
                        for(int i=0;i<iterations;i++)
                        Convolution.performSobelColored(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        //Display the image on the Image View after it is completed
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                    } catch (IOException | NullPointerException ex) {
                        //If the convolution did not work, inform the user with an alert
                        primaryStage.setAlwaysOnTop(false);
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                        primaryStage.setAlwaysOnTop(true);
                        return;
                    }
                    
                }else {
                    //This case will never happen, because everything is perfectly controlled. But in case it does, we simply do nothing.
                    return;
                }
                //After the convolution is completed, show to the user that the convolution is completed.
                primaryStage.setAlwaysOnTop(false);
                showAlertInfo("The " + choice + " convolution is completed");
                primaryStage.setAlwaysOnTop(true);
            } else {
                //This case should never happen, but in case it does, simply do nothing.
                return;
            }
            
        });
        /**
         * When the user clicks on the button to get from file chooser. A file
         * chooser will appear to let him choose an image from his computer
         */
        btnGetFromFile.setOnAction((event) -> {
            //File that will be manipulated by the user when performing the convolution
            temp = new File("src\\main\\resources\\Images\\Convolutions\\temp.bmp");
            //File chosen by the user through the file chooser.
            File chosen=null;
            //If a mistake occurs during the selection of the file, stop the process. No message shown, because it is very natural for users to simply close the file chooser
            boolean needToReturn = true;
            try{
                chosen = getFileFromFChooser();
                needToReturn =  false;
            }catch(Exception NoFileSelected){
                needToReturn =  true;
            }
            if(needToReturn==true) return;
            if(chosen==null)
                return;
            //If the file is in a format other than bmp or jpg, stop the process.
            if(verifyImageFormat(chosen)==false) 
                return;
            /*
            The idea is the get the file of the user, and to copy it inside of the temp file, so that no modifications be made to the original chosen file unless the user wants these modifications saved.
             */
            try {
                //Create file input stream to read the bytes from the file
                FileInputStream FIS = new FileInputStream(chosen);
                //Initialize the FileOutputStream so that it writes inside of the temp file
                FOS = new FileOutputStream(temp);
                FOS.write(FIS.readAllBytes());
            } catch (Exception e) {
                //If a mistake occurs, let the user know and drop the process.
                primaryStage.setAlwaysOnTop(false);
                showAlertWarning("The system could not get the file from the file chooser");
                primaryStage.setAlwaysOnTop(true);
                return;
            }
            //Now, the image that is being shown on the main image view is that of the temp file
            imageBeingDisplayedOnIV = temp;
            try {
                //Display the image on the main image view
                displayImage(imageBeingDisplayedOnIV.getAbsolutePath());
                
                
            } catch (Exception e) {
                //If a mistake occurs when trying to display that image, let the user know with an alert and drop the process.
                primaryStage.setAlwaysOnTop(false);
                showAlertWarning("The image has not been processed correctly. Please try again.");
                primaryStage.setAlwaysOnTop(true);
                return;
            }
        });
        
        // set default zoom to 1
        sliderZoom.setValue(1);
        /**
         * listener when the slider to zoom changes value - zoom the image
         */
        sliderZoom.valueProperty().addListener((observable, oldValue, NewValue) -> {
            double sliderValue = NewValue.doubleValue();
            imageImgView.fitWidthProperty().setValue(defaultWidth*sliderValue);
            imageImgView.fitHeightProperty().setValue(defaultHeight*sliderValue);
        });
        
        /**
         * If the user wants to get an image from the database, we need to open
         * a new stage which will display the images from the database. Since
         * the images are saved in the database in the form of bytes, a new
         * stage is required to represent them visually. Due to the complexity
         * of the procedure, having a new stage with its own FXMLController is
         * the best way to proceed.
         */
        btnGetFromDatabase.setOnAction((event) -> {
            try {
                //Set this primary stage off
                primaryStage.setAlwaysOnTop(false);
                //Create a window 
                Stage stage = new Stage();
                //Load the FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLUpdatedDatabaseViewer.fxml"));
                //Create the controller and set it
                FXMLDatabaseViewer databaseController = new FXMLDatabaseViewer(stage, temp,primaryStage);
                loader.setController(databaseController);
                SplitPane root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setAlwaysOnTop(true);
                //Show the window until it is closed
                stage.showAndWait();
                // The database returns the image chosen, clicked, by the user in the form of bytes
                //If no images have been slected, it returns the image that was shown on the main image view before the database viewer was opened
                byte[] tempImage = databaseController.getPassedImage();
                //The image should not be null
                if (tempImage != null) {
                    //Set the FOS to write in the temp file
                    FOS = new FileOutputStream(temp);
                    //Write the content of the chosen image
                    FOS.write(tempImage);
                    //Set the image of the image view
                    displayImage(temp.getAbsolutePath());
                    //Update the value of the imageBeingDisplayedOnIV which keeps track of the file that is being shown
                    imageBeingDisplayedOnIV = temp;
                    
                }
                //close the stage if it is not already done
                stage.close();
            } catch (Exception e) {
                //If a mistake occurs, let the user know with an alert and drop the process.
                primaryStage.setAlwaysOnTop(false);
                showAlertWarning("We could not load the file from the database. Please try again.");
                primaryStage.setAlwaysOnTop(true);
            }

        });
        /**
         * If the user wants to save the image being displayed on the image view
         * into the computer, we open a stage so that the user can pick a name
         * for the file, which was "temp", but will no longer be. Then a
         * directory chooser is used to choose a location in the computer for
         * the file. Finally, the image is saved.
         */
        btnSaveToFile.setOnAction((event) -> {
            //We need the image to be initialized
            if(imageImgView.getImage()==null){
                primaryStage.setAlwaysOnTop(false);
                showAlertWarning("Please choose an image before saving.");
                primaryStage.setAlwaysOnTop(true);
                return;
            }
            //To save the file, we need a directory and a name for the file
            String name = chooseNameFileDialog(primaryStage);
            //If no name have been written, do not do anything.
            if(name==null){
                return;
            }
            //Get the directory of the file
            DirectoryChooser dc;
            File file;
            try{
                dc = getDirectoryChooser(primaryStage);
                //Create the file at that directory
                file = new File(dc.getInitialDirectory().getAbsolutePath() + "//" + name + ".bmp");
            }catch(Exception NoLocationSelectedException){
                //If an error occurs, let the user know with an alert and drop the process.
                primaryStage.setAlwaysOnTop(false);
                showAlertWarning("No location has been selected to save the image.");
                primaryStage.setAlwaysOnTop(true);
                return;
            }
            //Create the file at the location with the name chosen by the user
            try {
                //Copy the data from the temporary file and write it in the new file
                FileInputStream FIS = new FileInputStream(imageBeingDisplayedOnIV.getAbsolutePath());
                FOS = new FileOutputStream(file);
                FOS.write(FIS.readAllBytes());
            } catch (FileNotFoundException ex) {
                //If no file is being displayed, let the user know with an alert and drop the process.
                primaryStage.setAlwaysOnTop(false);
                showAlertWarning("No file is being displayed. Please choose a file.");
                primaryStage.setAlwaysOnTop(true);
                return;
            } catch (IOException ex) {
                //If an error occurs, let the user know with an alert and drop the process.
                primaryStage.setAlwaysOnTop(false);
                showAlertWarning("Could not write in the file. Please try again.");
                primaryStage.setAlwaysOnTop(true);
                return;
            }
        });
        /**
         * If the user wants to save the image that is being displayed into the
         * database, we simply need to read the bytes of the image of the image
         * view and write them in the database, after having gotten the title
         * from the user.
         */
        btnSaveToDatabase.setOnAction((event) -> {
            //Make sure that the image is not null
            if (imageImgView.getImage() != null) {
                //Copy the file that is being displayed in temp
                Image imageToSave = imageImgView.getImage();
                temp = new File(imageToSave.getUrl());
                try {
                    //Insert the data in the database
                    Database.insertRow("Images", "ImagesConvolutions", chooseNameFileDialog(primaryStage), temp);
                    
                } catch (FileNotFoundException ex) {
                    //If the file is not found, let the user know with an alert and drop the process.
                    primaryStage.setAlwaysOnTop(false);
                    showAlertWarning("Could not write in the file because it was not found. Please try again.");
                    primaryStage.setAlwaysOnTop(true);
                    return;
                } catch (IOException ex) {
                    //If an IO Exception occurs, let the user know with an alert and drop the process.
                    primaryStage.setAlwaysOnTop(false);
                    showAlertWarning("An error occured. Please try again.");
                    primaryStage.setAlwaysOnTop(true);
                    return;
                } catch (SQLException ex) {
                    //If an error with the database occurs, let the user know with an alert and drop the process.
                    primaryStage.setAlwaysOnTop(false);
                    showAlertWarning("An error occured with our database. Please try again.");
                    primaryStage.setAlwaysOnTop(true);
                    return;
                }
            } //If the image is null, ask the user to choose an image.
            else {
                primaryStage.setAlwaysOnTop(false);
                showAlertWarning("Please choose an image before saving.");
                primaryStage.setAlwaysOnTop(true);
                return;
            }

        });

        /**
         * If the user wants to go back to the title menu, we simply have to
         * switch scenes.
         */
        menuItemBackToTitle.setOnAction((event) -> {
            MainApp.switchScene(MainApp.FXMLTitleScene, new FXMLTitleSceneController(primaryStage));

        });

        /**
         * The about modal popup
         */
        menuItemAboutConvolution.setOnAction((event) -> {
            openAboutDialog();
        });
    }
    /**
     * This method deletes everything that is in the temporary file. It is
     * crucial, because a user may run the program multiple times. Every time
     * the program closes, assuming the user has chosen some image to convolve,
     * temp will not be null, which can lead to problems in the logic of the
     * program.
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void initializeTempFile() throws FileNotFoundException, IOException {
        //Delete all the data
        FileOutputStream FOS = new FileOutputStream(temp);
        FOS.flush();
        FOS.close();
    }
    /**
     * This method opens a file chooser and returns the file chosen by the user
     * This is a method that Loovdrish has implemented last semester for the
     * project on wave simulation
     *
     * @return File file chosen by the user
     */
    public File getFileFromFChooser() {
        Stage stage = new Stage();
        FileChooser f = new FileChooser();
        stage.setAlwaysOnTop(true);
        this.primaryStage.setAlwaysOnTop(false);
        File file = f.showOpenDialog(stage);
        stage.close();
        return file;
    }

    /**
     * This method creates a dialog that is responsible of letting the user
     * choose a name for the file he wants to create. It returns the name of the
     * file. It creates a new stage which will be used as a window to contain
     * the text field used to write the name of the file. This is a method that
     * Loovdrish has implemented last semester for the project on wave
     * simulation
     *@param primaryStage- The primary stage of the application
     * @return nameFile, String which corresponds to the name of the file
     */
    public static String chooseNameFileDialog(Stage primaryStage){
        AtomicReference<String> nameFileOut = new AtomicReference<String>();
        Stage stage = new Stage();
        // set modal
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(primaryStage);
        VBox root = new VBox();
        Label nameLbl = new Label("Please write the name of your file");
        TextField nameTxtFld = new TextField("Name");
        nameTxtFld.setLayoutX(0);
        Button OkBtn = new Button("OK");
        OkBtn.setOnAction((event)->{
            nameFileOut.set(nameTxtFld.getText()); 
            stage.close();
        });
        // add the children
        root.getChildren().addAll(nameLbl, nameTxtFld, OkBtn);
        stage.setAlwaysOnTop(true);
        Scene scene = new Scene(root, 300, 300);
        stage.setScene(scene);
        stage.showAndWait();
        return nameFileOut.get();
    }

    /**
     * This method displays an image file onto the image view imageImgView
     *
     * @param filePath 
     * Source:
     * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/image/ImageView.html (Oracle, 2015)
     */
    public void displayImage(String filePath) {
        Image img = new Image(filePath);
        imageImgView.setImage(img);
        //Set the default width and height
        defaultWidth = img.getWidth();
        defaultHeight = img.getHeight();
        lblDefaultWidth.setText(Double.toString(img.getWidth()));
        lblDefaultHeight.setText(Double.toString(img.getHeight()));
        
        imageImgView.setFitWidth(defaultWidth*sliderZoom.getValue());
        imageImgView.setFitHeight(defaultHeight*sliderZoom.getValue());
    }
    /**
     * This method created a directory chooser for the user to select a location in his computer to save an image.
     * This is a method that Loovdrish had implemented last semester for the project on wave simulation.
     * Source to use directory chooser: https://docs.oracle.com/javase/8/javafx/api/javafx/stage/DirectoryChooser.html (Oracle, n.d.)
     * @param primaryStage
     * @return the directory chooser
     */
    public static DirectoryChooser getDirectoryChooser(Stage primaryStage){
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(primaryStage);
        DirectoryChooser dc = new DirectoryChooser();
        primaryStage.setAlwaysOnTop(false);
        stage.setAlwaysOnTop(true);
        dc.setInitialDirectory(dc.showDialog(stage));
        stage.setAlwaysOnTop(false);
        primaryStage.setAlwaysOnTop(true);
        return dc;
    }


    
    /**
     * This method displays an alert with a message
     * @param message - String - The message being shown to the user
     */
    public void showAlertWarning(String message) {
        primaryStage.setAlwaysOnTop(false);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Information Incorrect or Missing");
        alert.setContentText(message);
        alert.showAndWait();
        primaryStage.setAlwaysOnTop(true);
    }
    /**
     * This method opens the About window.
     */
    public void openAboutDialog(){
        try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConvolutionsAboutUpdated.fxml"));
                Pane root = loader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(primaryStage);
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setAlwaysOnTop(true);
                stage.setTitle("About Convolutions");
                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(FXMLRenderSceneController.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    /**
     * This method shows an alert with the information message shown to the user.
     * @param message - String - the message shown to the user
     * @return The alert - Returns itself
     */
    public static Alert showAlertInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(message);
        alert.showAndWait();
        return alert;
    }
    /**
     * Thsi method shows a confirmation alert to the user.
     * If the user returns true, then the user wants to continue. If not, stop all operations.
     * @param message - The message being shown to the user.
     * @return  Boolean  - Confirmation of the user.
     */
    public static boolean showAlertConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Information Incorrect or Missing");
        alert.setContentText(message);
        alert.showAndWait();
        System.out.println(alert.getResult());
        if(alert.getResult()==ButtonType.OK){
            return true;
        }
        else if(alert.getResult()==ButtonType.CANCEL){
            return false;
        }
        else return false;
    }
    
}
