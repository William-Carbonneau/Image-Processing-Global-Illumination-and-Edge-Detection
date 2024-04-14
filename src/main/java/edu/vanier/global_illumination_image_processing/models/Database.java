package edu.vanier.global_illumination_image_processing.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Loovdrish Sujore
 */
public class Database {
    
    public static void insertRow(String databaseName, String tableName, String titleImage, File temp) throws SQLException, FileNotFoundException, IOException{
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
    public static void deleteRow(String databaseName, String tableName, String titleImage, File temp) throws IOException, SQLException{
        Connection connection = null;
        FileInputStream FIS = new FileInputStream(temp);
        byte[] data = FIS.readAllBytes();
        try{
            connection = DriverManager.getConnection("jdbc:sqlite:"+databaseName+".db");
            String insertQuery = String.format("DELETE FROM %s WHERE id = ? ", tableName);
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            pstmt.setString(1, titleImage);
            pstmt.executeUpdate();
        }catch(SQLException e){
            System.out.println("An error occured while trying to delete the file from the database.");
        }
        finally{
            FIS.close();
            connection.close();
        }
    }
    
    }
