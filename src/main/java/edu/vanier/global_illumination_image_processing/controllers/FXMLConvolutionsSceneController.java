package edu.vanier.global_illumination_image_processing.controllers;

import edu.vanier.global_illumination_image_processing.MainApp;
import edu.vanier.global_illumination_image_processing.models.Convolution;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @Loovdrish Sujore
 */
public class FXMLConvolutionsSceneController {
    @FXML
    VBox RootVBox;
    @FXML
    MenuItem BackToTitleMenuItem;
    @FXML
    Button getFromDatabaseBtn;
    @FXML
    Button SaveToFileBtn;
    @FXML
    Button SaveToDatabaseBtn;
    @FXML
    ImageView imageImgView;
    @FXML
    Button getFromFileBtn;
    @FXML 
    Button convolveBtn;
    @FXML
    ChoiceBox convolutionCB;
    @FXML
    TextField txt11,txt12,txt13,txt21,txt22,txt23,txt31,txt32,txt33;
    // Source for the kernel to implement: https://youtu.be/C_zFhWdM4ic?si=CH3JvuO9mSfVmleJ
    float[][] rulesGaussian = {{1,2,1},{2,4,2},{1,2,1}};
    //Source for the kernel to implement: https://pro.arcgis.com/en/pro-app/latest/help/analysis/raster-functions/convolution-function.htm#:~:text=The%20Convolution%20function%20performs%20filtering,or%20other%20kernel%2Dbased%20enhancements.
    float[][] rulesSharp1 = {{0f,-0.25f,0f},{-0.25f,2f,-0.25f},{0f,-0.25f,0f}};
    //Source for the kernel: https://en.wikipedia.org/wiki/Sobel_operator
    float[][] rulesSobelY = {{-1,0,1},
                             {-2,0,2},
                             {-1,0,1}};
    //Source for the kernel: https://en.wikipedia.org/wiki/Sobel_operator
    float[][] rulesSobelX = {{-1,-2,-1},
                             {0,0,0},
                             {1,2,1}};
    Stage primaryStage;
    File inputFile;
    String nameFileOut;
    float threshold = 50;

