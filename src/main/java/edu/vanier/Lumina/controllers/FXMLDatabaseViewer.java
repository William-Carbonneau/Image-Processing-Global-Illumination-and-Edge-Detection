package edu.vanier.Lumina.controllers;

import edu.vanier.Lumina.models.Database;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLDatabaseViewer {
    /**
     * This refers to the image that was being displayed on the main image view of the main window, before that window was opened.
     * It is important to have that data, because the user does not pick or choose any image from the database, we need an object that stored
     * that data. However, we are using the temp file to show the images on the secondary and primary window. Therefore, we cannot count on 
     * temp to preserve that data.
     */
    private byte[] imageBeingDisplayedOnIV;
    /**
     * temp refers to a temporary file. We are using a temporary file to create Image objects. To minimize space, we using one single file for the entire
     * project, This file temp is recycled as many times as there are images from the database and as many convolutions are being performed on an image.
     */
    private File temp = new File("src\\main\\resources\\Images\\Convolutions\\temp.bmp");
    private ImageView iv;
    private ArrayList<ImageView> imvs = new ArrayList<>();
    private int indexSelected=-1;
    /**
     * the passedImage refers to the data, in the form of bytes, of the selected image by the user from the database.
     * If the user does not choose anything, for example, by closing the window, the initial image is passed.
     */
    private byte[] passedImage;
    private Stage stage;
    private ArrayList<String> titles;
    private ArrayList<byte[]> bs;
    @FXML
    SplitPane SPane;
    @FXML TilePane tilePane;
    @FXML Button btnChoose;
    @FXML Button btnDelete;
    
    @FXML
    public void initialize() throws IOException{
        getFromDBAndDisplay("Images","ImagesConvolutions",tilePane);
        
        /**
         * Load the currently selected image
         */
        btnChoose.setOnAction((event) -> {
            if (indexSelected == -1) 
                passedImage = imageBeingDisplayedOnIV;
            else
                passedImage = bs.get(indexSelected);
            this.stage.close();
        });
        
        /**
         * deletes selected image from database
         */
        btnDelete.setOnAction((event) -> {
            if(indexSelected!=-1 && (indexSelected>4))
            try {
                Database.deleteRow("Images","ImagesConvolutions", titles.get(indexSelected));
                //Update the viewer by eliminating all children and adding them if they are still in the database
                tilePane.getChildren().removeAll(tilePane.getChildren());
                getFromDBAndDisplay("Images","ImagesConvolutions",tilePane);
            } catch (IOException ex) {
                Logger.getLogger(FXMLDatabaseViewer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(FXMLDatabaseViewer.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    /**
     * Parametrized constructor
     * 
     * @param stage Stage to create viewer
     * @param temp File temporary file
     * @param primaryStage Stage primary stage owner of this popup 
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public FXMLDatabaseViewer(Stage stage, File temp, Stage primaryStage) throws FileNotFoundException, IOException {
        this.stage = stage;
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.initOwner(primaryStage);
        FileInputStream FIS = new FileInputStream(temp);
        this.imageBeingDisplayedOnIV = FIS.readAllBytes();
        passedImage = this.imageBeingDisplayedOnIV;
    }

    /**
     * TODO
     * @return 
     */
    public byte[] getPassedImage() {
        return passedImage;
    }
    
    /**
     * 
     * @param databaseName - The database we are using is called "Images"
     * @param tableName - The table we are using is called "ImagesConvolutions"
     * @param titleImage - The name of the image
     * @return []data - the bytes representing the image, which can be passed to create an actual file
     * @throws SQLException 
     */
    private byte[] getImageFromDB(String databaseName, String tableName, String titleImage) throws SQLException{
        Connection connection =null;
        byte[] data = null;
        try{
            connection  = DriverManager.getConnection("jdbc:sqlite:"+databaseName+".db");
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("Select *from "+tableName);
            while(rs.next()){
                String t = rs.getString("title");
                if(t.equals(titleImage)){
                    data = rs.getBytes("image");
                }
            }
        }catch(Exception e){
            System.out.println("Error caught");
        }
        finally{
            connection.close();
        }
        if(data!=null){
            System.out.println("Match found");
            return data;
        }
        else{
            System.out.println("No match found for the image");
            return null;
        }
    }
    
    /**
     * Get data from the database and put it in the GUI
     * 
     * @param titleDatabase the String title of the database
     * @param tableName the String name of the database
     * @param root the TilePane to put the data into
     * @throws FileNotFoundException
     * @throws IOException 
     */
    private void getFromDBAndDisplay(String titleDatabase,String tableName,  TilePane root) throws FileNotFoundException, IOException {
        Connection connection = null;
        imvs = new ArrayList<>();
        titles = new ArrayList<>();
        bs = new ArrayList<>();
        ArrayList<VBox> vBoxes = new ArrayList<>();
        // to try connect to the database and loop over the data
        try{
            connection  =DriverManager.getConnection("jdbc:sqlite:"+titleDatabase+".db");
            Statement stmt = connection.createStatement();
            //Get the values from the table
            String getResultSetFromDB = "Select *from "+tableName;
            ResultSet rs = stmt.executeQuery(getResultSetFromDB);
            System.out.println("Temporary file created");
            Image image;
            ImageView imageview;
            Label title;
            VBox imageAndTitle;
            // create the individual boxes/tiles and put them in the GUI
            while(rs.next()){
                imageAndTitle = new VBox();
                String titleImage = rs.getString("title");
                titles.add(titleImage);
                byte[] b = rs.getBytes("image");
                bs.add(b);
                temp = new File("src\\main\\resources\\Images\\Convolutions\\temp.bmp");
                FileOutputStream FOS = new FileOutputStream(temp);
                FOS.write(b);
                image  = new Image(temp.getAbsolutePath());
                imageview = new ImageView();
                imageview.setFitHeight(100);
                imageview.setFitWidth(100);
                imageview.setImage(image);
                imvs.add(imageview);
                title = new Label();
                title.setText(titleImage);
                imageAndTitle.getChildren().addAll(imageview,title);
                vBoxes.add(imageAndTitle);
                root.getChildren().add(imageAndTitle);
                FOS.flush();
            }
            // set event listenners on each box
            for(int i = 0; i<imvs.size(); i++){
                imvs.get(i).setOnMouseClicked((event)->{
                    System.out.println(imvs.indexOf(iv));
                });
            }
            // set selected event to put box around clicked object
            imvs.forEach((i)->{
                iv = i;
                i.setOnMouseClicked((event)->{
                    int index = imvs.indexOf(event.getPickResult().getIntersectedNode());
                    if (indexSelected!= -1) vBoxes.get(indexSelected).setStyle("");
                    indexSelected=index;
                    vBoxes.get(indexSelected).setStyle("-fx-border-color: BLACK;");
                    passedImage = bs.get(index);
                });
            });
            
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
