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
        float result = 0;
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
    /*
    public float[][] performConvolutionOnImageObject(float[][] in){
        float[][] result = new float[in.length][in[0].length];
        for(int counterR=0; counterR<in.length; counterR++){
            for(int counterC=0; counterC<in[0].length; counterC++){
                result[counterR][counterC] = performConvolutionOnPix(in, counterR, counterC);
            }
        }
        return result;
    }
    /**
     * This method performs a convolution of pixel, based on the values around it. It has been designed such that the convolution can also be done on the edges.
     */
    /*
    public float performConvolutionOnPix(float[][] in, int r, int c){
        float result=0;
        float temp = totalWeight;
        for(int counterR=0; counterR<rules.length; counterR++){
            for(int counterC=0; counterC<rules[0].length; counterC++){
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
    */
}
