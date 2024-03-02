package tests;

import edu.vanier.models.Convolution;

/**
 * Test Class for Convolution
 */
public class ConvolutionTest {
    public static void main(String[] args) {
        float[][] gauss = {
            {1,2,1},
            {2,4,2},
            {1,2,1}
        };
        float[][] matrix={
            {1,2,1,0,1,1,1},
            {1,1,5,0,1,1,1},
            {1,1,0,0,1,1,1}
        };
        float[][] result = Convolution.performConvolutionOnImage(gauss, matrix);
        Convolution.print2DArray(result);
    }
}
