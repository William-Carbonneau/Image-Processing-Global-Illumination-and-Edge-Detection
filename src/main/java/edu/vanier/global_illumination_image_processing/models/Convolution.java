package edu.vanier.Global_Illumination_Image_Processing.models;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * This class is a collection of static methods that are useful for convolutions.
 */
public class Convolution {
    public static void print1DArray(int[] array){
        System.out.print("[");
        for(int counter=0; counter<array.length; counter++){
            System.out.print(array[counter]+" ");
        }
        System.out.println("]");
    }
    public static void print2DArray(float[][] array){
        for (int i=0; i<array.length; i++){
            System.out.print("[");
            for (int j=0; j<array[0].length; j++){
                System.out.print(array[i][j]+" ");
            }
            System.out.println("]");
        }
        System.out.println();
    }
    /**
     * This method prints a 2-d array
     */
    public static void print2DArray(byte[][] array){
        for (int i=0; i<array.length; i++){
            System.out.print("[");
            for (int j=0; j<array[0].length; j++){
                System.out.print(array[i][j]+" ");
            }
            System.out.println("]");
        }
        System.out.println();
    }
    public static void print2DArray(int[][] array){
        for (int i=0; i<array.length; i++){
            System.out.print("[");
            for (int j=0; j<array[0].length; j++){
                System.out.print(array[i][j]+" ");
            }
            System.out.println("]");
        }
        System.out.println();
    }
    /**
     * This function takes a 1-dimensional array and converts it to a 2-dimensional array
     */
    public static int[][] trans1DTo2D(int[] arrayIn, int width, int height){
        int[][] arrayOut = new int[height][width];
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                arrayOut[i][j] = arrayIn[(arrayOut[0].length*i)+j];
            }
        }
        return arrayOut;
    }
    public static void performConvolution(String fileNameIn, String fileNameOut, float[][] rulesModel) throws IOException{
        BufferedImage BI = createBI(fileNameIn);
        float[][] r = new float[BI.getWidth()][BI.getHeight()];
        float[][] g = new float[BI.getWidth()][BI.getHeight()];
        float[][] b = new float[BI.getWidth()][BI.getHeight()];
        //Initialize the values of r, g, and b
        Color color;
        for(int w=0; w<BI.getWidth(); w++){
            for(int h=0; h<BI.getHeight(); h++){
                color = new Color(BI.getRGB(w, h));
                r[w][h] = color.getRed();
                g[w][h] = color.getGreen();
                b[w][h] = color.getBlue();
            }
        }
        //Perform the convolution on the colours array individually
        float[][] rFinal = performConvolutionOnArray(rulesModel, r);
        float[][] gFinal = performConvolutionOnArray(rulesModel, g);
        float[][] bFinal = performConvolutionOnArray(rulesModel, b);
        //Combine the colours to make a new image
        BufferedImage finalImage = new BufferedImage(r.length, r[0].length, BufferedImage.TYPE_INT_RGB);
        for(int w=0; w<BI.getWidth(); w++){
            for(int h=0; h<BI.getHeight(); h++){
                color = new Color(r[w][h]/256, g[w][h]/256, b[w][h]/256);
                finalImage.setRGB(w, h, color.getRGB());
                
            }
        }
        File file = new File(fileNameOut);
        ImageIO.write(finalImage, "png", file);
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
    /**
     * This method performs a convolution on 2D-array of float values
     */
    public static float[][] performConvolutionOnArray(float[][] rulesModel, float[][] in){
        float[][] result = new float[in.length][in[0].length];
        float weightRules=0;
        for(int r=0; r<rulesModel.length; r++){
            for(int c=0; c<rulesModel[0].length; c++){
                weightRules = weightRules+rulesModel[r][c];
            }
        }
        for(int counterR=0; counterR<in.length; counterR++){
            for(int counterC=0; counterC<in[0].length; counterC++){
                result[counterR][counterC] = performConvolutionOnPix(rulesModel, weightRules, in, counterR, counterC);
            }
        }
        return result;
    }
    
    public static float performConvolutionOnPix(float[][] rulesModel, float weightRules, float[][] in, int r, int c){
        float result = 0f;
        //Need to loop over each value around the central pixel
        for(int counterR=0; counterR<rulesModel.length; counterR++){
            for(int counterC=0; counterC<rulesModel[0].length; counterC++){
                //To computer the corners and edges, we need to keep in mind that some values around the central onemay not exist
                //We need to determine if these values exist or not
                // If they exist
                if((counterR+r-(rulesModel.length/2)>=0)&&(counterR+r<=in.length)&&(counterC+c-(rulesModel[0].length/2)>=0)&&(counterC+c<=in[0].length)){
                    result = result+in[counterR+r-(rulesModel.length/2)][counterC+c-(rulesModel[0].length/2)]*rulesModel[counterR][counterC];
                }
                //If they do not exist
                else{
                    weightRules = weightRules-rulesModel[counterR][counterC];
                }
            }
        }
        result = result/weightRules;
        return result;
    }
}
