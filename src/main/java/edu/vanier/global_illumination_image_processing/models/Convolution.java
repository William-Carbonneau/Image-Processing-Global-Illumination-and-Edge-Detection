package edu.vanier.global_illumination_image_processing.models;

import edu.vanier.global_illumination_image_processing.rendering.Intersection;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * This class is a collection of static methods that are useful for
 * convolutions.
 * 
 * This source was used to understand what is a convolution, what is a kernel, and how do they go hand in hand: https://youtu.be/C_zFhWdM4ic?si=nDnBCiZYqUB04SMO (Pound, 2015)
 */
public class Convolution {
    /**
     * This method finds the biggest value in an array
     * @param array
     * @return 
     */
    private static float findBiggestValue(float[][] array) {
        float max = 0;
        for (float[] array1 : array) {
            for (int j = 0; j<array[0].length; j++) {
                if (max < array1[j]) {
                    max = array1[j];
                }
            }
        }
        return max;
        
    }

    

    // Source for the kernel to implement: https://youtu.be/C_zFhWdM4ic?si=CH3JvuO9mSfVmleJ (Pound, 2015)
    private static final float[][] rulesGaussian = {{1, 2, 1}, {2, 4, 2}, {1, 2, 1}};
    //Source for the kernel to implement: https://pro.arcgis.com/en/pro-app/latest/help/analysis/raster-functions/convolution-function.htm#:~:text=The%20Convolution%20function%20performs%20filtering,or%20other%20kernel%2Dbased%20enhancements. (Esri)
    private static final float[][] rulesSharp1 = {{0f, -1f, 0f}, {-1f, 5f, -1f}, {0f, -1f, 0f}};

    /**
     * This method prints a 1-D array
     *
     * @param array
     */
    public static void print1DArray(int[] array) {
        System.out.print("[");
        for (int counter = 0; counter < array.length; counter++) {
            System.out.print(array[counter] + " ");
        }
        System.out.println("]");
    }

