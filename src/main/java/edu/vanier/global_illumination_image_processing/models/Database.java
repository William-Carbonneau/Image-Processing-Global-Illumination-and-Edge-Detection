package edu.vanier.global_illumination_image_processing.models;

import edu.vanier.global_illumination_image_processing.controllers.FXMLConvolutionsSceneController;
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
 *
 * @author Loovdrish Sujore
 * Learned the syntax and familiarized himself through:
 * https://www.youtube.com/watch?v=0beocykXUag (Logic Lamda, 2021)
 * https://github.com/frostybee/fx-gallery/tree/main/src/main/java/org/bee/fxgallery/db (Frostybee, 2024)
 */
public class Database {
    
    public static void insertRow(String databaseName, String tableName, String titleImage, File temp) throws SQLException, FileNotFoundException, IOException{
        if(titleImage==null||titleImage.equals("Name")){
            return;
        }
        if(verifyImageNameValid(titleImage, databaseName, tableName)==false){
            FXMLConvolutionsSceneController.showAlertInfo("The image was not saved because the name is not unique. Please try again.");
            return;
        }
            Connection connection = null;
            FileInputStream FIS = new FileInputStream(temp);
            byte[] data = FIS.readAllBytes();
            try{
                connection = DriverManager.getConnection("jdbc:sqlite:"+databaseName+".db");
                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO "+tableName+"(title, image) VALUES(?,?)");
                pstmt.setString(1, titleImage);
                pstmt.setBytes(2, data);
                pstmt.execute();
            }catch(SQLException e){
                System.out.println("An error occured while trying to save the file into the database.");
            }
            finally{
                FIS.close();
                connection.close();
            }
        
    }
    public static void deleteRow(String databaseName, String tableName, String titleImage) throws IOException, SQLException{
        Connection connection = null;
        try{
            connection = DriverManager.getConnection("jdbc:sqlite:"+databaseName+".db");
            String insertQuery = String.format("DELETE FROM "+tableName+" WHERE title = ? ");
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            pstmt.setString(1, titleImage);
            pstmt.executeUpdate();
        }catch(SQLException e){
            System.out.println("An error occured while trying to delete the file from the database.");
        }
        finally{
            connection.close();
        }
    }

    private static boolean verifyImageNameValid(String nameImage, String databaseName, String tableName) {
        Connection connection =null;
        byte[] data = null;
        try{
            connection  = DriverManager.getConnection("jdbc:sqlite:"+databaseName+".db");
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("Select *from "+tableName);
            while(rs.next()){
                String t = rs.getString("title");
                if(t.equals(nameImage)){
                    return false;
                }
            }
        }catch(Exception e){
            System.out.println("Error caught");
            return false;
        }
        return true;
    }
    
    }
