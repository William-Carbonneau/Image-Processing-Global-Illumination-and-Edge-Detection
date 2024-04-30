package edu.vanier.global_illumination_image_processing.tests;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Test database class - do not run alone
 * @author 2265724
 */
public class SQLiteTestGUI extends Application{
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SQLiteGUITestFXML.fxml"));
            // Create the controller by calling the constructor of FXMLMainAppController
            // Since the game has just begun, the level is 1, and the initial score of the player is 0
            testControllerSQLiteGUI controller = new testControllerSQLiteGUI();
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
class testControllerSQLiteGUI{
    @FXML
    Button ClickMeBtn;
    @FXML
    ImageView iv;
    @FXML
    public void initialize(){
        ClickMeBtn.setOnAction((event)->{
            try {
                initImageDB("Images", "ImagesConvolutions");
            } catch (SQLException ex) {
                System.out.println("Could not initialize the image database");
            } catch (FileNotFoundException ex) {
                Logger.getLogger("Weird Exception caught");
            } catch (IOException ex) {
                Logger.getLogger(testControllerSQLiteGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    //Source: https://github.com/frostybee/fx-gallery/tree/main/src/main/java/org/bee/fxgallery/db TODO citation
    //Source: https://www.youtube.com/watch?v=0beocykXUag TODO citation
    //Source: https://www.tutorialspoint.com/what-is-jdbc-blob-data-type-how-to-store-and-read-data-from-it  TODO citation
    private void initImageDB(String title, String tableName) throws SQLException, FileNotFoundException, IOException {
        Connection connection = null;
        try{
            connection = DriverManager.getConnection("jdbc:sqlite:"+title+".db");
            Statement stmt = connection.createStatement();
            System.out.println("Connection opened");
            try{
                stmt.execute("DROP TABLE "+tableName);
                System.out.println("Table that already existed has been deleted");
            }catch(SQLException e){
                System.out.println("The table does not exist");
            }
            System.out.println("Reading main");
            //Create the table
            String stmtCreateTable = "" +
                "CREATE TABLE "+tableName+
                "( "+
                "title varchar(255), "+
                "image BLOB"+
                "); "+
                "";
            stmt.execute(stmtCreateTable);
            System.out.println("Table has been sucesfully created");
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO "+tableName+"(title, image) VALUES(?,?)");
            System.out.println("pstmt succesfully declared");
            pstmt.setString(1,"Mountain");
            System.out.println("Lena title succesfully done");
            File file = new File("C:\\Users\\shalini\\Downloads\\Github\\Image-Processing-Global-Illumination-and-Edge-Detection\\src\\main\\resources\\Images\\Convolutions\\h.bmp");
            FileInputStream fin  = new FileInputStream(file.getAbsolutePath());
            System.out.println("Fin Created");
            byte[] b = fin.readAllBytes();
            pstmt.setBytes(2, b);
            System.out.println("FileInput done");
            pstmt.execute();
            System.out.println("Execute method done");
            System.out.println("Data has been succesfully inserted");
            File file2 = new File("C:\\Users\\shalini\\Downloads\\Github\\Image-Processing-Global-Illumination-and-Edge-Detection\\src\\main\\resources\\Images\\Convolutions\\sample4.bmp");
            FileInputStream fin2  = new FileInputStream(file2.getAbsolutePath());
            byte[] b2 = fin2.readAllBytes();
            PreparedStatement pstmt2 = connection.prepareStatement("INSERT INTO "+tableName+"(title, image) VALUES(?,?)");
            pstmt2.setString(1, "Lena");
            pstmt2.setBytes(2, b2);
            pstmt2.execute();
            //Read from db
            ResultSet rs = stmt.executeQuery("Select *from "+tableName);
            while(rs.next()) {
                String titleReceive = rs.getString("title");
                byte[] bReceive = rs.getBytes("image");
                System.out.println("Name: "+titleReceive);
                System.out.println("Tutorial Type: "+bReceive);
                System.out.println();
            }
            //Show on image view
            File fileCreate =new File("C:\\Users\\shalini\\Downloads\\Github\\Image-Processing-Global-Illumination-and-Edge-Detection\\src\\main\\resources\\Images\\Convolutions\\hOut.bmp");
            FileOutputStream FOS = new FileOutputStream(fileCreate);
            System.out.println("FOS Created");
            System.out.println(b.length);
            FOS.write(b);
            System.out.println("FOS written");
            Image image = new Image(fileCreate.getAbsolutePath());
            System.out.println("Image created");
            iv.setImage(image);
            System.out.println("Image set");
            
        }catch(SQLException e){
            System.out.println("Action interrupted");
        }
        finally{
            if(connection!=null){
                connection.close();
                System.out.println("Connection succefully closed");
            }
        }
    }

    private void deleteTable(Connection connection, String title) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("DELETE TABLE "+title);
    }

    private void createTable(Connection connection, String title, String elements) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("CREATE TABLE "+title+"("+elements+")");
    }
    
}
