package edu.vanier.Lumina.models;

import edu.vanier.Lumina.controllers.FXMLConvolutionsSceneController;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * This class is a base class for the Database. It is a collection of methods that handle the database.
 * 
 * @author Loovdrish Sujore
 * Learned the syntax and familiarized himself through:
 * https://www.youtube.com/watch?v=0beocykXUag (Logic Lamda, 2021)
 * https://github.com/frostybee/fx-gallery/tree/main/src/main/java/org/bee/fxgallery/db (Frostybee, 2024)
 * https://www.tutorialspoint.com/what-is-jdbc-blob-data-type-how-to-store-and-read-data-from-it  (Thomas)
 */
public class Database {
    /**
     * This is a static method which inserts a row of data in the database.
     * @param databaseName- The name of the database is "Images"
     * @param tableName - The name of the table is "ImagesConvolutions"
     * @param titleImage - The title of the image. This will serve as an ID when retrieving the image
     * @param temp - We will use the temporary file of the program to contain the bytes of the image to be saved (We are saving the bytes of the image.)
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static void insertRow(String databaseName, String tableName, String titleImage, File temp) throws SQLException, FileNotFoundException, IOException{
        //Need to make that the image has a title - Else, it may result in difficulties when retrieving the image.
        if(titleImage==null||titleImage.equals("Name")){
            return;
        }
        //We need the verify that the name of the image is valid - No image with the same name in the database.
        if(verifyImageNameValid(titleImage, databaseName, tableName)==false){
            FXMLConvolutionsSceneController.showAlertInfo("The image was not saved because the name is not unique. Please try again.");
            return;
        }
        //Establish the connection
        Connection connection = null;
        //File Input Stream to read the content of the temp file - The file of the image
        FileInputStream FIS = new FileInputStream(temp);
        //Get the bytes from the image
        byte[] data = FIS.readAllBytes();
            try{
                connection = DriverManager.getConnection("jdbc:sqlite:"+databaseName+".db");
                //Insert in the table
                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO "+tableName+"(title, image) VALUES(?,?)");
                pstmt.setString(1, titleImage);
                pstmt.setBytes(2, data);
                pstmt.execute();
            }catch(SQLException e){
                FXMLConvolutionsSceneController.showAlertInfo("An error occured while trying to save the file into the database.");
            }
            finally{
                //Close the connection
                FIS.close();
                connection.close();
            }
        
    }
    /**
     * This method deletes a row from the table
     * @param databaseName -The name of the database is "Images"
     * @param tableName - The name of the table is "ImagesConvolutions"
     * @param titleImage - - The title of the image. This will serve as an ID when retrieving the image
     * @throws IOException
     * @throws SQLException 
     */
    public static void deleteRow(String databaseName, String tableName, String titleImage) throws IOException, SQLException{
        //Establish the connection with the database
        Connection connection = null;
        try{
            connection = DriverManager.getConnection("jdbc:sqlite:"+databaseName+".db");
            //Drop the row
            String insertQuery = String.format("DELETE FROM "+tableName+" WHERE title = ? ");
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            pstmt.setString(1, titleImage);
            pstmt.executeUpdate();
        }catch(SQLException e){
            FXMLConvolutionsSceneController.showAlertInfo("An error occured while trying to delete the file from the database.");
        }
        finally{
            //Close the connection
            connection.close();
        }
    }
    /**
     * This method loop over every row in the database and verifies of the string inserted is already present in the database.
     * Since we are separating the rows using the name of the images, no two images can have the same name.
     * @param nameImage -The name of the image we are trying the save.
     * @param databaseName -The name of the database is "Images"
     * @param tableName -The name of the table is "ImagesConvolutions"
     * @return Boolean which says if the name is valid (unique) (true) or not (false)
     */
    private static boolean verifyImageNameValid(String nameImage, String databaseName, String tableName) {
        //Establish the connection
        Connection connection =null;
        byte[] data = null;
        try{
            connection  = DriverManager.getConnection("jdbc:sqlite:"+databaseName+".db");
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("Select *from "+tableName);
            //Loop over every loop and verify if there is a name identical to nameImage
            while(rs.next()){
                String t = rs.getString("title");
                if(t.equals(nameImage)){
                    //Return false if there is one
                    return false;
                }
            }
        }catch(Exception e){
            //If an error occurs, return false
            FXMLConvolutionsSceneController.showAlertInfo("An error occured while trying to insert the image inside of the database.");
            return false;
        }
        //Else return true.
        return true;
    }
    
    }
