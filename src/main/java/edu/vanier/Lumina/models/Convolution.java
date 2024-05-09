package edu.vanier.Lumina.models;

import edu.vanier.Lumina.rendering.Intersection;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * This class is a collection of static methods that are useful for
 * convolutions.
 *
 * This source was used to understand what is a convolution, what is a kernel,
 * and how do they go hand in hand:
 * https://youtu.be/C_zFhWdM4ic?si=nDnBCiZYqUB04SMO (Pound, 2015)
 */
public class Convolution {
    /**
     * Value of the height of the image when using the partial convolution
     */
    public static int partialHeight = Integer.MAX_VALUE;
    /**
     * Value of the width of the image when using the partial convolution
     */
    public static int partialWidth = Integer.MAX_VALUE;

    /**
     * This method finds the biggest value (absolute value) in an array
     * This method is useful in convolutions such as Sobel, where the color of the pixel is a gradient relative to the maximum difference captured by the kernel:
     * A negative difference is as important as a positive one.
     * @param array - The input float array
     * @return the biggest value
     */
    private static float findBiggestValue(float[][] array) {
        float max = 0;
        for (float[] array1 : array) {
            for (int j = 0; j < array[0].length; j++) {
                if (max < Math.abs(array1[j])) {
                    max = Math.abs(array1[j]);
                }
            }
        }
        return max;

    }

    /**
     * This method prints a 1-D array
     * Used in the developping stage of the application. 
     * @param array - the array to be printed
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
     * Used in the developping stage of the application.
     * @param array - the array to be printed
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
     * This function takes a 1-dimensional array and converts it to a
     * 2-dimensional array
     * Used in the developping stage of the application.
     * @param arrayIn - The array input to be converted
     * @param width - The width of the output array
     * @param height - The height of the output array
     * @return The 2-d array
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
     * https://ramok.tech/2018/09/27/convolution-in-java/ (Ramo, 2018) 
     * A grayscale pixel is obtained by averaging the r, g, and b values:
     * https://web.stanford.edu/class/cs101/image-6-grayscale-adva.html
     * (Standor.edu, Image-6 grayscale)
     */
    public static void performGrayscale(String filePathIn, String filePathOut) throws IOException {
        BufferedImage original = createBI(filePathIn);
        BufferedImage finalImage = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);
        //Initialize the values of r, g, and b
        float r;
        float g;
        float b;
        //Average of the r,g and b values
        float avg;

