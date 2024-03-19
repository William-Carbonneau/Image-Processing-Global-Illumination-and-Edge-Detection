package edu.vanier.global_illumination_image_processing.rendering;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.MersenneTwister;

/**
 * Halton sequence for Quasi Monte Carlo integration.
 * Generates Quasi-random sequence to use a base for the Monte Carlo integration 
 * Halton sequence is generated using a sequence of prime numbers as a base
 * 
 * TODO add credit for Halton algorithm
 * 
 * @author William Carbonneau <2265724 at edu.vaniercollege.ca>
 */
public class Halton {
    private double currentSequenceValue, inverseBase;
   

    /**
     * Default constructor
     */
    public Halton() {
    }
    
    /**
     * Iteratively calculate Halton sequence value using provided index and base
     * @param index int
     * @param base  int
     */
    public void number(int index, int base) {
        double tempInverseBase = inverseBase = 1.0/base;
        currentSequenceValue = 0.0;
        
        // iterate through the digits of index unitl 0
        while (index > 0) {
            // calculate contribution of Halton sequence
            currentSequenceValue += tempInverseBase * (double) (index%base);
            // normalize sequence by base
            index /= base;
            // adjust contirbution for the next digit
            tempInverseBase *= inverseBase;
        }
    }
    
    /**
     * set the next value of the Halton sequence by incrementing the current value of the sequence
     */
    public void next() {
        // calculate space remaining between the current value and the unit interval [0,1]
        double remainingSpace = 1.0 - currentSequenceValue - 0.0000001;
        
        if (inverseBase < remainingSpace) {
            currentSequenceValue += inverseBase;
        }else{
            // dynamic spacing value to incrment currentSequenceValue
            double stepSize = inverseBase;
            double stepSize2;
            
            // loop until stepSize becomes less than remainingSpace to find a suitable increment for currentSequenceValue
            do {
                stepSize2 = stepSize;
                // reduce remaining space
                stepSize *= inverseBase;
            }while (stepSize >= remainingSpace);
            currentSequenceValue += stepSize2 + stepSize - 1.0;
        }
    }
    
    /**
     * get the value of the Halton Sequence
     * @return value type: double
     */
    public double get() {
        return currentSequenceValue;
    }
    
}