    /**
     * This method prints a 2-D array
     *
     * @param array
     */
    public static void print2DArray(float[][] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print("[");
            for (int j = 0; j < array[0].length; j++) {
                System.out.print(array[j][i] + " ");
            }
            System.out.println("]");
        }
        System.out.println();
    }

    /**
     * This method prints a 2-d array
     */
    public static void print2DArray(byte[][] array) {
        for (int i = 0; i < array[0].length; i++) {
            System.out.print("[");
            for (int j = 0; j < array.length; j++) {
                System.out.print(array[j][i] + " ");
            }
            System.out.println("]");
        }
        System.out.println();
    }

    /**
     * This method prints a 2-d array
     */
    public static void print2DArray(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print("[");
            for (int j = 0; j < array[0].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println("]");
        }
        System.out.println();
    }

    /**
     * This function takes a 1-dimensional array and converts it to a
     * 2-dimensional array
     *
     * @param arrayIn
     * @param width
     * @param height
     * @return
     */
    public static int[][] trans1DTo2D(int[] arrayIn, int width, int height) {
        int[][] arrayOut = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                arrayOut[i][j] = arrayIn[(arrayOut[0].length * i) + j];
            }
        }
        return arrayOut;
    }

    /**
     * This method takes an input image and outputs its grayscale.
     *
     * @param filePathIn - The path of the file input
     * @param filePathOut - The path of the file output
     * @throws IOException 
     * Source used as a reference to use ImageIO:
     * https://ramok.tech/2018/09/27/convolution-in-java/  (Ramo, 2018)
     * A grayscale pixel is obtained by averaging the r, g, and b values:
     * https://web.stanford.edu/class/cs101/image-6-grayscale-adva.html (Standor.edu, Image-6 grayscale)
     */
    public static void performGrayscale(String filePathIn, String filePathOut) throws IOException {
        BufferedImage BI = createBI(filePathIn);
        BufferedImage finalImage = new BufferedImage(BI.getWidth(), BI.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        //Initialize the values of r, g, and b
        float r;
        float g;
        float b;
        float avg;
        Color color;
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                color = new Color(BI.getRGB(w, h));
                r = color.getRed();
                g = color.getGreen();
                b = color.getBlue();
                avg = (r + g + b) / 3;
                color = new Color(avg / 255, avg / 255, avg / 255);
                finalImage.setRGB(w, h, color.getRGB());
            }
        }
        File file = new File(filePathOut);
        ImageIO.write(finalImage, "bmp", file);

    }

    /**
     * This method finds the angles of the edges created by Sobel edge detection
     * and colors them.
     *
     * @param filePathIn
     * @param filePathOut
     * @throws IOException 
     * This source was used as a reference to use ImageIO in the context of performing a convolution:
     * https://ramok.tech/2018/09/27/convolution-in-java/ (Ramo, 2018)
     */
    public static void performEdgeAngles(String filePathIn, String filePathOut) throws IOException {
        //Source for the kernel: https://en.wikipedia.org/wiki/Sobel_operator (Sobel operator, 2024)
        float[][] rulesSobelX = {{-1, -2, -1},
        {0, 0, 0},
        {1, 2, 1}};
        //Source for the kernel: https://en.wikipedia.org/wiki/Sobel_operator (Sobel operator, 2024)
        float[][] rulesSobelY = {{-1, 0, 1},
        {-2, 0, 2},
        {-1, 0, 1}};
        BufferedImage BI = createBI(filePathIn);
        // Create the array gray corresponding to the average values of the pixels
        float[][] g = new float[BI.getWidth()][BI.getHeight()];
        //Initialize the values of g
        Color color;
        //The values of a geayscale image are uniform, meaning that the values for red, blue, and green are all the same
        // Therefore, we can take any one of these three to initialize the array g (g)
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                color = new Color(BI.getRGB(w, h));
                g[w][h] = color.getGreen();
            }
        }
        //Perform the convolution on the gray array, in order to get the final one
        // gFinal contains the floating numbers describing how much the colour values change up to down. (It does not represent the grayscale value, but the difference in the grayscale)
        float[][] gradientX = performConvolutionOnArray(rulesSobelX, g);
        float[][] gradientY = performConvolutionOnArray(rulesSobelY, g);
        //Find the angles of the edges with gradient X and gradient Y
        //Create image with colored angles
        BufferedImage finalImage = new BufferedImage(g.length, g[0].length, BufferedImage.TYPE_INT_RGB);
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                //color = new Color(Color.HSBtoRGB((float) Math.toDegrees( Math.atan(gradientY[w][h]/gradientX[w][h])), 1 , 1));
                if (gradientX[w][h] < Intersection.EPS || gradientY[w][h] < Intersection.EPS) {
                    finalImage.setRGB(w, h, Color.BLACK.getRGB());

                } else {
                    finalImage.setRGB(w, h, Color.HSBtoRGB((float) Math.toDegrees(Math.atan(gradientY[w][h] / gradientX[w][h])), 1, 1));
                }
                //finalImage.setRGB((int) Math.abs((Math.atan(gradientY[w][h]/gradientX[w][h])/(2*Math.PI)*255)),0, 0);

            }
        }

        // Create and write the output file
        File file = new File(filePathOut);
        ImageIO.write(finalImage, "bmp", file);

    }

    /**
     * This method performs Sobel edge detection along the x axis for an image.
     *
     * @param filePathIn
     * @param filePathOut
     * @throws IOException 
     * This source was used as a reference to use ImageIO in the context of performing a convolution:
     * https://ramok.tech/2018/09/27/convolution-in-java/ (Ramo, 2018)
     */
    public static void performSobelX(String filePathIn, String filePathOut) throws IOException {
        float threshold = 100;
        //Source for the kernel: https://en.wikipedia.org/wiki/Sobel_operator (Sobel operator, 2024)
        float[][] rulesSobelX = {{-1, -2, -1},
        {0, 0, 0},
        {1, 2, 1}};
        BufferedImage BI = createBI(filePathIn);
        // Create the array gray corresponding to the average values of the pixels
        float[][] g = new float[BI.getWidth()][BI.getHeight()];
        //Initialize the values of g
        Color color;
        //The values of a grayscale image are uniform, meaning that the values for red, blue, and green are all the same
        // Therefore, we can take any one of these three to initialize the array g (g)
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                color = new Color(BI.getRGB(w, h));
                g[w][h] = color.getGreen();
            }
        }
        //Perform the convolution on the gray array, in order to get the final one
        // gFinal contains the floating numbers describing how much the colour values change to its left and right. (It does not represent the grayscale value, but the difference in the grayscale)
        float[][] gFinal = performConvolutionOnArray(rulesSobelX, g);
        //Make a new image
        BufferedImage finalImage = new BufferedImage(g.length, g[0].length, BufferedImage.TYPE_INT_RGB);
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                //If the difference is bigger than the threshold, color that spot white
                if (gFinal[w][h] > threshold) {
                    color = new Color(255, 255, 255);
                } //If not, colour it black
                else {
                    color = new Color(0, 0, 0);
                }
                //Set the value of the colour
                finalImage.setRGB(w, h, color.getRGB());
            }
        }
        // Create and write the output file
        File file = new File(filePathOut);
        ImageIO.write(finalImage, "bmp", file);
    }

    /**
     * Reference to understand the algorithm:
     * https://youtu.be/uihBwtPIBxM?si=W3KaT-ADPo2NBvcW (Pound, 2015)
     *
     * @param filePathIn
     * @param filePathOut
     * @param threshold
     * @throws IOException
     * This source was used as a reference to use ImageIO in the context of performing a convolution:
     * https://ramok.tech/2018/09/27/convolution-in-java/ (Ramo, 2018)
     */
    public static void performSobel(String filePathIn, String filePathOut) throws IOException {

        //Source for the kernel: https://en.wikipedia.org/wiki/Sobel_operator (Sobel operator, 2024)
        float[][] rulesSobelX = {{-1, -2, -1},
                                 {0, 0, 0},
                                 {1, 2, 1}};
        //Source for the kernel: https://en.wikipedia.org/wiki/Sobel_operator (Sobel operator, 2024)
        float[][] rulesSobelY = {{-1, 0, 1},
                                 {-2, 0, 2},
                                 {-1, 0, 1}};
        BufferedImage BI = createBI(filePathIn);
        // Create the array gray corresponding to the average values of the pixels
        float[][] g = new float[BI.getWidth()][BI.getHeight()];
        //Initialize the values of g
        Color color;
        //The values of a geayscale image are uniform, meaning that the values for red, blue, and green are all the same
        // Therefore, we can take any one of these three to initialize the array g (g)
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                color = new Color(BI.getRGB(w, h));
                g[w][h] = color.getGreen();
            }
        }
        //Perform the convolution on the gray array, in order to get the final one
        // gFinal contains the floating numbers describing how much the colour values change up to down. (It does not represent the grayscale value, but the difference in the grayscale)
        float[][] gradientX = performConvolutionOnArray(rulesSobelX, g);
        float[][] gradientY = performConvolutionOnArray(rulesSobelY, g);
        float[][] sobel = new float[BI.getWidth()][BI.getHeight()];
        //Make a new image
        BufferedImage finalImage = new BufferedImage(g.length, g[0].length, BufferedImage.TYPE_INT_RGB);
        float maxGradient;//Maximum value that a pixel can return
        float ratioGradient; //ratio between the final gradient and the max gradient
        int colorFloat; //Value of the color that the pixel should have between 0 and 255
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                //Calculate the final gradient using Pythagora
                sobel[w][h] = (float) Math.sqrt(gradientX[w][h] * gradientX[w][h] + gradientY[w][h] * gradientY[w][h]);
            }
        }
        //We can now find the maximum gradient detected
        maxGradient = findBiggestValue(sobel);
        System.out.println(maxGradient);
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                //If the difference is bigger than the threshold, color that spot white
                ratioGradient = sobel[w][h]/maxGradient;
                colorFloat = (int)((int) 255*ratioGradient);
                try{
                    color = new Color(colorFloat, colorFloat, colorFloat);
                }catch(Exception e){
                    System.out.println(colorFloat);
                    color = new Color(0,0,0);
                }
                //Set the value of the colour
                finalImage.setRGB(w, h, color.getRGB());
            }
        }
        // Create and write the output file
        File file = new File(filePathOut);
        ImageIO.write(finalImage, "bmp", file);

    }
    public static void performSobelColored(String filePathIn, String filePathOut) throws IOException {
        
        //Source for the kernel: https://en.wikipedia.org/wiki/Sobel_operator (Sobel operator, 2024)
        float[][] rulesSobelX = {{-1, -2, -1},
                                 {0, 0, 0},
                                 {1, 2, 1}};
        //Source for the kernel: https://en.wikipedia.org/wiki/Sobel_operator (Sobel operator, 2024)
        float[][] rulesSobelY = {{-1, 0, 1},
                                 {-2, 0, 2},
                                 {-1, 0, 1}};
        BufferedImage BI = createBI(filePathIn);
        // Create the array gray corresponding to the average values of the pixels
        float[][] g = new float[BI.getWidth()][BI.getHeight()];
        float[][] r = new float[BI.getWidth()][BI.getHeight()];
        float[][] b = new float[BI.getWidth()][BI.getHeight()];
        //Initialize the values of g
        Color color;
        //The values of a geayscale image are uniform, meaning that the values for red, blue, and green are all the same
        // Therefore, we can take any one of these three to initialize the array g (g)
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                color = new Color(BI.getRGB(w, h));
                g[w][h] = color.getGreen();
                r[w][h] = color.getRed();
                b[w][h] = color.getBlue();
            }
        }
        //Perform the convolution on the gray array, in order to get the final one
        // gFinal contains the floating numbers describing how much the colour values change up to down. (It does not represent the grayscale value, but the difference in the grayscale)
        float[][] gradientXGreen = performConvolutionOnArray(rulesSobelX, g);
        float[][] gradientYGreen = performConvolutionOnArray(rulesSobelY, g);
        float[][] sobelGreen = new float[BI.getWidth()][BI.getHeight()];
        float[][] gradientXRed = performConvolutionOnArray(rulesSobelX, r);
        float[][] gradientYRed = performConvolutionOnArray(rulesSobelY, r);
        float[][] sobelRed = new float[BI.getWidth()][BI.getHeight()];
        float[][] gradientXBlue = performConvolutionOnArray(rulesSobelX, b);
        float[][] gradientYBlue = performConvolutionOnArray(rulesSobelY,b);
        float[][] sobelBlue = new float[BI.getWidth()][BI.getHeight()];
        //Make a new image
        BufferedImage finalImage = new BufferedImage(g.length, g[0].length, BufferedImage.TYPE_INT_RGB);
        float maxGradientGreen;//Maximum value that a green pixel can return
        float maxGradientRed;//Maximum value that a red pixel can return
        float maxGradientBlue;//Maximum value that a blue pixel can return
        float ratioGradientGreen; //ratio between the final gradient and the max gradient
        float ratioGradientRed; //ratio between the final gradient and the max gradient
        float ratioGradientBlue; //ratio between the final gradient and the max gradient
        int colorFloatGreen; //Value of the color that the pixel should have between 0 and 255
        int colorFloatRed; //Value of the color that the pixel should have between 0 and 255
        int colorFloatBlue; //Value of the color that the pixel should have between 0 and 255
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                //Calculate the final gradient using Pythagora
                sobelGreen[w][h] = (float) Math.sqrt(gradientXGreen[w][h] * gradientXGreen[w][h] + gradientYGreen[w][h] * gradientYGreen[w][h]);
                sobelBlue[w][h] = (float) Math.sqrt(gradientXBlue[w][h] * gradientXBlue[w][h] + gradientYBlue[w][h] * gradientYBlue[w][h]);
                sobelRed[w][h] = (float) Math.sqrt(gradientXRed[w][h] * gradientXRed[w][h] + gradientYRed[w][h] * gradientYRed[w][h]);
            }
        }
        //We can now find the maximum gradient detected
        maxGradientGreen = findBiggestValue(sobelGreen);
        maxGradientRed = findBiggestValue(sobelRed);
        maxGradientBlue = findBiggestValue(sobelBlue);
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                ratioGradientGreen = sobelGreen[w][h]/maxGradientGreen;
                ratioGradientRed = sobelRed[w][h]/maxGradientRed;
                ratioGradientBlue = sobelBlue[w][h]/maxGradientBlue;
                colorFloatGreen = (int)((int) 255*ratioGradientGreen);
                colorFloatRed = (int)((int) 255*ratioGradientRed);
                colorFloatBlue = (int)((int) 255*ratioGradientBlue);
                try{
                    color = new Color(colorFloatRed, colorFloatGreen, colorFloatBlue);
                }catch(Exception e){
                    color = new Color(0,0,0);
                }
                //Set the value of the colour
                finalImage.setRGB(w, h, color.getRGB());
            }
        }
        // Create and write the output file
        File file = new File(filePathOut);
        ImageIO.write(finalImage, "bmp", file);
    }
    /**
     * This method applies the laplacian operator kernel on an image
     * @param filePathIn
     * @param filePathOut
     * @throws IOException 
     * This source was used as a reference to use ImageIO in the context of performing a convolution:
     * https://ramok.tech/2018/09/27/convolution-in-java/ (Ramo, 2018)
     */
    public static void performLaplacianOperator(String filePathIn, String filePathOut) throws IOException {
        //Source for the kernel: https://youtu.be/uNP6ZwQ3r6A?si=Lg2Q0SyxrTAA6Qcw (Nayar, 2021)
        float[][] laplacianKernel = {{0, 1, 0},
        {1, -4, 1},
        {0, 1, 0}};
        BufferedImage BI = createBI(filePathIn);
        // Create the array gray corresponding to the average values of the pixels
        float[][] g = new float[BI.getWidth()][BI.getHeight()];
        //Initialize the values of g
        Color color;
        //The values of a geayscale image are uniform, meaning that the values for red, blue, and green are all the same
        // Therefore, we can take any one of these three to initialize the array g (g)
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                color = new Color(BI.getRGB(w, h));
                g[w][h] = color.getGreen();
            }
        }
        //Perform the convolution on the gray array, in order to get the final one
        // gFinal contains the floating numbers describing how much the colour values change up to down. (It does not represent the grayscale value, but the difference in the grayscale)
        float[][] laplacianResult = performConvolutionOnArray(laplacianKernel, g);
        //Make a new image
        BufferedImage finalImage = new BufferedImage(g.length, g[0].length, BufferedImage.TYPE_INT_RGB);
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                if (laplacianResult[w][h] != 0) {
                    color = new Color(0, 0, 0);
                } else {
                    color = new Color(255, 255, 255);
                }

                finalImage.setRGB(w, h, color.getRGB());
            }
        }
        // Create and write the output file
        File file = new File(filePathOut);
        ImageIO.write(finalImage, "bmp", file);
    }
    /**
     * This method applies the Prewitt operator kernel on an image. This operator is very similar to Sobel, with the exception of the corners.
     * @param filePathIn
     * @param filePathOut
     * @param threshold
     * @throws IOException 
     * This source was used as a reference to use ImageIO in the context of performing a convolution:
     * https://ramok.tech/2018/09/27/convolution-in-java/ (Ramo, 2018)
     */
    public static void performPrewitt(String filePathIn, String filePathOut, float threshold) throws IOException {
        //Source for the kernel: https://en.wikipedia.org/wiki/Prewitt_operator (Prewitt operator, 2024)
        float[][] rulesPrewittX = {{-1, -1, -1},
        {0, 0, 0},
        {1, 1, 1}};
        //Source for the kernel: https://en.wikipedia.org/wiki/Prewitt_operator (Prewitt operator, 2024)
        float[][] rulesPrewittY = {{-1, 0, 1},
        {-1, 0, 1},
        {-1, 0, 1}};
        BufferedImage BI = createBI(filePathIn);
        // Create the array gray corresponding to the average values of the pixels
        float[][] g = new float[BI.getWidth()][BI.getHeight()];
        //Initialize the values of g
        Color color;
        //The values of a geayscale image are uniform, meaning that the values for red, blue, and green are all the same
        // Therefore, we can take any one of these three to initialize the array g (g)
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                color = new Color(BI.getRGB(w, h));
                g[w][h] = color.getGreen();
            }
        }
        //Perform the convolution on the gray array, in order to get the final one
        // gFinal contains the floating numbers describing how much the colour values change up to down. (It does not represent the grayscale value, but the difference in the grayscale)
        float[][] gradientX = performConvolutionOnArray(rulesPrewittX, g);
        float[][] gradientY = performConvolutionOnArray(rulesPrewittY, g);
        //Make a new image
        BufferedImage finalImage = new BufferedImage(g.length, g[0].length, BufferedImage.TYPE_INT_RGB);
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                //Calculate the final gradient using Pythagora
                float finalGradient = (float) Math.sqrt(gradientX[w][h] * gradientX[w][h] + gradientY[w][h] * gradientY[w][h]);
                //If the difference is bigger than the threshold, color that spot white
                if (finalGradient > threshold) {
                    color = new Color(255, 255, 255);
                } // If not, colour it black
                else {
                    color = new Color(0, 0, 0);
                }
                //Set the value of the colour
                finalImage.setRGB(w, h, color.getRGB());
            }
        }
        // Create and write the output file
        File file = new File(filePathOut);
        ImageIO.write(finalImage, "bmp", file);
    }

    /**
     * This method performs Sobel edge detection along the y axis for an image.
     *
     * @param filePathIn
     * @param filePathOut
     * @throws IOException This source was used as a reference to use ImageIO in
     * the context of performing a convolution:
     * https://ramok.tech/2018/09/27/convolution-in-java/ (Ramo, 2018)
     */
    public static void performSobelY(String filePathIn, String filePathOut) throws IOException {
        float threshold = 100;
        //Source for the kernel: https://en.wikipedia.org/wiki/Sobel_operator (Sobel operator, 2024)
        float[][] rulesSobelY = {{-1, 0, 1},
        {-2, 0, 2},
        {-1, 0, 1}};
        BufferedImage BI = createBI(filePathIn);
        // Create the array gray corresponding to the average values of the pixels
        float[][] g = new float[BI.getWidth()][BI.getHeight()];
        //Initialize the values of g
        Color color;
        //The values of a geayscale image are uniform, meaning that the values for red, blue, and green are all the same
        // Therefore, we can take any one of these three to initialize the array g (g)
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                color = new Color(BI.getRGB(w, h));
                g[w][h] = color.getGreen();
            }
        }
        //Perform the convolution on the gray array, in order to get the final one
        // gFinal contains the floating numbers describing how much the colour values change up to down. (It does not represent the grayscale value, but the difference in the grayscale)
        float[][] gFinal = performConvolutionOnArray(rulesSobelY, g);
        //Make a new image
        BufferedImage finalImage = new BufferedImage(g.length, g[0].length, BufferedImage.TYPE_INT_RGB);
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                //If the difference is bigger than the threshold, color that spot white
                if (gFinal[w][h] > threshold) {
                    color = new Color(255, 255, 255);
                } // If not, colour it black
                else {
                    color = new Color(0, 0, 0);
                }
                //Set the value of the colour
                finalImage.setRGB(w, h, color.getRGB());
            }
        }
        // Create and write the output file
        File file = new File(filePathOut);
        ImageIO.write(finalImage, "bmp", file);
    }

    /**
     * This method detects all edges of an input image, and outputs the result
     * outlining the edges of the image
     *
     * @param filePathIn - The path of the path input
     * @param filePathOut - The path of the file output
     * @throws IOException 
     * This source was used as a reference to use ImageIO in the context of performing a convolution:
     * https://ramok.tech/2018/09/27/convolution-in-java/ (Ramo, 2018)
     */
    public static void mergeSobels(String filePathIn, String filePathOut) throws IOException {
        float threshold = 100;
        //Source for the kernel: https://en.wikipedia.org/wiki/Sobel_operator (Sobel operator, 2024)
        float[][] rulesSobelY = {{-1, 0, 1},
        {-2, 0, 2},
        {-1, 0, 1}};
        //Source for the kernel: https://en.wikipedia.org/wiki/Sobel_operator (Sobel operator, 2024)
        float[][] rulesSobelX = {{-1, -2, -1},
        {0, 0, 0},
        {1, 2, 1}};
        BufferedImage BI = createBI(filePathIn);
        //Create the array corresponding to the differences around the central pixel for the grayscale
        float[][] g = new float[BI.getWidth()][BI.getHeight()];
        //Initialize the values of g
        Color color;
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                color = new Color(BI.getRGB(w, h));
                g[w][h] = color.getGreen();
            }
        }
        //Perform the convolution on the colours array individually
        float[][] gFinalX = performConvolutionOnArray(rulesSobelX, g);
        float[][] gFinalY = performConvolutionOnArray(rulesSobelY, g);
        //Combine the sobels to make a new image
        BufferedImage finalImage = new BufferedImage(g.length, g[0].length, BufferedImage.TYPE_INT_RGB);
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                if (gFinalX[w][h] > threshold) {
                    color = new Color(255, 255, 255);
                } else if (gFinalY[w][h] > threshold) {
                    color = new Color(255, 255, 255);
                } else {
                    color = new Color(0, 0, 0);
                }
                finalImage.setRGB(w, h, color.getRGB());
            }
        }
        //Create and write the final image
        File file = new File(filePathOut);
        ImageIO.write(finalImage, "bmp", file);
    }

    /**
     * @param fileNameIn
     * @param fileNameOut
     * @param rulesModel This method takes an image input, performs a
     * convolution to it, and outputs the image output This source was used as a
     * reference to use ImageIO in the context of performing a convolution
     * (However, the entire algorithm for performing a given convolution on an
     * array was created separately by Loovdrish):
     * https://ramok.tech/2018/09/27/convolution-in-java/ (Ramo, 2018)
     * @throws IOException
     */
    public static void performConvolution(String fileNameIn, String fileNameOut, float[][] rulesModel) throws IOException {
        //Takes the file and creates a BufferedImage
        BufferedImage BI = createBI(fileNameIn);
        //Creates 3 arrays containing the value of green, red and blue for each pixel
        float[][] r = new float[BI.getWidth()][BI.getHeight()];
        float[][] g = new float[BI.getWidth()][BI.getHeight()];
        float[][] b = new float[BI.getWidth()][BI.getHeight()];
        //Initialize the values of r, g, and b
        Color color;
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
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
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                try {
                    color = new Color(rFinal[w][h] / 256, gFinal[w][h] / 256, bFinal[w][h] / 256);
                } catch (Exception e) {
                    color = new Color(Math.abs(rFinal[w][h]) > 256 ? 1 : 0, Math.abs(gFinal[w][h]) > 256 ? 1 : 0, Math.abs(bFinal[w][h]) > 256 ? 1 : 0);
                }
                finalImage.setRGB(w, h, color.getRGB());
            }
        }
        //Creates the output file
        File file = new File(fileNameOut);
        //Writes the output file with the data of the BufferedImage
        ImageIO.write(finalImage, "bmp", file);
    }

    /**
     * This method applies a kernel to an array.
     *
     * @param rulesModel - The 2-dimensional array modeling the kernel
     * @param in - The array on which we want to apply to convolution
     * @return result - The output array, after the convolution has been done on
     * the input array
     */
    public static float[][] performConvolutionOnArray(float[][] rulesModel, float[][] in) {
        //Create the output array
        float[][] result = new float[in.length][in[0].length];
        // The addition of all the values in the rules array => Is used for the averaging the value of the pixel
        float weightRules = 0;
        // Calculate the weight of the rules (The sum on all the values in rules)
        for (int r = 0; r < rulesModel.length; r++) {
            for (int c = 0; c < rulesModel[0].length; c++) {
                weightRules = weightRules + rulesModel[r][c];
            }
        }
        // Setting the values of the result array, by applying the kernel on each value
        for (int counterR = 0; counterR < in.length; counterR++) {
            for (int counterC = 0; counterC < in[0].length; counterC++) {
                result[counterR][counterC] = performConvolutionOnPix(rulesModel, weightRules, in, counterR, counterC);
            }
        }
        //Return the result array
        return result;
    }

    /**
     * This method is the unit method for the convolutions. It returns the value
     * at x,y coordinates that the final array should have based on a rules
     * array, an input array, and the location of the value of interest.
     *
     * @param rulesModel - The array that described the kernel to be applied
     * @param weightRules - The sum of all the values in the kernel
     * @param in - The input array, which contains the central and neighbouring
     * values on the array
     * @param r - The value of the row
     * @param c - The value of the column
     * @return floating number which corresponds to the value of the pixel after
     * the kernel has been applied on the central and neighbouring values.
     */
    public static float performConvolutionOnPix(float[][] rulesModel, float weightRules, float[][] in, int r, int c) {
        float result = 0f;
        //Need to loop over each value around the central pixel
        for (int counterR = 0; counterR < rulesModel.length; counterR++) {
            for (int counterC = 0; counterC < rulesModel[0].length; counterC++) {
                //To computer the corners and edges, we need to keep in mind that some values around the central one may not exist
                //We need to determine if these values exist or not
                // If they exist
                if ((counterR + r - (rulesModel.length / 2) >= 0) && (counterR + r <= in.length) && (counterC + c - (rulesModel[0].length / 2) >= 0) && (counterC + c <= in[0].length)) {
                    result = result + in[counterR + r - (rulesModel.length / 2)][counterC + c - (rulesModel[0].length / 2)] * rulesModel[counterR][counterC];
                } //If they do not exist
                else {
                    //We need to substract this value from the weight rules so that it does not impact the averaging
                    weightRules = weightRules - rulesModel[counterR][counterC];
                }
            }
        }
        //Some kernels (like the ones used for sobel) have a total weight of 0. Hence, we need to make sure that the division does not happen
        // This will not impact the accuracy of the method, because such methods are complete without the final division
        if (weightRules != 0) {
            result = result / weightRules;
        }
        return result;
    }

    /**
     * This method takes the path to a file and outputs the BufferedImage
     * corresponding to that file.
     *
     * @param filePath - The path to the image file
     * @return BufferedImage - The corresponding BufferedImage
     * @throws IOException 
     * Source: https://ramok.tech/2018/09/27/convolution-in-java/ (Ramo, 2018)
     */
    public static BufferedImage createBI(String filePath) throws IOException {
        File file = new File(filePath);
        return ImageIO.read(new File(file.getAbsolutePath()));
    }
}
