package edu.vanier.Global_Illumination_Image_Processing.tests;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import javax.imageio.ImageIO;
import static javax.swing.Spring.height;
import static javax.swing.Spring.width;

/**
 * 
 */
public class PPMProcessingTest {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        //BufferedImage bi = new BufferedImage();
        File file = new File("src\\main\\resources\\images\\landscape.bmp");
        BufferedImage img = ImageIO.read(new File(file.getAbsolutePath()));
        // you should stop here
        int[][] green = new int[480][360];
        int[][] alpha = new int[480][360];
        int[][] red = new int[480][360];
        int[][] blue = new int[480][360];
        for(int x=0; x<480; x++){
            for(int y=0; y<360; y++){
            int color = img.getRGB(x,y);
            alpha[x][y] = (int)(color>>24);
            red[x][y] = (int)(color>>16);
            green[x][y] = (int)(color>>8);
            blue[x][y] = (int)(color);
            }
        }
        int[][] result = new int[480][360];
        for(int x=0; x<480; x++){
            for(int y=0; y<360; y++){
                //Color color = new Color(red[x][y], green[x][y], blue[x][y], alpha[x][y]);
            }
        }
        
        
    }
        public static  int[] loadData(String filename) throws FileNotFoundException {
        File foo = new File(filename);
        Scanner scan = new Scanner(foo);
            scan.nextLine();
            scan.nextLine();
            int width = scan.nextInt();
            int height = scan.nextInt();
            System.out.print(height);
            scan.nextLine();
            int[] array = new int [width * height];
            int i = 0;
            while (scan.hasNextInt()) {
                array[i] = scan.nextInt();
                i++;
                System.out.print("yes");
                }
            scan.close();
        return array;
    }
}
