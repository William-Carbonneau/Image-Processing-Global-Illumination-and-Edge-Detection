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
    
    Convolution(String name, float[][] rules){
        
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
    //Need to do this more carefully
    public float performConvolutionOnPix(float[][] in, int r, int c){
        float result=0;
        for(int counterR=0; counterR<in.length; counterR++){
            for(int counterC=0; counterC<in[0].length; counterC++){
                if(counterR+r<((in.length-1)/2)+1){
                    totalWeight = totalWeight/rules[counterR][counterC];
                }
                else if(counterC+c<((in[0].length-1)/2)+1){
                    totalWeight = totalWeight/rules[counterR][counterC];
                }
                else if(counterR+r>((in.length-1)/2)+1){
                }
                else if(counterC+c>((in[0].length-1)/2)+1){
                }
                else
                    result = result+in[counterR+r-rules.length/2][counterC+c-rules[0].length/2]*rules[counterR][counterC];
            }
        }
        return result;
    }
}