        Color color;
        //Loop through all the pixels of the image
        for (int w = 0; w < original.getWidth(); w++) {
            for (int h = 0; h < original.getHeight(); h++) {
                //Get the color of the pixel
                color = new Color(original.getRGB(w, h));
                //Get the r,g,b values
                r = color.getRed();
                g = color.getGreen();
                b = color.getBlue();
                //average the values
                avg = (r + g + b) / 3;
                //set the values of the rgb
                color = new Color(avg / 255, avg / 255, avg / 255);
                //Check for partial convolution
                //Only input convolution on the specfied part of the image
                if ((partialWidth > w && partialHeight > h)) {
                    finalImage.setRGB(w, h, color.getRGB());

                } else {
                    finalImage.setRGB(w, h, original.getRGB(w, h));
                }

            }
        }
        //Create the file and write it
        File file = new File(filePathOut);
        ImageIO.write(finalImage, "bmp", file);

    }

    /**
     * This method finds the angles of the edges created by Sobel edge detection
     * and colors them.
     *
     * @param filePathIn - Path of the input image
     * @param filePathOut - Path of the output image
     * @throws IOException 
     * 
     * This source was used as a reference to use ImageIO in
     * the context of performing a convolution:
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
        //The values of a grayscale image are uniform, meaning that the values for red, blue, and green are all the same
        // Therefore, we can take any one of these three to initialize the array g (g)
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                color = new Color(BI.getRGB(w, h));
                g[w][h] = color.getGreen();
            }
        }
        //Perform the convolution on the gray array, in order to get the final one
        // gFinal contains the floating numbers describing how much the colour values change up to down. 
        // (It does not represent the grayscale value, but the difference in the grayscale)
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

            }
        }

        // Create and write the output file
        File file = new File(filePathOut);
        ImageIO.write(finalImage, "bmp", file);

    }

    /**
     * This method performs Sobel edge detection along the x axis for an image.
     *
     * @param filePathIn- The path of the input file
     * @param filePathOut - The path of the output file
     * @throws IOException This source was used as a reference to use ImageIO in
     * the context of performing a convolution:
     * https://ramok.tech/2018/09/27/convolution-in-java/ (Ramo, 2018)
     */
    public static void performSobelX(String filePathIn, String filePathOut) throws IOException {
        //Instead of using a gradient, we thought that using a threshold would be more effective
        float threshold = 100;
        //Source for the kernel: https://en.wikipedia.org/wiki/Sobel_operator (Sobel operator, 2024)
        float[][] rulesSobelX = {
            {-1, -2, -1},
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
     * This method performs Sobel Edge detection on an image and saves the output image in the output path.
     * Reference to understand the algorithm:
     * https://youtu.be/uihBwtPIBxM?si=W3KaT-ADPo2NBvcW (Pound, 2015)
     *
     * @param filePathIn- The file of the input image
     * @param filePathOut - The file of the output image
     * @throws IOException 
     * This source was used as a reference to use ImageIO in
     * the context of performing a convolution:
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
                //Color this spot with a shade that is proportional to the max gradient
                ratioGradient = sobel[w][h] / maxGradient;
                colorFloat = Math.abs((int) ((int) 255 * ratioGradient));
                try {
                    color = new Color(colorFloat, colorFloat, colorFloat);
                } catch (Exception e) {
                    System.out.println(colorFloat);
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
     * This method performs the Prewitt image convolution on an input file and saves it in the output file path
     * Reference to understand the algorithm:
     * https://youtu.be/uihBwtPIBxM?si=W3KaT-ADPo2NBvcW (Pound, 2015)
     *
     * @param filePathIn - Path of the input image
     * @param filePathOut - Path of the output image
     * 
     * @throws IOException 
     * This source was used as a reference to use ImageIO in
     * the context of performing a convolution:
     * https://ramok.tech/2018/09/27/convolution-in-java/ (Ramo, 2018)
     */
    public static void performPrewittPure(String filePathIn, String filePathOut) throws IOException {

        // source (Prewitt operator, 2024)
        float[][] rulesprewittY = {{1, 1, 1},
        {0, 0, 0},
        {-1, -1, -1}};
        // source (Prewitt operator, 2024)
        float[][] rulesprewittX = {{1, 0, -1},
        {1, 0, -1},
        {1, 0, -1}};

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
        float[][] gradientX = performConvolutionOnArray(rulesprewittX, g);
        float[][] gradientY = performConvolutionOnArray(rulesprewittY, g);
        float[][] prewitt = new float[BI.getWidth()][BI.getHeight()];
        //Make a new image
        BufferedImage finalImage = new BufferedImage(g.length, g[0].length, BufferedImage.TYPE_INT_RGB);
        float maxGradient;//Maximum value that a pixel can return
        float ratioGradient; //ratio between the final gradient and the max gradient
        int colorFloat; //Value of the color that the pixel should have between 0 and 255
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                //Calculate the final gradient using Pythagora
                prewitt[w][h] = (float) Math.sqrt(gradientX[w][h] * gradientX[w][h] + gradientY[w][h] * gradientY[w][h]);
            }
        }
        //We can now find the maximum gradient detected
        maxGradient = findBiggestValue(prewitt);

        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                //color that spot proportionally to the max gradient
                ratioGradient = prewitt[w][h] / maxGradient;
                colorFloat = (int) ((int) 255 * ratioGradient);
                try {
                    color = new Color(colorFloat, colorFloat, colorFloat);
                } catch (Exception e) {
                    System.out.println(colorFloat);
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
     * Performs Colored Sobel convolution, meaning that it is the same as Sobel, just applied with all of the r,g,b values instead of only their average.
     *
     * @param filePathIn- The path of the file to be convolved
     * @param filePathOut - The path in which we save the output image
     * @throws IOException
     * This source was used as a reference to use ImageIO in
     * the context of performing a convolution:
     * https://ramok.tech/2018/09/27/convolution-in-java/ (Ramo, 2018)
     */
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
        //Get the r,g,b values
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                color = new Color(BI.getRGB(w, h));
                g[w][h] = color.getGreen();
                r[w][h] = color.getRed();
                b[w][h] = color.getBlue();
            }
        }
        //Perform the convolution on the all three r,g,b arrays to get the gradients for each color
        float[][] gradientXGreen = performConvolutionOnArray(rulesSobelX, g);
        float[][] gradientYGreen = performConvolutionOnArray(rulesSobelY, g);
        float[][] sobelGreen = new float[BI.getWidth()][BI.getHeight()];
        float[][] gradientXRed = performConvolutionOnArray(rulesSobelX, r);
        float[][] gradientYRed = performConvolutionOnArray(rulesSobelY, r);
        float[][] sobelRed = new float[BI.getWidth()][BI.getHeight()];
        float[][] gradientXBlue = performConvolutionOnArray(rulesSobelX, b);
        float[][] gradientYBlue = performConvolutionOnArray(rulesSobelY, b);
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
        //We can now find the maximum gradients detected
        maxGradientGreen = findBiggestValue(sobelGreen);
        maxGradientRed = findBiggestValue(sobelRed);
        maxGradientBlue = findBiggestValue(sobelBlue);
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                //Get the r,g,b values in proprotion ot the gradients
                ratioGradientGreen = sobelGreen[w][h] / maxGradientGreen;
                ratioGradientRed = sobelRed[w][h] / maxGradientRed;
                ratioGradientBlue = sobelBlue[w][h] / maxGradientBlue;
                colorFloatGreen = (int) ((int) 255 * ratioGradientGreen);
                colorFloatRed = (int) ((int) 255 * ratioGradientRed);
                colorFloatBlue = (int) ((int) 255 * ratioGradientBlue);
                //set the color
                try {
                    color = new Color(colorFloatRed, colorFloatGreen, colorFloatBlue);
                } catch (Exception e) {
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
     * This method applies the laplacian operator kernel on an image. This is
     * the 7x7 version that does not require blurring.
     *
     * @param filePathIn - The path of the input image
     * @param filePathOut - The path in which we want the output image to be saved
     * @throws IOException This source was used as a reference to use ImageIO in
     * the context of performing a convolution:
     * https://ramok.tech/2018/09/27/convolution-in-java/ (Ramo, 2018)
     */
    public static void performLaplacianOperator9x9(String filePathIn, String filePathOut) throws IOException {
        //Source for the kernel: https://homepages.inf.ed.ac.uk/rbf/HIPR2/log.htm (Fisher et al., 2003)
        float[][] laplacianKernel = {
            {0, 1, 1, 2, 2, 2, 1, 1, 0},
            {1, 2, 4, 5, 5, 5, 4, 2, 1},
            {1, 4, 5, 3, 0, 3, 5, 4, 1},
            {2, 5, 3, -12, -24, -12, 3, 5, 2},
            {2, 5, 0, -24, -40, -24, 0, 5, 2},
            {2, 5, 3, -12, -24, -12, 3, 5, 2},
            {1, 4, 5, 3, 0, 3, 5, 4, 1},
            {1, 2, 4, 5, 5, 5, 4, 2, 1},
            {0, 1, 1, 2, 2, 2, 1, 1, 0}};
        //Create the image
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
        float biggestResult = findBiggestValue(laplacianResult);
        float finalColor;
        //Make a new image
        BufferedImage finalImage = new BufferedImage(g.length, g[0].length, BufferedImage.TYPE_INT_RGB);
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                //Set the laplacian as the ratio of the laplacian over the biggest result
                try {
                    finalColor = laplacianResult[w][h] / biggestResult;
                    finalColor = Math.abs(finalColor);
                    color = new Color(finalColor, finalColor, finalColor);
                    finalImage.setRGB(w, h, color.getRGB());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    color = new Color(0, 0, 0);
                    finalImage.setRGB(w, h, color.getRGB());
                }

            }
        }
        // Create and write the output file
        File file = new File(filePathOut);
        ImageIO.write(finalImage, "bmp", file);
    }

    /**
     * This method applies the Laplacian operator kernel on an image. It uses a smaller kernel meaning that we need to blue the image first.
     *
     * @param filePathIn - the file of the input image
     * @param filePathOut - The path in which we want the output image to be saved
     * @throws IOException 
     * This source was used as a reference to use ImageIO in
     * the context of performing a convolution:
     * https://ramok.tech/2018/09/27/convolution-in-java/ (Ramo, 2018)
     */
    public static void performLaplacianOperator3x3(String filePathIn, String filePathOut) throws IOException {
        //Source for the kernel: https://youtu.be/uNP6ZwQ3r6A?si=Lg2Q0SyxrTAA6Qcw (Nayar, 2021)
        float[][] laplacianKernel = {
            {0, -1, 0},
            {-1, 4, -1},
            {0, -1, 0}
        };
        //Create the image
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
        float biggestResult = findBiggestValue(laplacianResult);
        float finalColor;
        //Make a new image
        BufferedImage finalImage = new BufferedImage(g.length, g[0].length, BufferedImage.TYPE_INT_RGB);
        for (int w = 0; w < BI.getWidth(); w++) {
            for (int h = 0; h < BI.getHeight(); h++) {
                //Set the laplacian as the ratio of the laplacian over the biggest result
                try {
                    finalColor = laplacianResult[w][h] / biggestResult;
                    finalColor = Math.abs(finalColor);
                    color = new Color(finalColor, finalColor, finalColor);
                    finalImage.setRGB(w, h, color.getRGB());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    color = new Color(0, 0, 0);
                    finalImage.setRGB(w, h, color.getRGB());

                }

            }
        }
        // Create and write the output file
        File file = new File(filePathOut);
        ImageIO.write(finalImage, "bmp", file);
    }

    /**
     * This method applies the Prewitt operator kernel on an image. This
     * operator is very similar to Sobel, with the exception of the corners.
     *
     * @param filePathIn - The path of the input image
     * @param filePathOut - The path in which we want the output image to be saved
     * @param threshold - The threshold - The minimum value for which a difference needs to equal or exceed in order to be considered an edge.
     * @throws IOException 
     * This source was used as a reference to use ImageIO in
     * the context of performing a convolution:
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
        //Create the image
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
     * @param filePathIn - The path of the input image
     * @param filePathOut - The path in which we want the output image to be saved
     * @throws IOException 
     * This source was used as a reference to use ImageIO in
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
     * This method takes an image input, performs a
     * convolution to it, based on a predefined kernel, and saves the resulting image in the output path. 
     * This source was used as a reference to use ImageIO in the context of performing a convolution
     * (However, the entire algorithm for performing a given convolution on an
     * array was created separately by Loovdrish):
     * https://ramok.tech/2018/09/27/convolution-in-java/ (Ramo, 2018)
     * @param pathNameIn - The path of the input image
     * @param pathNameOut - The path in which we want the output image to be saved
     * @param rulesModel  - The array representing the kernel
     * @throws IOException
     */
    public static void performConvolution(String pathNameIn, String pathNameOut, float[][] rulesModel) throws IOException {
        //Takes the file and creates a BufferedImage
        BufferedImage BI = createBI(pathNameIn);
        //Creates 3 arrays containing the value of green, red and blue for each pixel
        float[][] r = new float[BI.getWidth()][BI.getHeight()];
        float[][] g = new float[BI.getWidth()][BI.getHeight()];
        float[][] b = new float[BI.getWidth()][BI.getHeight()];
        //Initialize the values of r, g, and b (red, green, and blue component of the pixels)
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
                //Check for partial convolution
                //Only input convolution on the specfied part of the image
                if ((partialWidth > w && partialHeight > h)) {
                    finalImage.setRGB(w, h, color.getRGB());

                } else {
                    finalImage.setRGB(w, h, BI.getRGB(w, h));
                }
            }
        }
        //Creates the output file
        File file = new File(pathNameOut);
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
     * @param rulesModel - The array that describes the kernel to be applied
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
        //Return the result
        return result;
    }

    /**
     * This method takes the path to a file and outputs the BufferedImage
     * corresponding to that file.
     *
     * @param filePath - The path to the image file
     * @return BufferedImage - The corresponding BufferedImage
     * @throws IOException 
     * Source:
     * https://ramok.tech/2018/09/27/convolution-in-java/ (Ramo, 2018)
     */
    public static BufferedImage createBI(String filePath) throws IOException {
        File file = new File(filePath);
        return ImageIO.read(new File(file.getAbsolutePath()));
    }
}
