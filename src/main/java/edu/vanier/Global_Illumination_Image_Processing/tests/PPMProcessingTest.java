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
        byte[][] green = new byte[480][360];
        byte[][] alpha = new byte[480][360];
        byte[][] red = new byte[480][360];
        byte[][] blue = new byte[480][360];
        for(int x=0; x<480; x++){
            for(int y=0; y<360; y++){
            int color = img.getRGB(x,y);
            //red[x][y] = (byte)(color>>16);
            green[x][y] = (byte)(color>>8);
            //blue[x][y] = (byte)(color);
            //System.out.println(red[x][y]);
            System.out.println("color "+ color);
            System.out.println(green[x][y]);
            //System.out.println(blue[x][y]);
            }
        }
        
    }
}
