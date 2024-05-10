package edu.vanier.Lumina.tests;

import edu.vanier.Lumina.models.Convolution;

/**
 * Test version of the convolutions algorithm
 * @author 2287559
 */
public class ConvolutionAlgorithmTest{
    public static void main(String[] args) {
        //Input Data
        float[][] data = 
        {   {1,3,1},
            {3,1,3},
            {1,3,1}};
        //Kernel
        float[][] rules = 
        {   {1,2,1},
            {2,3,2},
            {1,2,1}};
        //Print both of them
        Convolution.print2DArray(data);
        Convolution.print2DArray(rules);
        //Get the result using the algorithm
        float[][] result = Convolution.performConvolutionOnArray(rules, data);
        //Print the result
        Convolution.print2DArray(result);
    }
}
