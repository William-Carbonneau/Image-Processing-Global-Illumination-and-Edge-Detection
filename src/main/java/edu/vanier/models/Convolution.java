package edu.vanier.models;

/**
 *
 * @author loovd
 */
public class Convolution {
    String name; // name of the convolution
    /**
     * Rules refers to the weight distribution of each pixel
     */
    float[][] rules;
    int numOfRows;
    int numOfCol;
    float totalWeight=0;
    
    public Convolution(String name, float[][] rules){
        
        //name of the convolution
        this.name = name;
        
        //matrix (in the form of a 2-dimensional array) representing the rules of the convolution
        this.rules =rules;
        
        //initialize the number of rows and columns
        numOfRows = rules.length;
        numOfCol = rules[0].length;
        
        //Calculate total weight
        for(int counterR=0; counterR<numOfRows; counterR++){
            for(int counterC=0; counterC<numOfCol; counterC++){    
                totalWeight = totalWeight+rules[counterR][counterC];
            }
        }
        
    }
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
    /**
     * Performs convolution based on the rules
     */
    public float[][] performConvolutionOnImage(float[][] in){
        float[][] result = new float[numOfRows][numOfCol];
        for(int counterR=0; counterR<in.length; counterR++){
            for(int counterC=0; counterC<in[0].length; counterC++){
                result[counterR][counterC] = performConvolutionOnPix(in, counterR, counterC);
            }
        }
        return result;
    }
    /**
     * Most complicated method of the class. Is what needs to be implemented for the code to work correctly.
     */
    public float performConvolutionOnPix(float[][] in, int r, int c){
        float result=0;
        float temp = totalWeight;
        for(int counterR=0; counterR<in.length; counterR++){
            for(int counterC=0; counterC<in[0].length; counterC++){
                //To perform a convolution, we need to determine if the pixels around the central one exist
                //If they exist, we add them according to their weight
                //counterR+r gives the absolute index in terms of rows, and counterC+c
                if((counterR+r-1>=0)&&(counterR+r<=in.length)&&(counterC+c-1>=0)&&(counterC+c<=in[0].length)){
                    //System.out.println("in->"+in[counterR+r-1][counterC+c-1]);
                    result = result+in[counterR+r-1][counterC+c-1]*rules[counterR][counterC];
                }
                //If they do not exist, we divide the totalWeight by the value from rules at that relative location
                else{
                    //System.out.println("out->"+rules[counterR][counterC]);
                    temp = temp-rules[counterR][counterC];
                }
            }
        }
        //System.out.println("========================");
        result = result/temp;
        return result;
    }
}
