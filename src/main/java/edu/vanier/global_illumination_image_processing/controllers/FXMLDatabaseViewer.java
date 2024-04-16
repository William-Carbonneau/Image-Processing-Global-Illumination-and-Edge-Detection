package edu.vanier.global_illumination_image_processing.controllers;

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
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FXMLDatabaseViewer {
    private File temp;
    private ImageView iv;
    private ArrayList<ImageView> imvs = new ArrayList<>();
    private int indexSelected=-1;
    private Image passedImage;
    private Stage stage;
    @FXML
    SplitPane SPane;
    @FXML TilePane tilePane;
    @FXML Button chooseBtn;
    
    @FXML
    public void initialize() throws IOException{
        getFromDBAndDisplay("Images","ImagesConvolutions",tilePane);
        
        /**
         * Load the currently selected image
         */
        chooseBtn.setOnAction((event) -> {
            ImageView temp;
            if (indexSelected == -1) return;
            temp = imvs.get(indexSelected);
            passedImage = temp.getImage();
            this.stage.hide();
        });
    }
    public FXMLDatabaseViewer(Stage stage, File temp) {
        this.stage = stage;
        this.temp = temp;
    }

    /**
     * TODO
     * @return 
     */
    public Image getPassedImage() {
        return passedImage;
    }
    
    
    
    private void getFromDBAndDisplay(String titleDatabase,String tableName,  TilePane root) throws FileNotFoundException, IOException {
        Connection connection = null;
        imvs = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<byte[]> bs = new ArrayList<>();
        ArrayList<VBox> vBoxes = new ArrayList<>();
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
                vBoxes.add(imageAndTitle);
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
                    int index = imvs.indexOf(event.getPickResult().getIntersectedNode());
                    if (indexSelected!= -1) vBoxes.get(indexSelected).setStyle("");
                    indexSelected=index;
                    vBoxes.get(indexSelected).setStyle("-fx-border-color: BLACK;");
                
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
