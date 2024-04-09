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
import javafx.scene.layout.BorderPane;
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
    Button saveToDatabaseBtn;
    float defaultThreshold=100;
    // Source for the kernel to implement: https://youtu.be/C_zFhWdM4ic?si=CH3JvuO9mSfVmleJ
    float[][] rulesGaussian3x3 = {{1,2,1},{2,4,2},{1,2,1}};
    //Taken from https://www.researchgate.net/figure/Discrete-approximation-of-the-Gaussian-kernels-3x3-5x5-7x7_fig2_325768087
    float[][] rulesGaussian5x5 = {{1,4,7,4,1},{4,16,26,16,4},{7,26,41,26,7},{4,16,26,16,4},{1,4,7,4,1}};
    //Taken from https://www.researchgate.net/figure/Discrete-approximation-of-the-Gaussian-kernels-3x3-5x5-7x7_fig2_325768087
    float[][] rulesGaussian7x7 = {{0,0,1,2,1,0,0},{0,3,13,22,13,3,0},{1,13,59,97,59,13,1},{2,22,97,159,97,22,2},{1,13,59,97,59,13,1},{0,3,13,22,13,3,0},{0,0,1,2,1,0,0}};
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
    File imageBeingDisplayedOnIV;
    String nameFileOut;
    float threshold = 50;
    float[][] kernelWithMoreDimensions;

    public FXMLConvolutionsSceneController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
    }

    FXMLConvolutionsSceneController() {
        
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
        convolutionCB.getItems().addAll("Kernels With Different Dimensions", "Gaussian Blur 3x3","Gaussian Blur 5x5","Gaussian Blur 7x7", "Sharpening","Grayscale", "Sobel X", "Sobel Y", "Sobel Complete","Prewitt","Laplacian", "Colored Edge Angles");
        convolutionCB.setOnAction((event)->{
            //Get the value of the convolution
            String choice = convolutionCB.getValue().toString();
            
        });
        getFromFileBtn.setOnAction((event)->{
            System.out.println("Get from file clicked");
            File temp = new File("src\\main\\resources\\Images\\Convolutions\\temp.bmp");
            File chosen = getFileFromFChooser();
            try{
            FileInputStream FIS = new FileInputStream(chosen);
            FileOutputStream FOS = new FileOutputStream(temp);
            FOS.write(FIS.readAllBytes());
            }catch(Exception e){
                System.out.println("The system could not get the file from the file chooser");
            }
            imageBeingDisplayedOnIV = temp;
            try{
                displayImage(imageBeingDisplayedOnIV.getAbsolutePath());
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
                stage.setTitle("Database Viewer");
                stage.show();
                String titleDatabase;
                titleDatabase = "Images";
                String tableName ="ImagesConvolutions" ;
                //Get the elements from the db and display them
                getFromDBAndDisplay(titleDatabase, tableName, root);
            }catch(Exception e){
            }
            
        });
        SaveToFileBtn.setOnAction((event)->{
            //To save the file, we need a directory and a name for the file
            String name = chooseNameFileDialog();
            DirectoryChooser dc = getDirectoryChooser();
            File file = new File(dc.getInitialDirectory().getAbsolutePath()+"//"+name+".bmp");
            try {
                //Cipy the data from the temporary file
                FileInputStream FIS = new FileInputStream(imageBeingDisplayedOnIV.getAbsolutePath());
                FileOutputStream FOS = new FileOutputStream(file);
                FOS.write(FIS.readAllBytes());
            } catch (FileNotFoundException ex) {
                System.out.println("No file is being displayed");
            } catch (IOException ex) {
                System.out.println("Could not write in the file");
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
        convolveBtn.setOnAction((event)->{
            //To convolve an image, we need an image and a convolution choice
            //Convolution choice
            boolean convolutionIsSelected = false;
            String choice =convolutionCB.getValue().toString();
            if(convolutionCB.getValue()!=null){
                choice = convolutionCB.getValue().toString();
                System.out.println(choice+" selected");
                convolutionIsSelected = true;
            }
            else{
                System.out.println("No choice selected");
                //Show message to user to choose a convolution
            }
            //Image
            boolean imageIsSelected = false;
            Image image = imageImgView.getImage();
            if(image!=null){
                imageIsSelected = true;
                System.out.println("imageIsSelected: "+imageIsSelected);
            }
            else{
                System.out.println("No image selected");
                //Show message to user to choose an image
            }
            if(convolutionIsSelected==true&&imageIsSelected==true){
                System.out.println("Necessary conditions for convolution to be carried are respected");
                //If the user wants the custom kernel, we need to get the values from the textfields and initialize the rulesCustom 2D array
            
            if(choice.equals("Gaussian Blur 3x3")){
                System.out.println("Gaussian Blur 3x3 Clicked");
                try {
                    Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian3x3);
                    displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                } catch (IOException | NullPointerException ex) {
                    System.out.println("Error caught");
                }
            }
            else if(choice.equals("Gaussian Blur 5x5")){
                System.out.println("Gaussian Blur 5x5 Clicked");
                try {
                    Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian5x5);
                    displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                } catch (IOException | NullPointerException ex) {
                    System.out.println("Error caught");
                }
            }
            else if(choice.equals("Gaussian Blur 7x7")){
                System.out.println("Gaussian Blur 7x7 Clicked");
                try {
                    Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                    displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                } catch (IOException | NullPointerException ex) {
                    System.out.println("Error caught");
                }
            }
            else if(choice.equals("Sharpening")){
                try {
                    Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesSharp1);
                    displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                } catch (IOException | NullPointerException ex) {
                    System.out.println("Error caught");
                }
            }
            else if(choice.equals("Grayscale")){
                try {
                    Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                    displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                } catch (IOException | NullPointerException ex) {
                    System.out.println("Error caught");
                }
            }
            else if(choice.equals("Sobel X")){
                try {
                    Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                    Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                    Convolution.performSobelX(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                    displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                } catch (IOException | NullPointerException ex) {
                    System.out.println("Error caught");
                }
            }
            else if(choice.equals("Sobel Y")){
                try {
                    Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                    Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                    Convolution.performSobelX(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                    displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                } catch (IOException | NullPointerException ex) {
                    System.out.println("Error caught");
                }
            }
            else if(choice.equals("Sobel Complete")){
                try {
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
                    Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                    Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                    Convolution.performSobel(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), threshold);
                    displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                } catch (IOException | NullPointerException ex) {
                    System.out.println("Error caught");
                }
            }
            else if(choice.equals("Prewitt")){
                try {
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
                    Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                    Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                    Convolution.performPrewitt(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), threshold);
                    displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                } catch (IOException | NullPointerException ex) {
                    System.out.println("Error caught");
                }
            }
            else if(choice.equals("Laplacian")){
                try {
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
                    Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                    Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                    Convolution.performLaplacianOperator(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                    displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                } catch (IOException | NullPointerException ex) {
                    System.out.println("Error caught");
                }
            }
            else if(choice.equals("Kernels With Different Dimensions")){
                primaryStage.setAlwaysOnTop(false);
                System.out.println("Done1");
                Stage stage = new Stage();
                FXMLLoader loaderCK = new FXMLLoader(getClass().getResource("/fxml/FXMLCustomKernel.fxml"));
                System.out.println("Done2");
                FXMLCustomKernelController controllerCK = new FXMLCustomKernelController();
                loaderCK.setController(controllerCK);
                System.out.println("Done3");
                    try {
                        BorderPane rootBP = loaderCK.load();
                        Scene scene = new Scene(rootBP);
                        stage.setScene(scene);
                        stage.setAlwaysOnTop(true);
                        stage.showAndWait();
                        kernelWithMoreDimensions = controllerCK.getKernelFloat();
                        Convolution.print2DArray(kernelWithMoreDimensions);
                        try {
                            Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), kernelWithMoreDimensions);
                            displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                        } catch (IOException | NullPointerException ex) {
                            System.out.println("Error caught");
                        }
                        
                    } catch (IOException ex) {
                        Logger.getLogger("Could not load FXML");
                    }
                    primaryStage.setAlwaysOnTop(true);
                
            } 
            else if (choice.equals("Colored Edge Angles")){
                    try {
                        
                        Convolution.performConvolution(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath(), rulesGaussian7x7);
                        Convolution.performGrayscale(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        Convolution.performEdgeAngles(this.imageBeingDisplayedOnIV.getAbsolutePath(), this.imageBeingDisplayedOnIV.getAbsolutePath());
                        displayImage(this.imageBeingDisplayedOnIV.getAbsolutePath());
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLConvolutionsSceneController.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }
            else{
                System.out.println("Else");
            }
            }
            
        });
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
                        temp = new File("src\\main\\resources\\Images\\Convolutions\\temp.bmp");
                        FOS = new FileOutputStream(temp);
                        FOS.write(b);
                        imageBeingDisplayedOnIV = temp;
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
