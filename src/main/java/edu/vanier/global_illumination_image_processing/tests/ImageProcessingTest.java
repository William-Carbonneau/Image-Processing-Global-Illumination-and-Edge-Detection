package edu.vanier.Global_Illumination_Image_Processing.tests;

import edu.vanier.Global_Illumination_Image_Processing.models.Convolution;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * 
 */
public class ImageProcessingTest {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        float[][] rules = {{1,2,1},{2,4,2},{1,2,1}};
        Convolution.performConvolution("src\\main\\resources\\images\\landscape.bmp", "src\\main\\resources\\images\\landscape12.bmp", rules);
        
        
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
