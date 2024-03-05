package edu.vanier.global_illumination_image_processing.rendering;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.distribution.UniformRealDistribution;

/**
 * The main algorithm
 * @author William Carbonneau
 */
public class RenderingEquation {
    // create uniform random numbers
    private static MersenneTwister twister = new MersenneTwister();
    private static UniformRealDistribution uniRealDist = new UniformRealDistribution(twister, 0.0, 1.0);
    
    // width and height of render image/camera in pixels, cannot be static for dynamic adjustment TODO, add getters and setters
    private int width = 800, height = 800;
    
    // get uniform random numbers from -1.0 to 1.0
    public static double uniformRand() {
        return 2.0 * uniRealDist.sample() - 1.0;
    }
    
    // get uniform random numbers from 0.0 to 1.0
    public static double uniformRand2() {
        return uniRealDist.sample();
    }
    
    public Vec3D CamPlaneCoordinate(double x, double y) {
        double w = width;
        double h = height;
        
        double FieldOfViewX = Math.PI/4;
        double FieldOfViewY = (h/w) * FieldOfViewX;
        
        return (new Vec3D(((2*x-w)/w)*Math.tan(FieldOfViewX),-((2*y-h)/h) * Math.tan(FieldOfViewY),-1.0));
    }
    
}
