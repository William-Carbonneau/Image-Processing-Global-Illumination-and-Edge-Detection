package edu.vanier.global_illumination_image_processing.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//copied from https://www.youtube.com/watch?v=0beocykXUag (Logic Lamda, 2021)

/**
 * test database class - do not run alone
 * @author 2265724
 */
public class SQLiteTest1 {
    public static void main(String[] args) {
        System.out.println("SQLiteTest1 class being read");
        Connection conn = null;
        try{
            conn = DriverManager.getConnection("jdbc:sqlite:FirstDatabase.db");
            System.out.println("Opened database connection");
            try{
                deleteTable(conn);
            }catch(Exception nothing){
                System.out.println("The table exists");
            }
            createTable(conn);
            System.out.println("Data will be inserted");
            insertMovie(conn, "Aliens", "James Cameron", 1986, "R");
            System.out.println("Data has been succesfully inserted");
            System.out.println("Displaying data");
            displayDatabase(conn, "Movies");
        }catch(SQLException e){
            System.out.println("Error caught");
        }
        finally{
            if(conn!=null){
                try{
                    conn.close();
                    System.out.println("Table succesfully closed");
                }catch(SQLException e){
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    
                }
            }
        }
    }
    private static void displayDatabase(Connection conn, String tableName)throws SQLException{
        String selectSQL = "SELECT * from "+tableName;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(selectSQL);
        
        System.out.println("----------------------"+tableName+"-------------------");
        while(rs.next()){
            System.out.println("Movie: "+rs.getString("title")+", ");
            System.out.println(rs.getString("director")+", ");
            System.out.println(rs.getInt("year")+", ");
            System.out.println("Movie: "+rs.getString("rating"));
        }
        System.out.println("----------------------------");
    }
    private static void insertMovie(Connection conn, String title, String director, int year, String rating) throws SQLException{
        String insertSQL = "INSERT INTO Movies(title, director, year, rating) VALUES(?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(insertSQL);
        pstmt.setString(1, title);
        pstmt.setString(2, director);
        pstmt.setInt(3, year);
        pstmt.setString(4, rating);
        pstmt.executeUpdate();
    }
    public static void createTable(Connection conn)throws SQLException{
        String createTablesq = "" +
                "CREATE TABLE Movies"+
                "( "+
                "title varchar(255), "+
                "director varchar(255), "+
                "year integer, "+
                "rating varchar(5) "+
                "); "+
                "";
        Statement stmt = conn.createStatement();
        stmt.execute(createTablesq);
    }
    private static void deleteTable(Connection conn) throws SQLException{
        String deleteTableSQL = "DROP TABLE Movies";
        Statement stmt = conn.createStatement();
        stmt.execute(deleteTableSQL);
    }
}
