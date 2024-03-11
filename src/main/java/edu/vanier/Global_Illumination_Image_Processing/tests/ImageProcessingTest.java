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
public class ImageProcessingTest {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        //BufferedImage bi = new BufferedImage();
        File file = new File("src\\main\\resources\\images\\landscape.bmp");
        BufferedImage img = ImageIO.read(new File(file.getAbsolutePath()));
        int width = img.getWidth();
        int height = img.getHeight();

        double[][][] image = new double[3][height][width];
        for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            Color color = new Color(img.getRGB(j, i));
            image[0][i][j] = color.getRed();
            image[1][i][j] = color.getGreen();
            image[2][i][j] = color.getBlue();
        }
    }
        System.out.println(image[1][1][1]);
        
    }
}
