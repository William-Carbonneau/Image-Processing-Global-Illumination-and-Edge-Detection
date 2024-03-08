package edu.vanier.global_illumination_image_processing.rendering;
import java.util.HashMap;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.distribution.UniformRealDistribution;

/**
 * The main algorithm
 * @author William Carbonneau
 */
public class RenderingEquation {
    // create uniform random numbers
    private static final MersenneTwister twister = new MersenneTwister();
    private static final UniformRealDistribution uniRealDist = new UniformRealDistribution(twister, 0.0, 1.0);
    private static final HashMap<String, Double> parameterList = new HashMap<>(); 
    
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
        
        return (new Vec3D(((2*x - w) / w)*Math.tan(FieldOfViewX), -((2*y - h) / h) * Math.tan(FieldOfViewY), -1.0));
    }
    
    // stratified sampling of  hemisphere from http://www.rorydriscoll.com/2009/01/07/better-sampling/
    // takes random radius and angle as a parameter
    // This ensures an even ray distribution from the hemisphere to reduce noise in Monte Carlo
    Vec3D Hemisphere(double randRadius, double randAngle) {
        double radius = Math.sqrt(1.0 - (randRadius*randAngle));
        double angle = 2 * Math.PI * randAngle;
        return new Vec3D(Math.cos(angle) * radius, Math.sin(angle) * radius, randRadius);
    }
    
    void trace(Ray ray, Scene scene, int recursionDepth, DiffuseColor color, HashMap<String, Double> parameterList, Halton halton1, Halton halton2) {
        // russian roulette recursion
        double rouletteFactor = 1.0;
        
        // exit condition
        if (recursionDepth >= 5) {
            double rouletteStopProbability = 0.1;
            if (uniformRand2() <= rouletteStopProbability) {
                // if we meet the exit probablilistic condition return from recursion
                return;
            }
            // adjust the roulette factor for weighting results
            rouletteFactor = 1.0 / (1.0 - rouletteStopProbability);
        }
        
        Intersection intersect = scene.intersect(ray);
        // check in interscetion exists, otherwise return
        if(!intersect.containsObjectBool()) return;
        // at this point we are sure to have an intersection
        
        
        //// trace a ray to the nearest intersction point then bounce
        Vec3D hitPoint = ray.getOrigin().add(ray.getDirection().multiply(intersect.getIntersectDistance()));
        // get the object normal at the intersection point
        Vec3D normal = intersect.getObject().normal(hitPoint);
        // set the ray otigin to the hitpoint for the next iteration outward
        ray.setOrigin(hitPoint);
        
        // next handle emission from rendering equation [Le(x,w)], weighted by the roulette probability weight
        double emission = intersect.getObject().getEmission();
        color = color.add(new DiffuseColor(emission, emission, emission)).multiply(rouletteFactor);
        
        // calculate the diffuse color by the hemisphere sampler
        // type 1 is diffuse material
        if(intersect.getObject().getType() == 1) {
            // create an orthonormal system from the surface normal
            Vec3D rotationX = new Vec3D(0,0,0), rotationY = new Vec3D(0,0,0);
            Vec3D.orthonormalSystem(normal, rotationX, rotationY);
            // get new direction from hemisphere sampler
            Vec3D sampleDirection = Hemisphere(uniformRand2(),uniformRand2());
            
            // gte new rotated ray direction from orthonormal system
            Vec3D rotatedDirection = new Vec3D(
                new Vec3D(rotationX.getX(), rotationY.getX(), normal.getX()).dot(sampleDirection),
                new Vec3D(rotationX.getY(), rotationY.getY(), normal.getY()).dot(sampleDirection),
                new Vec3D(rotationX.getZ(), rotationY.getZ(), normal.getZ()).dot(sampleDirection)
            );
            ray.setDirection(rotatedDirection);
            double cosineDirection = ray.getDirection().dot(normal);
            
            // create temporary color for recursive call-back
            DiffuseColor tempColor = new DiffuseColor(0,0,0);
            
            // call recursive trace
            trace(ray,scene,recursionDepth+1,tempColor,parameterList,halton1,halton2);
            
            // recursive collection/aggregation
            color = color.add(tempColor.multiplyColor(intersect.getObject().getColor()).multiply(cosineDirection * 0.1 * rouletteFactor));
        }
        
        // calculate the specular component of the rendering, handle uniquely because specular has a perfect bounce.
        // type 2 is specular material
        if (intersect.getObject().getType() == 2) {
            double cosineDirection = ray.getDirection().dot(normal);
            ray.setDirection(ray.getDirection().subtract(normal.multiply(cosineDirection*2)).norm());
            
            // create temporary color for recursive call-back
            DiffuseColor tempColor = new DiffuseColor(0,0,0);
            
            // call recursive trace
            trace(ray,scene,recursionDepth+1,tempColor,parameterList,halton1,halton2);
            
            // recursive collection/aggregation
            color = color.add(tempColor.multiply(rouletteFactor));
        }
        
        // handle glass and refractive materials using snell's law and Fresnel's law
        // Calculate probability weights and outgoing direction        
        // type 3 is refractive
        if (intersect.getObject().getType() == 3) {
            // set up physical variables
            double rIndex = parameterList.get("refractiveIndex");
            double ratio = (1.0-rIndex)/(1.0+rIndex);
            ratio = ratio*ratio;
            
            // if inside the medium
            if (normal.dot(ray.getDirection()) > 0) {
                normal = normal.multiply(-1);
                rIndex = 1/rIndex;
            }

            rIndex = 1/rIndex;
            
            double cosineDirection1 = -1 * normal.dot(ray.getDirection());
            double cosineDirection2 = 1.0 - rIndex*rIndex*(1.0-cosineDirection1*cosineDirection1);
            // Schlick approximation
            double fresnelFactorProbability = ratio + (1.0-ratio)* Math.pow(1.0-cosineDirection1, 5.0);
            // calculate refraction direction
            if (cosineDirection2 > 0 && uniformRand2() > fresnelFactorProbability) {
                ray.setDirection(ray.getDirection().multiply(rIndex).add(normal.multiply(rIndex*cosineDirection1-Math.sqrt(cosineDirection2))).norm());
            }else {
                // otherwise get the reflection index
                ray.setDirection(ray.getDirection().add(normal.multiply(cosineDirection1*2)).norm());
            }
            
            // create temporary color for recursive call-back
            DiffuseColor tempColor = new DiffuseColor(0,0,0);
            
            // call recursive trace
            trace(ray,scene,recursionDepth+1,tempColor,parameterList,halton1,halton2);
            
            // recursive collection/aggregation
            color = color.add(tempColor.multiply(1.15*rouletteFactor));
        }
        
    }
    
    int run() {
        return 1;
    }
}
