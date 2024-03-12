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
        BufferedImage img = createBI("src\\main\\resources\\images\\landscape.bmp");
        createImage(getRGBFromBI(img), "src\\main\\resources\\images\\landscape2.bmp");
        
    }
    public static BufferedImage createBI(String fileName) throws IOException{
        File file = new File(fileName);
        return ImageIO.read(new File(file.getAbsolutePath()));
    }
    /**
     * 0=RED
     * 1=GREEN
     * 2=BLUE
     * Idea of having a third dimension storing each value of the RGB + Code very similar to: https://ramok.tech/2018/09/27/convolution-in-java/
     */
    public static float[][][] getRGBFromBI(BufferedImage img){
        float[][][] rgb = new float[img.getWidth()][img.getHeight()][3];
        Color color;
        for(int w=0; w<img.getWidth(); w++){
            for(int h=0; h<img.getHeight(); h++){
                color = new Color(img.getRGB(w, h));
                rgb[w][h][0] = color.getRed();
                rgb[w][h][1] = color.getGreen();
                rgb[w][h][2] = color.getBlue();
            }
        }
        return rgb;
    }
    /**
     * https://ramok.tech/2018/09/27/convolution-in-java/
     */
    public static File createImage(float[][][] rgb, String fileName) throws IOException{
        BufferedImage finalImage = new BufferedImage(rgb.length, rgb[0].length, BufferedImage.TYPE_INT_RGB);
        for(int w=0; w<rgb.length; w++){
            for(int h=0; h<rgb[0].length; h++){
                finalImage.setRGB(w, h, (int) (rgb[w][h][0]+rgb[w][h][1]+rgb[w][h][2]));
            }
        }
        File file = new File(fileName);
        ImageIO.write(finalImage, "png", file);
        return file;
    }
}
