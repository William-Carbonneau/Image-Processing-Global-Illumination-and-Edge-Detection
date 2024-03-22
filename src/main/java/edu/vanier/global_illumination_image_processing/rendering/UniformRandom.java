package edu.vanier.global_illumination_image_processing.rendering;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.MersenneTwister;

/**
 * Random class for UniformRandom numbers in individual threads
 * 
 * @author William Carbonneau
 */
public class UniformRandom {
    // create uniform UniformRandom numbers
    private final MersenneTwister twister;
    private final UniformRealDistribution uniRealDist;
    
    public UniformRandom() {
        this.twister = new MersenneTwister();
        this.uniRealDist = new UniformRealDistribution(this.twister, 0.0, 1.0);
    }
    
    /**
     * get uniform UniformRandom numbers from -1.0 to 1.0
     * 
     * @return double
     */
    public double uniformRand() {
        return 2.0 * uniRealDist.sample() - 1.0;
    }
    
    /**
     * get uniform UniformRandom numbers from 0.0 to 1.0
     * 
     * @return double
     */
    public double uniformRand2() {
        return uniRealDist.sample();
    }
}
