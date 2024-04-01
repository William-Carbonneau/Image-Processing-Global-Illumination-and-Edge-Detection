package edu.vanier.global_illumination_image_processing.controllers;

import edu.vanier.global_illumination_image_processing.MainApp;
import edu.vanier.global_illumination_image_processing.models.Convolution;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
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
    TextField thresholdTxtBox;
    @FXML
    Button convolveAgainBtn;
    @FXML
    Button saveToDatabaseBtn;
    float defaultThreshold=100;
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
    File fileOut;

    public FXMLConvolutionsSceneController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
    }

    FXMLConvolutionsSceneController() {
        
    }
    public TextField[][] getCustomKernelTxtField(){
        TextField[][] txtRules = {{txt11,txt12,txt13},
                                          {txt21,txt22,txt23},
                                          {txt31,txt32,txt33}};
        return txtRules;
    }
    public float[][] getCustomKernelFloat(TextField[][] txtRules){
        float[][] rulesCustom = new float[3][3];
        for(int i=0; i<txtRules.length; i++){
                        for(int j=0; j<txtRules[0].length;j++){
                            try{
                                rulesCustom[i][j] = Float.valueOf(txtRules[i][j].getText());
                            }catch(Exception e){
                                rulesCustom[i][j] = 0f;
                            }
                        }
                    }
        return rulesCustom;
    }
    @FXML
    public void initialize(){
        convolutionCB.getItems().addAll("Custom Kernel", "Gaussian Blur", "Sharpening","Grayscale", "Sobel X", "Sobel Y", "Sobel Complete", "Reset");
        convolveAgainBtn.setOnAction((event)->{
            if(fileOut!=null){
                inputFile = fileOut;
                String choice = convolutionCB.getValue().toString();
                System.out.println(choice+" clicked");
                if(choice.equals("Custom Kernel")){
                    TextField[][] txtRules = getCustomKernelTxtField();
                    float[][] rulesCustom = getCustomKernelFloat(txtRules);
                    
                // Let the user choose a directory to create the image
                // Choose the directory in which the user wants to save the image
                Stage stage = new Stage();
                DirectoryChooser dc = new DirectoryChooser();
                primaryStage.setAlwaysOnTop(false);
                stage.setAlwaysOnTop(true);
                dc.setInitialDirectory(dc.showDialog(stage));
                stage.setAlwaysOnTop(false);
                primaryStage.setAlwaysOnTop(true);
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
                if(choice.equals("Gaussian Blur")){
                    // Let the user choose a directory to create the image
                    // Choose the directory in which the user wants to save the image
                    Stage stage = new Stage();
                    DirectoryChooser dc = new DirectoryChooser();
                    primaryStage.setAlwaysOnTop(false);
                    stage.setAlwaysOnTop(true);
                    dc.setInitialDirectory(dc.showDialog(stage));
                    stage.setAlwaysOnTop(false);
                    primaryStage.setAlwaysOnTop(true);
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
            }
            else{
                System.out.println("The output file is null");
            }
            
        });
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
                try {
                    dc.getInitialDirectory().createNewFile();
                    // Make a dialog appear for the user to choose a name for the file
                    String nameFileOut = chooseNameFileDialog();
                    //Get the threshold from the FXML
                    float threshold=100;
                    if(thresholdTxtBox.getText()==null){
                        threshold=defaultThreshold;
                    }
                    else{
                        try{
                        threshold = Float.parseFloat(thresholdTxtBox.getText());
                        }catch(Exception e){
                            threshold=defaultThreshold;
                        }
                    }
                    // Create a file with the name
                    fileOut = new File(dc.getInitialDirectory()+"\\"+nameFileOut+".bmp");
                    Convolution.performConvolution(this.inputFile.getAbsolutePath(), fileOut.getAbsolutePath(), rulesGaussian);
                    Convolution.performGrayscale(fileOut.getAbsolutePath(), fileOut.getAbsolutePath());
                    Convolution.performSobel(fileOut.getAbsolutePath(),fileOut.getAbsolutePath(), threshold);
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
            //Connect with the db
            //We are assuming that the db has already been created
            try{
                //Read all images from the database and display them in GUI
                //Set this primary stage off
                primaryStage.setAlwaysOnTop(false);
                //Create a window 
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLDatabaseConvolutions.fxml"));
                FlowPane root = loader.load();
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setAlwaysOnTop(true);
                stage.show();
                String titleDatabase;
                titleDatabase = "Images";
                String tableName ="ImagesConvolutions" ;
                //Get the elements from the db and display them
                getFromDBAndDisplay(titleDatabase, tableName, root);
            }catch(Exception e){
            }
            
        });
        saveToDatabaseBtn.setOnAction((event)->{
            System.out.println("Save to Database clicked");
            Connection connection = null;
            if(imageImgView.getImage()!=null){
                Image imageToSave = imageImgView.getImage();
                File temp = new File(imageToSave.getUrl());
                System.out.println("URL "+imageToSave.getUrl());
                try {
                    FileInputStream FIS = new FileInputStream(temp.getAbsolutePath());
                    byte[] b = FIS.readAllBytes();
                    connection = DriverManager.getConnection("jdbc:sqlite:Images.db");
                    PreparedStatement pstmt = connection.prepareStatement("INSERT INTO ImagesConvolutions(title, image) VALUES(?,?)");
                    String titleImage = chooseNameFileDialog();
                    pstmt.setString(1, titleImage);
                    pstmt.setBytes(2, b);
                    pstmt.execute();
                    FileOutputStream FOS = new FileOutputStream(temp.getAbsolutePath());
                    FOS.flush();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(FXMLConvolutionsSceneController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLConvolutionsSceneController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLConvolutionsSceneController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else{
                print("The image view is null");
            }
            
        });
        
        convolveBtn.setOnAction(convolutionCB.getOnAction());
        BackToTitleMenuItem.setOnAction((event)->{
            MainApp.switchScene(MainApp.FXMLTitleScene, new FXMLTitleSceneController(primaryStage));
            
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
    public static void print(String string){
        System.out.println(string);
    }
    private void getFromDBAndDisplay(String titleDatabase,String tableName,  FlowPane root) throws FileNotFoundException, IOException {
        Connection connection = null;
        try{
            connection  =DriverManager.getConnection("jdbc:sqlite:"+titleDatabase+".db");
            print("Connection established");
            Statement stmt = connection.createStatement();
            //Get the values from the table
            String getResultSetFromDB = "Select *from "+tableName;
            ResultSet rs = stmt.executeQuery(getResultSetFromDB);
            print("rs created");
            System.out.println("File temp created");
            FileOutputStream FOS;
            Image image;
            ImageView imageview;
            System.out.println(rs.getFetchSize());
            File temp;
            Label title;
            VBox imageAndTitle;
            while(rs.next()){
                imageAndTitle = new VBox();
                String titleImage = rs.getString("title");
                byte[] b = rs.getBytes("image");
                temp = new File("src\\main\\resources\\Images\\Convolutions\\"+titleImage+".bmp");
                FOS = new FileOutputStream(temp);
                FOS.write(b);
                System.out.println("FOS written");
                image  = new Image(temp.getAbsolutePath());
                imageview = new ImageView();
                imageview.setFitHeight(100);
                imageview.setFitWidth(100);
                imageview.setImage(image);
                title = new Label();
                title.setText(titleImage);
                imageAndTitle.getChildren().addAll(imageview,title);
                root.getChildren().add(imageAndTitle);
                FOS.flush();
            }
            String choiceOfImage = chooseNameFileDialog();
            //Retrive from the database
            try{
                ResultSet rs2 = stmt.executeQuery(getResultSetFromDB);
                while(rs2.next()){
                    if(rs2.getString("title").equals(choiceOfImage)){
                        byte[] b = rs2.getBytes("image");
                        System.out.println(b.length);
                        temp = new File("src\\main\\resources\\Images\\Convolutions\\"+choiceOfImage+".bmp");
                        FOS = new FileOutputStream(temp);
                        FOS.write(b);
                        imageImgView.setImage(new Image(temp.getAbsolutePath()));
                        
                    }
                }
                
            }catch(SQLException e){
                System.out.println("The image does not exist");
            }
            
        }catch(SQLException e){
            System.out.println("SQLException caught");
        }
        finally{
            try{
                connection.close();
            }catch(SQLException e){
                System.out.println("Could not close the connection");
            }
        }
    }
}
