package edu.vanier.Global_Illumination_Image_Processing.models;

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
    
    public static float[][] performConvolutionOnImage(float[][] rulesModel, float[][] in){
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
