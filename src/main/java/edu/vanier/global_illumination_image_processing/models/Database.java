package edu.vanier.global_illumination_image_processing.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Loovdrish Sujore
 */
public class Database {
    Connection connection;
    String databaseName;
    ArrayList<String> tableNames = new ArrayList<>();

    public Database(String databaseName) throws SQLException {
        this.databaseName = databaseName;
        this.connection = DriverManager.getConnection("jdbc:sqlite:src\\main\\resources\\Images\\Convolutions\\"+databaseName+".db");
    }
    
    public void createTable(String tableName){
        tableNames.add(tableName);
    }
    
    public void insertRow(String titleImage, byte[] data){
    }
    
    }