    public FXMLConvolutionsSceneController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
    }

    FXMLConvolutionsSceneController() {
        
    }
    
    @FXML
    public void initialize(){
        convolutionCB.getItems().addAll("Custom Kernel", "Gaussian Blur", "Sharpening","Grayscale", "Sobel X", "Sobel Y", "Sobel Complete", "Reset");
        
        convolutionCB.setOnAction((event)->{
            //Get the value of the convolution
            String choice = convolutionCB.getValue().toString();
            //If the user wants the custom kernel, we need to get the values from the textfields and initialize the rulesCustom 2D array
            if(choice.equals("Custom Kernel")){
                System.out.println("Custom Kernel Clicked");
                float[][] rulesCustom = new float[3][3];
                TextField[][] txtRules = {{txt11,txt12,txt13},
                                          {txt21,txt22,txt23},
                                          {txt31,txt32,txt33}};
                for(int i=0; i<txtRules.length; i++){
                    for(int j=0; j<txtRules[0].length;j++){
                        try{
                            rulesCustom[i][j] = Float.valueOf(txtRules[i][j].getText());
                        }catch(Exception e){
                            rulesCustom[i][j] = 0f;
                        }
                        System.out.print(rulesCustom[i][j]+" ");
                    }
                    System.out.println();
                }
                //Get the image the user wants to convolve
                if(inputFile==null){
                    this.inputFile = getFileFromFChooser();
                }
                // Let the user choose a directory to create the image
                // Choose the directory in which the user wants to save the image
                Stage stage = new Stage();
                DirectoryChooser dc = new DirectoryChooser();
                primaryStage.setAlwaysOnTop(false);
                stage.setAlwaysOnTop(true);
                dc.setInitialDirectory(dc.showDialog(stage));
                stage.setAlwaysOnTop(false);
                primaryStage.setAlwaysOnTop(true);
                File fileOut;
                try {
                    dc.getInitialDirectory().createNewFile();
                    // Make a dialog appear for the user to choose a name for the file
                    String nameFileOut = chooseNameFileDialog();
                    // Create a file with the name
                    fileOut = new File(dc.getInitialDirectory()+"\\"+nameFileOut+".bmp");
                    Convolution.performConvolution(this.inputFile.getAbsolutePath(), fileOut.getAbsolutePath(), rulesCustom);
                    displayImage(fileOut.getAbsolutePath());
                } catch (IOException | NullPointerException ex) {
                    System.out.println("Error caught");
                }
                inputFile=null;
                
            }
            else if(choice.equals("Gaussian Blur")){
                System.out.println("Gaussian Blur Clicked");
                //Get the image the user wants to convolve
                if(inputFile==null){
                    this.inputFile = getFileFromFChooser();
                }
                // Let the user choose a directory to create the image
                // Choose the directory in which the user wants to save the image
                Stage stage = new Stage();
                DirectoryChooser dc = new DirectoryChooser();
                primaryStage.setAlwaysOnTop(false);
                stage.setAlwaysOnTop(true);
                dc.setInitialDirectory(dc.showDialog(stage));
                stage.setAlwaysOnTop(false);
                primaryStage.setAlwaysOnTop(true);
                File fileOut;
                try {
                    dc.getInitialDirectory().createNewFile();
                    // Make a dialog appear for the user to choose a name for the file
                    String nameFileOut = chooseNameFileDialog();
                    // Create a file with the name
                    fileOut = new File(dc.getInitialDirectory()+"\\"+nameFileOut+".bmp");
                    Convolution.performConvolution(this.inputFile.getAbsolutePath(), fileOut.getAbsolutePath(), rulesGaussian);
                    displayImage(fileOut.getAbsolutePath());
                } catch (IOException | NullPointerException ex) {
                    System.out.println("Error caught");
                }
                inputFile=null;
            }
            else if(choice.equals("Sharpening")){
                System.out.println("Sharpening Clicked");
                //Get the image the user wants to convolve
                if(inputFile==null){
                    this.inputFile = getFileFromFChooser();
                }
                // Let the user choose a directory to create the image
                // Choose the directory in which the user wants to save the image
                Stage stage = new Stage();
                DirectoryChooser dc = new DirectoryChooser();
                primaryStage.setAlwaysOnTop(false);
                stage.setAlwaysOnTop(true);
                dc.setInitialDirectory(dc.showDialog(stage));
                stage.setAlwaysOnTop(false);
                primaryStage.setAlwaysOnTop(true);
                File fileOut;
                try {
                    dc.getInitialDirectory().createNewFile();
                    // Make a dialog appear for the user to choose a name for the file
                    String nameFileOut = chooseNameFileDialog();
                    // Create a file with the name
                    fileOut = new File(dc.getInitialDirectory()+"\\"+nameFileOut+".bmp");
                    Convolution.performConvolution(this.inputFile.getAbsolutePath(), fileOut.getAbsolutePath(), rulesSharp1);
                    displayImage(fileOut.getAbsolutePath());
                } catch (IOException | NullPointerException ex) {
                    System.out.println("Error caught");
                }
                inputFile=null;
            }
            else if(choice.equals("Grayscale")){
                System.out.println("Sharpening Clicked");
                //Get the image the user wants to convolve
                if(inputFile==null){
                    this.inputFile = getFileFromFChooser();
                }
                // Let the user choose a directory to create the image
                // Choose the directory in which the user wants to save the image
                Stage stage = new Stage();
                DirectoryChooser dc = new DirectoryChooser();
                primaryStage.setAlwaysOnTop(false);
                stage.setAlwaysOnTop(true);
                dc.setInitialDirectory(dc.showDialog(stage));
                stage.setAlwaysOnTop(false);
                primaryStage.setAlwaysOnTop(true);
                File fileOut;
                try {
                    dc.getInitialDirectory().createNewFile();
                    // Make a dialog appear for the user to choose a name for the file
                    String nameFileOut = chooseNameFileDialog();
                    // Create a file with the name
                    fileOut = new File(dc.getInitialDirectory()+"\\"+nameFileOut+".bmp");
                    Convolution.performGrayscale(this.inputFile.getAbsolutePath(), fileOut.getAbsolutePath());
                    displayImage(fileOut.getAbsolutePath());
                } catch (IOException | NullPointerException ex) {
                    System.out.println("Error caught");
                }
                inputFile=null;
            }
            else if(choice.equals("Sobel X")){
                System.out.println("Sobel X clicked");
                //Get the image the user wants to convolve
                if(inputFile==null){
                    this.inputFile = getFileFromFChooser();
                }
                // Let the user choose a directory to create the image
                // Choose the directory in which the user wants to save the image
                Stage stage = new Stage();
                DirectoryChooser dc = new DirectoryChooser();
                primaryStage.setAlwaysOnTop(false);
                stage.setAlwaysOnTop(true);
                dc.setInitialDirectory(dc.showDialog(stage));
                stage.setAlwaysOnTop(false);
                primaryStage.setAlwaysOnTop(true);
                File fileOut;
                try {
                    dc.getInitialDirectory().createNewFile();
                    // Make a dialog appear for the user to choose a name for the file
                    String nameFileOut = chooseNameFileDialog();
                    // Create a file with the name
                    fileOut = new File(dc.getInitialDirectory()+"\\"+nameFileOut+".bmp");
                    Convolution.performConvolution(this.inputFile.getAbsolutePath(), fileOut.getAbsolutePath(), rulesGaussian);
                    Convolution.performGrayscale(fileOut.getAbsolutePath(), fileOut.getAbsolutePath());
                    Convolution.performSobelX(fileOut.getAbsolutePath(),fileOut.getAbsolutePath());
                    displayImage(fileOut.getAbsolutePath());
                } catch (IOException | NullPointerException ex) {
                    System.out.println("Error caught");
                }
                inputFile=null;
            }
            else if(choice.equals("Sobel Y")){
                System.out.println("Sobel Y clicked");
                //Get the image the user wants to convolve
                if(inputFile==null){
                    this.inputFile = getFileFromFChooser();
                }
                // Let the user choose a directory to create the image
                // Choose the directory in which the user wants to save the image
                Stage stage = new Stage();
                DirectoryChooser dc = new DirectoryChooser();
                primaryStage.setAlwaysOnTop(false);
                stage.setAlwaysOnTop(true);
                dc.setInitialDirectory(dc.showDialog(stage));
                stage.setAlwaysOnTop(false);
                primaryStage.setAlwaysOnTop(true);
                File fileOut;
                try {
                    dc.getInitialDirectory().createNewFile();
                    // Make a dialog appear for the user to choose a name for the file
                    String nameFileOut = chooseNameFileDialog();
                    // Create a file with the name
                    fileOut = new File(dc.getInitialDirectory()+"\\"+nameFileOut+".bmp");
                    Convolution.performConvolution(this.inputFile.getAbsolutePath(), fileOut.getAbsolutePath(), rulesGaussian);
                    Convolution.performGrayscale(fileOut.getAbsolutePath(), fileOut.getAbsolutePath());
                    Convolution.performSobelY(fileOut.getAbsolutePath(),fileOut.getAbsolutePath());
                    displayImage(fileOut.getAbsolutePath());
                } catch (IOException | NullPointerException ex) {
                    System.out.println("Error caught");
                }
                inputFile=null;
            }
            else if(choice.equals("Sobel Complete")){
                System.out.println("Sobel Complete clicked");
                //Get the image the user wants to convolve
                if(inputFile==null){
                    chooseFileDialog();
                    this.inputFile = getFileFromFChooser();
                }
                // Let the user choose a directory to create the image
                // Choose the directory in which the user wants to save the image
                chooseDirectoryDialog();
                DirectoryChooser dc = getDirectoryChooser();
                File fileOut;
                try {
                    dc.getInitialDirectory().createNewFile();
                    // Make a dialog appear for the user to choose a name for the file
                    String nameFileOut = chooseNameFileDialog();
                    // Create a file with the name
                    fileOut = new File(dc.getInitialDirectory()+"\\"+nameFileOut+".bmp");
                    Convolution.performConvolution(this.inputFile.getAbsolutePath(), fileOut.getAbsolutePath(), rulesGaussian);
                    Convolution.performGrayscale(fileOut.getAbsolutePath(), fileOut.getAbsolutePath());
                    Convolution.performSobel(fileOut.getAbsolutePath(),fileOut.getAbsolutePath());
                    displayImage(fileOut.getAbsolutePath());
                } catch (IOException | NullPointerException ex) {
                    System.out.println("Error caught");
                }
                inputFile=null;
            }
            else if(choice.equals("Reset")){
                System.out.println("Reset");
                inputFile=null;
            }
            else{
                System.out.println("Else");
            }
        });
        getFromFileBtn.setOnAction((event)->{
            System.out.println("Get from file clicked");
            inputFile = getFileFromFChooser();
            try{
                displayImage(inputFile.getAbsolutePath());
            }catch(Exception e){
                System.out.println("Error caught");
            }
        });
        getFromDatabaseBtn.setOnAction((event)->{
            System.out.println("Get from database clicked");
            
        });
        SaveToFileBtn.setOnAction((event)->{
            System.out.println("Save to File clicked");
            
        });
        SaveToDatabaseBtn.setOnAction((event)->{
            System.out.println("Save to Database clicked");
            
        });
        
        convolveBtn.setOnAction(convolutionCB.getOnAction());
        BackToTitleMenuItem.setOnAction((event)->{
            MainApp.switchScene(MainApp.FXMLTitleScene, new FXMLTitleSceneController());
            
        });
    }
    /**
     * This method opens a file chooser and returns the file chosen by the user
     * This is a method that Loovdrish has implemented last semester for the project on wave simulation
     * @return File file chosen by the user
     */
    public File getFileFromFChooser(){
        Stage stage = new Stage();
        FileChooser f = new FileChooser();
        stage.setAlwaysOnTop(true);
        this.primaryStage.setAlwaysOnTop(false);
        File file = f.showOpenDialog(stage);
        stage.close();
        return file;
    }

    /**
     * This method creates a dialog that is responsible of letting the user choose a name for the file he wants to create.
     * It returns the name of the file.
     * It creates a new stage which will be used as a window to contain the text field used to write the name of the file.
     * This is a method that Loovdrish has implemented last semester for the project on wave simulation
     * @return nameFile, String which corresponds to the name of the file
     */
    public String chooseNameFileDialog(){
        Stage stage = new Stage();
        VBox root = new VBox();
        Label nameLbl = new Label("Please write the name of your file");
        TextField nameTxtFld = new TextField("Name");
        nameTxtFld.setLayoutX(0);
        Button OkBtn = new Button("OK");
        OkBtn.setOnAction((event)->{
            nameFileOut = nameTxtFld.getText();
            stage.close();
        });
        root.getChildren().addAll(nameLbl,nameTxtFld, OkBtn);
        stage.setAlwaysOnTop(true);
        Scene scene = new Scene(root, 300, 300);
        stage.setScene(scene);
        stage.showAndWait();
        return nameFileOut;
    }
    /**
     * This method creates a dialog that is responsible of letting the user anticipate that a File Chooser is about to appear.
     * It creates a new stage which will be used as a window to contain the message.
     */
    public void chooseFileDialog(){
        Stage stage = new Stage();
        VBox root = new VBox();
        Label nameLbl = new Label("Please choose the file you want to convolve");
        TextField nameTxtFld = new TextField("Name");
        nameTxtFld.setLayoutX(0);
        Button GotItBtn = new Button("Got it!");
        GotItBtn.setOnAction((event)->{
            stage.close();
        });
        root.getChildren().addAll(nameLbl,GotItBtn);
        stage.setAlwaysOnTop(true);
        Scene scene = new Scene(root, 300, 200);
        stage.setScene(scene);
        stage.showAndWait();
    }
    /**
     * This method creates a dialog that is responsible of letting the user anticipate that a Directory Chooser is about to appear.
     * It creates a new stage which will be used as a window to contain the message.
     * This is a method that Loovdrish has implemented last semester for the project on wave simulation
     */
    public void chooseDirectoryDialog(){
        Stage stage = new Stage();
        VBox root = new VBox();
        Label nameLbl = new Label("Please choose a direcotry in which to save your output file");
        Button GotItBtn = new Button("Got it!");
        GotItBtn.setOnAction((event)->{
            stage.close();
        });
        root.getChildren().addAll(nameLbl,GotItBtn);
        stage.setAlwaysOnTop(true);
        Scene scene = new Scene(root, 300, 200);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * This method displays an image file onto the image view imageImgView
     * @param filePath 
     * Source: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/image/ImageView.html
     */
    public void displayImage(String filePath){
        imageImgView.setImage(new Image(filePath));
    }
    public DirectoryChooser getDirectoryChooser(){
        Stage stage = new Stage();
        DirectoryChooser dc = new DirectoryChooser();
        primaryStage.setAlwaysOnTop(false);
        stage.setAlwaysOnTop(true);
        dc.setInitialDirectory(dc.showDialog(stage));
        stage.setAlwaysOnTop(false);
        primaryStage.setAlwaysOnTop(true);
        return dc;
    }
}
