package edu.vanier.global_illumination_image_processing.controllers;

import edu.vanier.global_illumination_image_processing.MainApp;
import static edu.vanier.global_illumination_image_processing.controllers.FXMLRenderSceneController.textFormatterDoubleRegex;
import static edu.vanier.global_illumination_image_processing.controllers.FXMLRenderSceneController.textFormatterIntegerRegex;
import edu.vanier.global_illumination_image_processing.models.Convolution;
import edu.vanier.global_illumination_image_processing.models.Database;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @Loovdrish Sujore
 */
public class FXMLConvolutionsSceneController {

    /**
     * Button to go back to the main menu
     */
    @FXML
    MenuItem BackToTitleMenuItem;
    /**
     * The about menu item
     */
    @FXML
    MenuItem menuItemAboutConvolution;
    /**
     * Button to retrieve an image from the database
     */
    @FXML
    Button getFromDatabaseBtn;
    /**
     * Button to save an image on the computer
     */
    @FXML
    Button SaveToFileBtn;
    /**
     * Image view used to show the main active image
     */
    @FXML
    ImageView imageImgView;
    /**
     * Button to get an image from a file chooser
     */
    @FXML
    Button getFromFileBtn;
    /**
     * Button to start a convolution, to convolve an image
     */
    @FXML
    Button convolveBtn;
    /**
     * Choice box which contains the convolution options
     */
    @FXML
    ChoiceBox convolutionCB;
    /**
     * Textfield that enables the user to set a threshold value for certain
     * convolutions
     */
    @FXML
    TextField thresholdTxtBox;
    /**
     * Textfield if the user wants to perform the same convolution multiple times in a row
     */
    @FXML
    TextField iterationsTxtField;
    /**
     * Button to save the active image into the database, in the form of byte[]
     */
    @FXML
    Button saveToDatabaseBtn;
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
     * represinting these images in the form of bytes in the database,
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
    // Source for the kernel to implement: https://youtu.be/C_zFhWdM4ic?si=CH3JvuO9mSfVmleJ
    float[][] rulesGaussian3x3 = {{1, 2, 1}, {2, 4, 2}, {1, 2, 1}};
    //Taken from https://www.researchgate.net/figure/Discrete-approximation-of-the-Gaussian-kernels-3x3-5x5-7x7_fig2_325768087
    float[][] rulesGaussian5x5 = {{1, 4, 7, 4, 1}, {4, 16, 26, 16, 4}, {7, 26, 41, 26, 7}, {4, 16, 26, 16, 4}, {1, 4, 7, 4, 1}};
    //Taken from https://www.researchgate.net/figure/Discrete-approximation-of-the-Gaussian-kernels-3x3-5x5-7x7_fig2_325768087
    float[][] rulesGaussian7x7 = {{0, 0, 1, 2, 1, 0, 0}, {0, 3, 13, 22, 13, 3, 0}, {1, 13, 59, 97, 59, 13, 1}, {2, 22, 97, 159, 97, 22, 2}, {1, 13, 59, 97, 59, 13, 1}, {0, 3, 13, 22, 13, 3, 0}, {0, 0, 1, 2, 1, 0, 0}};
    //Source for the kernel to implement: https://pro.arcgis.com/en/pro-app/latest/help/analysis/raster-functions/convolution-function.htm#:~:text=The%20Convolution%20function%20performs%20filtering,or%20other%20kernel%2Dbased%20enhancements.
    float[][] rulesSharp1 = {{0f, -0.25f, 0f}, {-0.25f, 2f, -0.25f}, {0f, -0.25f, 0f}};
    //Source for the kernel: https://en.wikipedia.org/wiki/Sobel_operator
    float[][] rulesSobelY = {{-1, 0, 1},
    {-2, 0, 2},
    {-1, 0, 1}};
    //Source for the kernel: https://en.wikipedia.org/wiki/Sobel_operator
    float[][] rulesSobelX = {{-1, -2, -1},
    {0, 0, 0},
    {1, 2, 1}};
    /**
     * main window being used to interact with the user. We will need to access
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
     * Image views loaded from the database
     */
    ArrayList<ImageView> imvs = new ArrayList<>();
    /**
     * Titles of images loaded from the database.
     */
    ArrayList<String> titles = new ArrayList<>();
    /**
     * byte[] representing the data of each image loaded from the database
     */
    ArrayList<byte[]> bs = new ArrayList<>();
    /**
     * ImageView that is clicked by the user in the database viewer.
     */
    ImageView iv;

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
     * This method converts an array of textfields into one of float
     * corresponding to the values that are inserted in the textfields
     *
     * @param txtRules - The values of the kernel at each index
     * @return custom kernel [][] in the type of float[][]
     */
    public float[][] getCustomKernelFloat(TextField[][] txtRules) {
        float[][] rulesCustom = new float[3][3];
        for (int i = 0; i < txtRules.length; i++) {
            for (int j = 0; j < txtRules[0].length; j++) {
                try {
                    rulesCustom[i][j] = Float.valueOf(txtRules[i][j].getText());
                } catch (Exception e) {
                    rulesCustom[i][j] = 0f;
                }
            }
        }
        return rulesCustom;
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
        iterationsTxtField.setTextFormatter(new TextFormatter <> (input -> input.getControlNewText().matches(textFormatterIntegerRegex) ? input : null));
        //Initialize the choices in the choice box
        convolutionCB.getItems().addAll("Custom Kernel", "Gaussian Blur 3x3", "Gaussian Blur 5x5", "Gaussian Blur 7x7", "Sharpening", "Grayscale", "Sobel X", "Sobel Y", "Sobel Classic","Sobel Colored", "Prewitt", "Laplacian", "Colored Edge Angles");
        //When someone clicks on the convolve button
        convolveBtn.setOnAction((event) -> {
            //To convolve an image, we need an image and a convolution choice
            //Convolution choice: Check if a choice has been made
            boolean convolutionIsSelected = false;
            String choice;
            //Set the variable to true if a selection has been made
            if (convolutionCB.getValue() != null) {
                choice = convolutionCB.getValue().toString();
                System.out.println(choice + " selected");
                convolutionIsSelected = true;
            } //If not, show an alert to the user
            else {
                System.out.println("No choice selected");
                //Show message to user to choose a convolution
                showAlertWarning("Please choose a convolution from the choice box");
                return;
            }
            //Image: Check if a choice has been made
            boolean imageIsSelected = false;
            Image image = imageImgView.getImage();
            if (image != null) {
                imageIsSelected = true;
            } //If not, show an alert to the user
            else {
                //Show message to user to choose an image
                showAlertWarning("Please choose a convolution from the database or the file chooser");
                return;
            }
            //If both conditions are accepted, then we can proceed with the convolution
            if (convolutionIsSelected == true && imageIsSelected == true) {
                //Verify the iterationsTxtField to see of the user wants to repeat a convolution multiple times in a row
                int iterations = 1;
                try{
                    iterations = Integer.parseInt(iterationsTxtField.getText());
                    if(iterations<=0) iterations=1;
                }
                catch(Exception notAnInt){
                    iterations=  1;
                }
                //Set the choice value from the choicebox
                choice = convolutionCB.getValue().toString();
                //Determine the value from the choice box and  perform the convolution using the temp file and the imageBeingDisplayedOnIV file
                //Then, after each convolution is completed, let the user know that the convolution has been completed with an alert
                if (choice.equals("Gaussian Blur 3x3")) {
                    try {
                        for(int i=0;i<iterations;i++)
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian3x3);
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                        showAlertInfo("The " + choice + " convolution is completed");
                    } catch (IOException | NullPointerException ex) {
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                    }
                } else if (choice.equals("Gaussian Blur 5x5")) {
                    try {
                        for(int i=0;i<iterations;i++)
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian5x5);
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                        showAlertInfo("The " + choice + " convolution is completed");
                    } catch (IOException | NullPointerException ex) {
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                    }
                } else if (choice.equals("Gaussian Blur 7x7")) {
                    try {
                        for(int i=0;i<iterations;i++)
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                        showAlertInfo("The " + choice + " convolution is completed");
                    } catch (IOException | NullPointerException ex) {
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                    }
                } else if (choice.equals("Sharpening")) {
                    try {
                        for(int i=0;i<iterations;i++)
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesSharp1);
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                        showAlertInfo("The " + choice + " convolution is completed");
                    } catch (IOException | NullPointerException ex) {
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                    }
                } else if (choice.equals("Grayscale")) {
                    try {
                        for(int i=0;i<iterations;i++)
                        Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                        showAlertInfo("The " + choice + " convolution is completed");
                    } catch (IOException | NullPointerException ex) {
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                    }
                } else if (choice.equals("Sobel X")) {
                    try {
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                        Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        for(int i=0;i<iterations;i++)
                        Convolution.performSobelX(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                        showAlertInfo("The " + choice + " convolution is completed");
                    } catch (IOException | NullPointerException ex) {
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                    }
                } else if (choice.equals("Sobel Y")) {
                    try {
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                        Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        for(int i=0;i<iterations;i++)
                        Convolution.performSobelY(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                        showAlertInfo("The " + choice + " convolution is completed");
                    } catch (IOException | NullPointerException ex) {
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                    }
                } else if (choice.equals("Sobel Classic")) {
                    try {
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                        Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        for(int i=0;i<iterations;i++)
                        Convolution.performSobel(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                        showAlertInfo("The " + choice + " convolution is completed");
                    } catch (IOException | NullPointerException ex) {
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                    }
                } else if (choice.equals("Prewitt")) {
                    try {
                        float threshold = 100;
                        if (thresholdTxtBox.getText() == null) {
                            threshold = defaultThreshold;
                        } else {
                            try {
                                threshold = Float.parseFloat(thresholdTxtBox.getText());
                            } catch (Exception e) {
                                threshold = defaultThreshold;
                            }
                        }
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                        Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        for(int i=0;i<iterations;i++)
                        Convolution.performPrewitt(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), threshold);
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                        showAlertInfo("The " + choice + " convolution is completed");
                    } catch (IOException | NullPointerException ex) {
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                    }
                } else if (choice.equals("Laplacian")) {
                    try {
                        if (thresholdTxtBox.getText() == null) {
                            threshold = defaultThreshold;
                        } else {
                            try {
                                threshold = Float.parseFloat(thresholdTxtBox.getText());
                            } catch (Exception e) {
                                threshold = defaultThreshold;
                            }
                        }
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                        Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        for(int i=0;i<iterations;i++)
                        Convolution.performLaplacianOperator(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                        showAlertInfo("The " + choice + " convolution is completed");
                    } catch (IOException | NullPointerException ex) {
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                    }
                } else if (choice.equals("Custom Kernel")) {
                    primaryStage.setAlwaysOnTop(false);
                    Stage stage = new Stage();
                    FXMLLoader loaderCK = new FXMLLoader(getClass().getResource("/fxml/FXMLCustomKernel.fxml"));
                    FXMLCustomKernelController controllerCK = new FXMLCustomKernelController(stage);
                    loaderCK.setController(controllerCK);
                    try {
                        BorderPane rootBP = loaderCK.load();
                        Scene scene = new Scene(rootBP);
                        stage.setScene(scene);
                        stage.setAlwaysOnTop(true);
                        stage.showAndWait();
                        if(controllerCK.valid==false){ 
                            showAlertInfo("The kernel has been dropped.");
                            return;
                        }
                        kernelWithMoreDimensions = controllerCK.getKernelFloat();
                        try {
                            for(int i=0;i<iterations;i++)
                            Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), kernelWithMoreDimensions);
                            displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                            showAlertInfo("The " + choice + " convolution is completed");
                        } catch (IOException | NullPointerException ex) {
                            showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                        }

                    } catch (IOException ex) {
                        Logger.getLogger("Could not load FXML");
                    }
                    primaryStage.setAlwaysOnTop(true);

                } else if (choice.equals("Colored Edge Angles")) {
                    try {

                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                        Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        for(int i=0;i<iterations;i++)
                        Convolution.performEdgeAngles(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                        showAlertInfo("The " + choice + " convolution is completed");
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLConvolutionsSceneController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else if (choice.equals("Sobel Colored")){
                    try {
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                        for(int i=0;i<iterations;i++)
                        Convolution.performSobelColored(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                        showAlertInfo("The " + choice + " convolution is completed");
                    } catch (IOException | NullPointerException ex) {
                        showAlertWarning("The " + choice + " convolution did not work. Please try again.");
                    }
                    
                }else {
                    System.out.println("Else");
                }
            } else {
                return;
            }
        });
        /**
         * When the user clicks on the button to get from file chooser. A file
         * chooser will appear to let him choose an image from his computer
         */
        getFromFileBtn.setOnAction((event) -> {
            //File that will be manipulated by the user when performing the convolution
            temp = new File("src\\main\\resources\\Images\\Convolutions\\temp.bmp");
            //File chosen by the user through the file chooser.
            File chosen = getFileFromFChooser();
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
                showAlertWarning("The system could not get the file from the file chooser");
            }
            //Now, the image that is being shown on the main image view is that of the temp file
            imageBeingDisplayedOnIV = temp;
            try {
                //Display the image on the main image view
                displayImage(imageBeingDisplayedOnIV.getAbsolutePath());
            } catch (Exception e) {
                showAlertWarning("The image has not been processed correctly. Please try again.");
            }
        });
        /**
         * If the user wants to get an image from the database, we need to open
         * a new stage which will display the images from the database. Since
         * the images are saved in the database in the form of bytes, a new
         * stage is required to represent them visually. Due to the complexity
         * of the procedure, having a new stage with its own FXMLController is
         * the best way to proceed.
         */
        getFromDatabaseBtn.setOnAction((event) -> {
            try {
                //Set this primary stage off
                primaryStage.setAlwaysOnTop(false);
                //Create a window 
                Stage stage = new Stage();
                //Load the FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLUpdatedDatabaseViewer.fxml"));
                //Create the controller and set it
                FXMLDatabaseViewer databaseController = new FXMLDatabaseViewer(stage, temp);
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
                    imageImgView.setImage(new Image(temp.getAbsolutePath()));
                    //Update the value of the imageBeingDisplayedOnIV which keeps track of the file that is being shown
                    imageBeingDisplayedOnIV = temp;
                }
                //close the stage if it is not already done
                stage.close();
            } catch (Exception e) {
                showAlertWarning("We could not load the file from the database. Please try again.");
            }

        });
        /**
         * If the user wants to save the image being displayed on the image view
         * into the computer, we open a stage so that the user can pick a name
         * for the file, which was "temp", but will no longer be. Then a
         * directory chooser is used to choose a location in the computer for
         * the file. Finally, the image is saved.
         */
        SaveToFileBtn.setOnAction((event) -> {
            //To save the file, we need a directory and a name for the file
            String name = chooseNameFileDialog();
            DirectoryChooser dc = getDirectoryChooser(primaryStage);
            //Create the file at the location with the name chosen by the user
            File file = new File(dc.getInitialDirectory().getAbsolutePath() + "//" + name + ".bmp");
            try {
                //Copy the data from the temporary file and write it in the new file
                FileInputStream FIS = new FileInputStream(imageBeingDisplayedOnIV.getAbsolutePath());
                FOS = new FileOutputStream(file);
                FOS.write(FIS.readAllBytes());
            } catch (FileNotFoundException ex) {
                showAlertWarning("No file is being displayed. Please choose a file.");
            } catch (IOException ex) {
                showAlertWarning("Could not write in the file. Please try again.");
            }
        });
        /**
         * If the user wants to save the image that is being displayed into the
         * database, we simply need to read the bytes of the image of the image
         * view and write them in the database, after having gotten the title
         * from the user.
         */
        saveToDatabaseBtn.setOnAction((event) -> {
            //Make sure that the image is not null
            if (imageImgView.getImage() != null) {
                //Copy the file that is being displayed in temp
                Image imageToSave = imageImgView.getImage();
                temp = new File(imageToSave.getUrl());
                try {
                    //Insert the data in the database
                    Database.insertRow("Images", "ImagesConvolutions", chooseNameFileDialog(), temp);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(FXMLConvolutionsSceneController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLConvolutionsSceneController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLConvolutionsSceneController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } //If the image is null, ask the user to choose an image.
            else {
                showAlertWarning("Please choose an image before saving.");
            }

        });

        /**
         * If the user wants to go back to the title menu, we simply have to
         * switch scenes.
         */
        BackToTitleMenuItem.setOnAction((event) -> {
            MainApp.switchScene(MainApp.FXMLTitleScene, new FXMLTitleSceneController(primaryStage));

        });

        /**
         * The about modal popup
         */
        menuItemAboutConvolution.setOnAction((event) -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLConvolutionsAboutScene.fxml"));
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
        });
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
     *
     * @return nameFile, String which corresponds to the name of the file
     */
    public static String chooseNameFileDialog(){
        AtomicReference<String> nameFileOut = new AtomicReference<String>();
        Stage stage = new Stage();
        VBox root = new VBox();
        Label nameLbl = new Label("Please write the name of your file");
        TextField nameTxtFld = new TextField("Name");
        nameTxtFld.setLayoutX(0);
        Button OkBtn = new Button("OK");
        OkBtn.setOnAction((event)->{
            nameFileOut.set(nameTxtFld.getText()); 
            stage.close();
        });
        root.getChildren().addAll(nameLbl, nameTxtFld, OkBtn);
        stage.setAlwaysOnTop(true);
        Scene scene = new Scene(root, 300, 300);
        stage.setScene(scene);
        stage.showAndWait();
        return nameFileOut.get();
    }

    /**
     * This method creates a dialog that is responsible of letting the user
     * anticipate that a File Chooser is about to appear. It creates a new stage
     * which will be used as a window to contain the message.
     */
    public void chooseFileDialog() {
        Stage stage = new Stage();
        VBox root = new VBox();
        Label nameLbl = new Label("Please choose the file you want to convolve");
        TextField nameTxtFld = new TextField("Name");
        nameTxtFld.setLayoutX(0);
        Button GotItBtn = new Button("Got it!");
        GotItBtn.setOnAction((event) -> {
            stage.close();
        });
        root.getChildren().addAll(nameLbl, GotItBtn);
        stage.setAlwaysOnTop(true);
        Scene scene = new Scene(root, 300, 200);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * This method creates a dialog that is responsible of letting the user
     * anticipate that a Directory Chooser is about to appear. It creates a new
     * stage which will be used as a window to contain the message. This is a
     * method that Loovdrish has implemented last semester for the project on
     * wave simulation
     */
    public void chooseDirectoryDialog() {
        Stage stage = new Stage();
        VBox root = new VBox();
        Label nameLbl = new Label("Please choose a direcotry in which to save your output file");
        Button GotItBtn = new Button("Got it!");
        GotItBtn.setOnAction((event) -> {
            stage.close();
        });
        root.getChildren().addAll(nameLbl, GotItBtn);
        stage.setAlwaysOnTop(true);
        Scene scene = new Scene(root, 300, 200);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * This method displays an image file onto the image view imageImgView
     *
     * @param filePath Source:
     * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/image/ImageView.html
     */
    public void displayImage(String filePath) {
        imageImgView.setImage(new Image(filePath));
    }
    public static DirectoryChooser getDirectoryChooser(Stage primaryStage){
        Stage stage = new Stage();
        DirectoryChooser dc = new DirectoryChooser();
        primaryStage.setAlwaysOnTop(false);
        stage.setAlwaysOnTop(true);
        dc.setInitialDirectory(dc.showDialog(stage));
        stage.setAlwaysOnTop(false);
        primaryStage.setAlwaysOnTop(true);
        return dc;
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
        FileOutputStream FOS = new FileOutputStream(temp);
        FOS.flush();
        FOS.close();
    }

    public static void showAlertWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Information Incorrect or Missing");
        alert.setContentText(message);
        ButtonType helpBtn = new ButtonType("Help");
        alert.getButtonTypes().addAll(helpBtn);
        alert.showAndWait();
        if (alert.getResult() == helpBtn) {
            alert.close();
        }
    }

    public static void showAlertInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * If the user returns true, then the user wants to continue. If not, stop all operations.
     * @param message
     * @return 
     */
    public static boolean showAlertConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Information Incorrect or Missing");
        alert.setContentText(message);
        ButtonType helpBtn = new ButtonType("Help");
        alert.getButtonTypes().addAll(helpBtn);
        alert.showAndWait();
        System.out.println(alert.getResult());
        if (alert.getResult() == helpBtn) {
            alert.close();
            return false;
        }
        else if(alert.getResult()==ButtonType.OK){
            return true;
        }
        else if(alert.getResult()==ButtonType.CANCEL){
            return false;
        }
        else return false;
    }
}
