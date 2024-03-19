package edu.vanier.global_illumination_image_processing.controllers;

import java.awt.Color;
import java.awt.image.BufferedImage;
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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

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
    float[][] rulesGaussian = {{1,2,1},{2,4,2},{1,2,1}};
    //Source for the kernel to implement: https://pro.arcgis.com/en/pro-app/latest/help/analysis/raster-functions/convolution-function.htm#:~:text=The%20Convolution%20function%20performs%20filtering,or%20other%20kernel%2Dbased%20enhancements.
    float[][] rulesSharp1 = {{0f,-0.25f,0f},{-0.25f,2f,-0.25f},{0f,-0.25f,0f}};
    float[][] rulesSobelY = {{-1,0,1},
                             {-2,0,2},
                             {-1,0,1}};
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
                TextField[][] txtRules = {{txt11,txt12,txt13},{txt21,txt22,txt23},{txt31,txt32,txt33}};
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
                // Choose the directory in which the user wants to save the settings
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
                    String nameFileOut = chooseNameDialog();
                    // Create a file with the name
                    fileOut = new File(dc.getInitialDirectory()+"\\"+nameFileOut+".bmp");
                    performConvolution(this.inputFile.getAbsolutePath(), fileOut.getAbsolutePath(), rulesCustom);
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
                // Choose the directory in which the user wants to save the settings
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
                    String nameFileOut = chooseNameDialog();
                    // Create a file with the name
                    fileOut = new File(dc.getInitialDirectory()+"\\"+nameFileOut+".bmp");
                    performConvolution(this.inputFile.getAbsolutePath(), fileOut.getAbsolutePath(), rulesGaussian);
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
                // Choose the directory in which the user wants to save the settings
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
                    String nameFileOut = chooseNameDialog();
                    // Create a file with the name
                    fileOut = new File(dc.getInitialDirectory()+"\\"+nameFileOut+".bmp");
                    performConvolution(this.inputFile.getAbsolutePath(), fileOut.getAbsolutePath(), rulesSharp1);
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
                // Choose the directory in which the user wants to save the settings
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
                    String nameFileOut = chooseNameDialog();
                    // Create a file with the name
                    fileOut = new File(dc.getInitialDirectory()+"\\"+nameFileOut+".bmp");
                    performGrayscale(this.inputFile.getAbsolutePath(), fileOut.getAbsolutePath());
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
                // Choose the directory in which the user wants to save the settings
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
                    String nameFileOut = chooseNameDialog();
                    // Create a file with the name
                    fileOut = new File(dc.getInitialDirectory()+"\\"+nameFileOut+".bmp");
                    performConvolution(this.inputFile.getAbsolutePath(), fileOut.getAbsolutePath(), rulesGaussian);
                    performGrayscale(fileOut.getAbsolutePath(), fileOut.getAbsolutePath());
                    performSobelX(fileOut.getAbsolutePath(),fileOut.getAbsolutePath());
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
                // Choose the directory in which the user wants to save the settings
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
                    String nameFileOut = chooseNameDialog();
                    // Create a file with the name
                    fileOut = new File(dc.getInitialDirectory()+"\\"+nameFileOut+".bmp");
                    performConvolution(this.inputFile.getAbsolutePath(), fileOut.getAbsolutePath(), rulesGaussian);
                    performGrayscale(fileOut.getAbsolutePath(), fileOut.getAbsolutePath());
                    performSobelY(fileOut.getAbsolutePath(),fileOut.getAbsolutePath());
                } catch (IOException | NullPointerException ex) {
                    System.out.println("Error caught");
                }
                inputFile=null;
            }
            else if(choice.equals("Sobel Complete")){
                                System.out.println("Sobel Complete clicked");
                //Get the image the user wants to convolve
                if(inputFile==null){
                    this.inputFile = getFileFromFChooser();
                }
                // Let the user choose a directory to create the image
                // Choose the directory in which the user wants to save the settings
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
                    String nameFileOut = chooseNameDialog();
                    // Create a file with the name
                    fileOut = new File(dc.getInitialDirectory()+"\\"+nameFileOut+".bmp");
                    performConvolution(this.inputFile.getAbsolutePath(), fileOut.getAbsolutePath(), rulesGaussian);
                    performGrayscale(fileOut.getAbsolutePath(), fileOut.getAbsolutePath());
                    mergeSobels(fileOut.getAbsolutePath(),fileOut.getAbsolutePath());
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
            //this.inputFile = getFileFromFChooser();
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
        
        convolveBtn.setOnAction((event)->{
            System.out.println("Convolution button clicked");
            if(inputFile==null){
                getFromFileBtn.getOnAction();
            }
        });
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
     * @param fileNameIn
     * @param fileNameOut
     * @param rulesModel
     * This method takes an image input, performs a convolution to it, and outputs the image output
     * This source was used as a reference to use ImageIO in the context of performing a convolution (However, the entire algorithm for performing a given convolution on an array was created separately by Loovdrish):
     * https://ramok.tech/2018/09/27/convolution-in-java/
     * @throws IOException 
     */
    public static void performConvolution(String fileNameIn, String fileNameOut, float[][] rulesModel) throws IOException{
        //Takes the file and creates a BufferedImage
        BufferedImage BI = createBI(fileNameIn);
        //Creates 3 arrays containing the value of green, red and blue for each pixel
        float[][] r = new float[BI.getWidth()][BI.getHeight()];
        float[][] g = new float[BI.getWidth()][BI.getHeight()];
        float[][] b = new float[BI.getWidth()][BI.getHeight()];
        //Initialize the values of r, g, and b
        Color color;
        for(int w=0; w<BI.getWidth(); w++){
            for(int h=0; h<BI.getHeight(); h++){
                color = new Color(BI.getRGB(w, h));
                r[w][h] = color.getRed();
                g[w][h] = color.getGreen();
                b[w][h] = color.getBlue();
            }
        }
        //Perform the convolution on the colours array individually
        float[][] rFinal = performConvolutionOnArray(rulesModel, r);
        float[][] gFinal = performConvolutionOnArray(rulesModel, g);
        float[][] bFinal = performConvolutionOnArray(rulesModel, b);
        //Combine the colours to make a new image
        BufferedImage finalImage = new BufferedImage(r.length, r[0].length, BufferedImage.TYPE_INT_RGB);
        for(int w=0; w<BI.getWidth(); w++){
            for(int h=0; h<BI.getHeight(); h++){
                try{
                color = new Color(rFinal[w][h]/256, gFinal[w][h]/256, bFinal[w][h]/256);
                }catch(Exception e){
                    System.out.println("red="+r[w][h]+"green="+g[w][h]+"blue="+b[w][h]);
                    color= new Color(0,0,0);
                }
                finalImage.setRGB(w, h, color.getRGB());
            }
        }
        //Creates the output file
        File file = new File(fileNameOut);
        //Writes the output file with the data of the BufferedImage
        ImageIO.write(finalImage, "bmp", file);
    }
    /**
     * This method applies a kernel to an array. 
     * @param rulesModel - The 2-dimensional array modeling the kernel
     * @param in - The array on which we want to apply to convolution
     * @return result  - The output array, after the convolution has been done on the input array
     */
    public static float[][] performConvolutionOnArray(float[][] rulesModel, float[][] in){
        //Create the output array
        float[][] result = new float[in.length][in[0].length];
        // The addition of all the values in the rules array => Is used for the averaging the value of the pixel
        float weightRules=0;
        // Calculate the weight of the rules (The sum on all the values in rules)
        for(int r=0; r<rulesModel.length; r++){
            for(int c=0; c<rulesModel[0].length; c++){
                weightRules = weightRules+rulesModel[r][c];
            }
        }
        // Setting the values of the result array, by applying the kernel on each value
        for(int counterR=0; counterR<in.length; counterR++){
            for(int counterC=0; counterC<in[0].length; counterC++){
                result[counterR][counterC] = performConvolutionOnPix(rulesModel, weightRules, in, counterR, counterC);
            }
        }
        //Return the result array
        return result;
    }
    /**
     * This method is the unit method for the convolutions. It returns the value
     * at x,y coordinates that the final array should have based on a rules 
     * array, an input array, and the location of the value of interest.
     * @param rulesModel - The array that described the kernel to be applied
     * @param weightRules - The sum of all the values in the kernel
     * @param in - The input array, which contains the central and neighbouring values on the array
     * @param r - The value of the row
     * @param c - The value of the column
     * @return floating number which corresponds to the value of the pixel after the kernel has been applied on the central and neighbouring values.
     */
    public static float performConvolutionOnPix(float[][] rulesModel, float weightRules, float[][] in, int r, int c){
        float result = 0f;
        //Need to loop over each value around the central pixel
        for(int counterR=0; counterR<rulesModel.length; counterR++){
            for(int counterC=0; counterC<rulesModel[0].length; counterC++){
                //To computer the corners and edges, we need to keep in mind that some values around the central one may not exist
                //We need to determine if these values exist or not
                // If they exist
                if((counterR+r-(rulesModel.length/2)>=0)&&(counterR+r<=in.length)&&(counterC+c-(rulesModel[0].length/2)>=0)&&(counterC+c<=in[0].length)){
                    result = result+in[counterR+r-(rulesModel.length/2)][counterC+c-(rulesModel[0].length/2)]*rulesModel[counterR][counterC];
                }
                //If they do not exist
                else{
                    //We need to substract this value from the weight rules so that it does not impact the averaging
                    weightRules = weightRules-rulesModel[counterR][counterC];
                }
            }
        }
        //Some kernels (like the ones used for sobel) have a total weight of 0. Hence, we need to make sure that the division does not happen
        // This will not impact the accuracy of the method, because such methods are complete without the final division
        if(weightRules!=0)
            result = result/weightRules;
        return result;
    }
    /**
     * This method takes the path to a file and outputs the BufferedImage corresponding to that file.
     * @param filePath - The path to the image file
     * @return BufferedImage - The corresponding BufferedImage
     * @throws IOException 
     * Source: https://ramok.tech/2018/09/27/convolution-in-java/
     */
    public static BufferedImage createBI(String filePath) throws IOException{
        File file = new File(filePath);
        return ImageIO.read(new File(file.getAbsolutePath()));
    }
    /**
     * This method creates a dialog that is responsible of letting the user choose a name for the file he wants to create.
     * It returns the name of the file.
     * It creates a new stage which will be used as a window to contain the text field used to write the name of the file.
     * This is a method that Loovdrish has implemented last semester for the project on wave simulation
     * @return nameFile, String which corresponds to the name of the file
     */
    public String chooseNameDialog(){
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
     * This method takes an input image and outputs its grayscale.
     * @param filePathIn - The path of the file input
     * @param filePathOut - The path of the file output
     * @throws IOException 
     * Source used as a reference to use ImageIO: https://ramok.tech/2018/09/27/convolution-in-java/
     */
    private static void performGrayscale(String filePathIn, String filePathOut) throws IOException {
        BufferedImage BI = createBI(filePathIn);
        BufferedImage finalImage = new BufferedImage(BI.getWidth(), BI.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        //Initialize the values of r, g, and b
        //Snippet of code taken from https://www.youtube.com/watch?v=zSo3QZTleqA
        int p,a,r,g,b,avg;
        for(int w=0; w<BI.getWidth(); w++){
            for(int h=0; h<BI.getHeight(); h++){
                p = BI.getRGB(w, h);
                a = (p>>24)&0xff;
                r = (p>>16)&0xff;
                g = (p>>8)&0xff;
                b = p&0xff;
                avg = (r+g+b)/3;
                p=(a<<24)|(avg<<16)|(avg<<8)|avg;
                finalImage.setRGB(w, h, p);
            }
        }
        File file = new File(filePathOut);
        ImageIO.write(finalImage, "bmp", file);
        
    }
    /**
     * This method performs Sobel edge detection along the x axis for an image.
     * 
     * @param filePathIn
     * @param filePathOut
     * @throws IOException 
     * This source was used as a reference to use ImageIO in the context of performing a convolution:
     * https://ramok.tech/2018/09/27/convolution-in-java/
     */
    private void performSobelX(String filePathIn, String filePathOut) throws IOException {
        BufferedImage BI = createBI(filePathIn);
        // Create the array gray corresponding to the average values of the pixels
        float[][] g = new float[BI.getWidth()][BI.getHeight()];
        //Initialize the values of g
        Color color;
        //The values of a geayscale image are uniform, meaning that the values for red, blue, and green are all the same
        // Therefore, we can take any one of these three to initialize the array g (g)
        for(int w=0; w<BI.getWidth(); w++){
            for(int h=0; h<BI.getHeight(); h++){
                color = new Color(BI.getRGB(w, h));
                g[w][h] = color.getGreen();
            }
        }
        //Perform the convolution on the gray array, in order to get the final one
        // gFinal contains the floating numbers describing how much the colour values change to its left and right. (It does not represent the grayscale value, but the difference in the grayscale)
        float[][] gFinal = performConvolutionOnArray(rulesSobelX, g);
        //Make a new image
        BufferedImage finalImage = new BufferedImage(g.length, g[0].length, BufferedImage.TYPE_INT_RGB);
        for(int w=0; w<BI.getWidth(); w++){
            for(int h=0; h<BI.getHeight(); h++){
                //If the difference is bigger than the threshold, color that spot white
                if(gFinal[w][h]>threshold){
                    color =new Color(255,255,255);
                }
                //If not, colour it black
                else{
                    color = new Color(0,0,0);
                    System.out.println(gFinal[w][h]);
                }
                //Set the value of the colour
                finalImage.setRGB(w, h, color.getRGB());
            }
        }
        // Create and write the output file
        File file = new File(filePathOut);
        ImageIO.write(finalImage, "bmp", file);
    }
    /**
     * This method performs Sobel edge detection along the y axis for an image.
     * 
     * @param filePathIn
     * @param filePathOut
     * @throws IOException 
     * This source was used as a reference to use ImageIO in the context of performing a convolution:
     * https://ramok.tech/2018/09/27/convolution-in-java/
     */
    private void performSobelY(String filePathIn, String filePathOut) throws IOException {
        BufferedImage BI = createBI(filePathIn);
        // Create the array gray corresponding to the average values of the pixels
        float[][] g = new float[BI.getWidth()][BI.getHeight()];
        //Initialize the values of g
        Color color;
        //The values of a geayscale image are uniform, meaning that the values for red, blue, and green are all the same
        // Therefore, we can take any one of these three to initialize the array g (g)
        for(int w=0; w<BI.getWidth(); w++){
            for(int h=0; h<BI.getHeight(); h++){
                color = new Color(BI.getRGB(w, h));
                g[w][h] = color.getGreen();
            }
        }
        //Perform the convolution on the gray array, in order to get the final one
        // gFinal contains the floating numbers describing how much the colour values change up to down. (It does not represent the grayscale value, but the difference in the grayscale)
        float[][] gFinal = performConvolutionOnArray(rulesSobelY, g);
        //Make a new image
        BufferedImage finalImage = new BufferedImage(g.length, g[0].length, BufferedImage.TYPE_INT_RGB);
        for(int w=0; w<BI.getWidth(); w++){
            for(int h=0; h<BI.getHeight(); h++){
                //If the difference is bigger than the threshold, color that spot white
                if(gFinal[w][h]>threshold){
                    color =new Color(255,255,255);
                }
                // If not, colour it black
                else{
                    color = new Color(0,0,0);
                    System.out.println(gFinal[w][h]);
                }
                //Set the value of the colour
                finalImage.setRGB(w, h, color.getRGB());
            }
        }
        // Create and write the output file
        File file = new File(filePathOut);
        ImageIO.write(finalImage, "bmp", file);
    }
    /**
     * This method detects all edges of an input image, and outputs the result outlining the edges of the image
     * @param filePathIn - The path of the path input
     * @param filePathOut - The path of the file output
     * @throws IOException 
     * This source was used as a reference to use ImageIO in the context of performing a convolution:
     * https://ramok.tech/2018/09/27/convolution-in-java/
     */
    private void mergeSobels(String filePathIn, String filePathOut) throws IOException{
        BufferedImage BI = createBI(filePathIn);
        //Create the array corresponding to the differences around the central pixel for the grayscale
        float[][] g = new float[BI.getWidth()][BI.getHeight()];
        //Initialize the values of g
        Color color;
        for(int w=0; w<BI.getWidth(); w++){
            for(int h=0; h<BI.getHeight(); h++){
                color = new Color(BI.getRGB(w, h));
                g[w][h] = color.getGreen();
            }
        }
        //Perform the convolution on the colours array individually
        float[][] gFinalX = performConvolutionOnArray(rulesSobelX, g);
        float[][] gFinalY = performConvolutionOnArray(rulesSobelY, g);
        //Combine the sobels to make a new image
        BufferedImage finalImage = new BufferedImage(g.length, g[0].length, BufferedImage.TYPE_INT_RGB);
        for(int w=0; w<BI.getWidth(); w++){
            for(int h=0; h<BI.getHeight(); h++){
                if(gFinalX[w][h]>threshold){
                    color =new Color(255,255,255);
                }
                else if(gFinalY[w][h]>threshold){
                    color =new Color(255,255,255);
                }
                else{
                    color = new Color(0,0,0);
                }
                finalImage.setRGB(w, h, color.getRGB());
            }
        }
        //Create and write the final image
        File file = new File(filePathOut);
        ImageIO.write(finalImage, "bmp", file);
    }
}
