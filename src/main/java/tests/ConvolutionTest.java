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
        Convolution gaussian = new Convolution("gaussian",gauss);
        float[][] matrix={
            {1,2,1},
            {1,1,5},
            {1,1,0}
        };
        float[][] result = gaussian.performConvolutionOnImage(matrix);
        Convolution.print2DArray(result);
    }
}
