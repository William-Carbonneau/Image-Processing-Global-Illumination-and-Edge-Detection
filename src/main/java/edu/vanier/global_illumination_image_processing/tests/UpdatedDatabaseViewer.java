package edu.vanier.global_illumination_image_processing.tests;

import ch.qos.logback.core.util.Loader;
import edu.vanier.global_illumination_image_processing.controllers.FXMLConvolutionsSceneController;
import static edu.vanier.global_illumination_image_processing.controllers.FXMLConvolutionsSceneController.print;
import java.io.File;
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
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class UpdatedDatabaseViewer extends Application{
    public static void main(String[] args) {
        launch(args);
    }
    

    @Override
    public void start(Stage primaryStage) throws Exception {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLUpdatedDatabaseViewer.fxml"));
            // Create the controller by calling the constructor of FXMLMainAppController
            // Since the game has just begun, the level is 1, and the initial score of the player is 0
            FXMLControllerViewerDatabase controller = new FXMLControllerViewerDatabase();
            // Set the controller of the loader
            loader.setController(controller);
            // Load the pane from the fxml
            SplitPane root = loader.load();
            //-- 2) Create and set the scene to the stage.
            Scene scene = new Scene(root, 800, 800);
            // Set the scene of the primary stage
            primaryStage.setScene(scene);
            // Set the title of the application
            primaryStage.setTitle("Test");
            primaryStage.sizeToScene();
            primaryStage.setAlwaysOnTop(true);
            // Show the primary stage
            primaryStage.show();
        }
        catch(Exception e){System.out.println("Error Caught");}
    }
}
class FXMLControllerViewerDatabase{
    @FXML ScrollPane ScrollPaneItems;
    @FXML TilePane tilePane;
    @FXML Button chooseBtn;
    @FXML Button deleteBtn;
    @FXML
    public void initialize(){
        chooseBtn.setOnAction((event)->{
            System.out.println("Take button clicked");
        });
        deleteBtn.setOnAction((event)->{
            System.out.println("Delete button clicked");
        });
        /*
private void getFromDBAndDisplay(String titleDatabase,String tableName,  FlowPane root) throws FileNotFoundException, IOException {
        Connection connection = null;
        ArrayList<ImageView> imvs = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<byte[]> bs = new ArrayList<>();
        try{
            connection  =DriverManager.getConnection("jdbc:sqlite:"+titleDatabase+".db");
            print("Connection established");
            Statement stmt = connection.createStatement();
            //Get the values from the table
            String getResultSetFromDB = "Select *from "+tableName;
            ResultSet rs = stmt.executeQuery(getResultSetFromDB);
            print("rs created");
            System.out.println("File temp created");
            Image image;
            ImageView imageview;
            System.out.println(rs.getFetchSize());
            Label title;
            VBox imageAndTitle;
            String chosen;
            while(rs.next()){
                imageAndTitle = new VBox();
                String titleImage = rs.getString("title");
                titles.add(titleImage);
                byte[] b = rs.getBytes("image");
                bs.add(b);
                temp = new File("src\\main\\resources\\Images\\Convolutions\\"+titleImage+".bmp");
                FileOutputStream FOS = new FileOutputStream(temp);
                FOS.write(b);
                System.out.println("FOS written");
                image  = new Image(temp.getAbsolutePath());
                imageview = new ImageView();
                imageview.setFitHeight(100);
                imageview.setFitWidth(100);
                imageview.setImage(image);
                imvs.add(imageview);
                title = new Label();
                title.setText(titleImage);
                imageAndTitle.getChildren().addAll(imageview,title);
                root.getChildren().add(imageAndTitle);
                FOS.flush();
            }
            for(int i = 0; i<imvs.size(); i++){
                imvs.get(i).setOnMouseClicked((event)->{
                    System.out.println(imvs.indexOf(iv));
                });
            }
            imvs.forEach((i)->{
                iv = i;
                i.setOnMouseClicked((event)->{
                System.out.println(imvs.indexOf(event.getPickResult().getIntersectedNode()));
                temp = new File("src\\main\\resources\\Images\\Convolutions\\temp.bmp");
                    try {
                        FOS = new FileOutputStream(temp);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(FXMLConvolutionsSceneController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        FOS.write(bs.get(imvs.indexOf(event.getPickResult().getIntersectedNode())));
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLConvolutionsSceneController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                imageBeingDisplayedOnIV = temp;
                imageImgView.setImage(new Image(temp.getAbsolutePath()));
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
    
    */
    }
}