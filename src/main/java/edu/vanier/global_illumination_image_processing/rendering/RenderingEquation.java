package edu.vanier.global_illumination_image_processing.rendering;
import edu.vanier.global_illumination_image_processing.rendering.objects.Plane;
import edu.vanier.global_illumination_image_processing.rendering.objects.Sphere;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    // width and height of render image/camera in pixels, cannot be static for dynamic adjustment TODO, add getters and setters
    private static int width = 800, height = 800;

    /**
     * Returns the width of the camera's viewport.
     * 
     * @return The width of the camera's viewport.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the camera's viewport.
     * 
     * @return The height of the camera's viewport.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the width of the camera's viewport.
     * 
     * @param widthParam The width to be set for the camera's viewport.
     */
    public void setWidth(int widthParam) {
        width = widthParam;
    }

    /**
     * Sets the height of the camera's viewport.
     * 
     * @param heightParam The height to be set for the camera's viewport.
     */
    public void setHeight(int heightParam) {
        height = heightParam;
    }
    
    /**
     * get uniform random numbers from -1.0 to 1.0
     * 
     * @return double
     */
    public static double uniformRand() {
        return 2.0 * uniRealDist.sample() - 1.0;
    }
    
    /**
     * get uniform random numbers from 0.0 to 1.0
     * 
     * @return double
     */
    public static double uniformRand2() {
        return uniRealDist.sample();
    }
    
    /**
     * Get the coordinates on the perspective camera plane
     * 
     * @param x
     * @param y
     * @return Vec3D
     */
    private static Vec3D CamPlaneCoordinate(double x, double y) {
        double w = width;
        double h = height;
        
        double FieldOfViewX = Math.PI/4;
        double FieldOfViewY = (h/w) * FieldOfViewX;
        
        return (new Vec3D(((2*x - w) / w)*Math.tan(FieldOfViewX), -((2*y - h) / h) * Math.tan(FieldOfViewY), -1.0));
    }
    
    /**
    * Generates a stratified sample point on a hemisphere for diffuse objects. TODO citation
    * 
    * This method implements stratified sampling of a hemisphere based on the algorithm provided by  http://www.rorydriscoll.com/2009/01/07/better-sampling/
    * It takes random radius and angle as parameters, ensuring an even ray distribution from the hemisphere to reduce noise in Monte Carlo simulations.
    * 
    * @param uniform1 The random radius parameter used for sampling the hemisphere.
    * @param uniform2  The random angle parameter used for sampling the hemisphere.
    * @return Vec3D representing the stratified sample point on the hemisphere.
    */
    private static Vec3D Hemisphere(double uniform1, double uniform2) {
/* // non-stratified sampler
        double radius = Math.sqrt(1.0 - (uniform1 * uniform1));
        double angle = 2 * Math.PI * uniform2;
        return new Vec3D(Math.cos(angle) * radius, Math.sin(angle) * radius, uniform1);
*/

        // stratified sampling, constly square roots, but improved sampling performance
        double radius = Math.sqrt(uniform1);
        double angle = 2*Math.PI*uniform2;
        double xPos = radius*Math.cos(angle);
        double yPos = radius*Math.sin(angle);
        
        return new Vec3D(xPos,yPos, Math.sqrt(Double.max(0.0, 1.0-uniform1)));
        
    }
    
    // TODO docs
    private static void trace(Ray ray, Scene scene, int recursionDepth, DiffuseColor color, HashMap<String, Double> parameterList, Halton halton1, Halton halton2) {
//        if (color.getR() > 0 || color.getG() > 0 || color.getB() > 0) System.out.printf("%f, %f, %f%n", color.getR(),color.getG(),color.getB());
        
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
        // check if interscetion exists, otherwise return
        if(!intersect.containsObjectBool()) return;
        // at this point we are sure to have an intersection
                
        
        //// trace a ray to the nearest intersction point then bounce
        Vec3D hitPoint = ray.getOrigin().add(ray.getDirection().multiply(intersect.getIntersectDistance()));
        // get the object normal at the intersection point
        Vec3D normal = intersect.getObject().normal(hitPoint);
                
        // set the ray origin to the hitpoint for the next iteration outward
        ray.setOrigin(hitPoint);

        
        // next handle emission from rendering equation [Le(x,w)], weighted by the roulette probability weight
        double emission = intersect.getObject().getEmission();
        DiffuseColor emissionColor = new DiffuseColor(emission, emission, emission);
        color.addToObject(emissionColor.multiply(rouletteFactor));
        
        // calculate the diffuse color by the hemisphere sampler
        // type 1 is diffuse material TODO, DETERMINED THAT DIFFUSE IS THE CAUSE OF THE SHADOW PROBLEMS
        if(intersect.getObject().getType() == 1) {
            // create an orthonormal system from the surface normal
            Vec3D rotationX = new Vec3D(0,0,0);
            Vec3D rotationY = new Vec3D(0,0,0);
            Vec3D.orthonormalSystem(normal, rotationX, rotationY);
            
            halton1.next();
            halton2.next();
            
            // intentional bug for line effect (reduce orthonormal system)
//            rotationY = new Vec3D(0,0,0);
            
            // get new direction from hemisphere sampler
            Vec3D sampleDirection = Hemisphere(uniformRand2(),uniformRand2());
            
            // intentional bug for swirl effect
//            Vec3D sampleDirection = Hemisphere(halton1.get(),halton2.get());
            
            // get new rotated ray direction from orthonormal system
            Vec3D rotatedDirection = new Vec3D(0,0,0);
            
            rotatedDirection.setX(new Vec3D(rotationX.getX(), rotationY.getX(), normal.getX()).dot(sampleDirection));
            rotatedDirection.setY(new Vec3D(rotationX.getY(), rotationY.getY(), normal.getY()).dot(sampleDirection));
            rotatedDirection.setZ(new Vec3D(rotationX.getZ(), rotationY.getZ(), normal.getZ()).dot(sampleDirection));
            

            ray.setDirection(rotatedDirection.norm()); // TODO costly normalize
            
            // intentional bug for psychedelic effect
//            ray.setDirection(normal);

            double cosineDirection = ray.getDirection().dot(normal);
            
            // create temporary color for recursive call-back
            DiffuseColor tempColor = new DiffuseColor(0,0,0);

            // call recursive trace
            trace(ray,scene,recursionDepth+1,tempColor,parameterList,halton1,halton2);
            
            // recursive collection/aggregation
            color.addToObject(tempColor.multiplyColor(intersect.getObject().getColor()).multiply(cosineDirection * 0.1 * rouletteFactor));
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
            color.addToObject(tempColor.multiply(rouletteFactor));

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
            
            double cosineDirection1 = -1.0 * normal.dot(ray.getDirection());
            double cosineDirection2 = 1.0 - (rIndex*rIndex*(1.0-(cosineDirection1*cosineDirection1)));
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
            DiffuseColor tempColor = new DiffuseColor(0,0,0); // TODO assume I need 1 here
            
            // call recursive trace
            trace(ray,scene,recursionDepth+1,tempColor,parameterList,halton1,halton2);
            
            // recursive collection/aggregation
            color.addToObject(tempColor.multiply(1.15*rouletteFactor));

        }
    }
    
    public static int run() {
        HashMap<String, Double> parameterList = new HashMap<>(); 
        Scene scene = new Scene();
        
        // set parameters of the simulation
        parameterList.put("refractiveIndex", 1.5); // set refractive index
        parameterList.put("spp", 16.0); // set samples per pixel
        
        double SPP = parameterList.get("spp");
        
        Halton halton1 = new Halton(), halton2 = new Halton();
        halton1.number(0,2);
        halton2.number(0,2);
        
        DiffuseColor[][] pixels = new DiffuseColor[width][height];
        
        // Instantiate each DiffuseColor object
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels[i][j] = new DiffuseColor();
            }
        }

        // add objects
        // Sphere: origin, radius, color, emission, type 
        scene.addObj(new Sphere(new Vec3D(-0.75,-1.45,-4.4), 1.05, new DiffuseColor(4, 8, 4), 0,2));
        scene.addObj(new Sphere(new Vec3D(2.0,-2.05,-3.7), 0.5, new DiffuseColor(10, 10, 1), 0,3));
        scene.addObj(new Sphere(new Vec3D(-1.75,-1.95,-3.1), 0.6, new DiffuseColor(4, 4, 12), 0,1));
        // Plane: normal, distance from origin to normal, color, emission, type
        scene.addObj(new Plane(new Vec3D(0,1,0), 2.5, new DiffuseColor(6, 6, 6), 0,1)); // bottom plane
        scene.addObj(new Plane(new Vec3D(0,0,1), 5.5, new DiffuseColor(6, 6, 6), 0,1)); // back plane
        scene.addObj(new Plane(new Vec3D(1,0,0), 2.75, new DiffuseColor(10, 2, 2), 0,1)); // left plane
        scene.addObj(new Plane(new Vec3D(-1,0,0), 2.75, new DiffuseColor(2, 10, 2), 0,1)); // right plane
        scene.addObj(new Plane(new Vec3D(0,-1,0), 3.0, new DiffuseColor(6, 6, 6), 0,1)); // ceiling plane
        scene.addObj(new Plane(new Vec3D(0,0,-1), 0.5, new DiffuseColor(6, 6, 6), 0,1)); // front plane
        // add lights - use color and emissiveness and diffuse material for diffuse radial light
        scene.addObj(new Sphere(new Vec3D(0,1.9,-3), 0.5, new DiffuseColor(0, 0, 0), 10000,1));

        // TODO start a clock
        
        // TODO parallelize the coming loop
        // loop over every pixel
        for (int column = 0; column < width; column++) {
            
            // print progress
            System.out.printf("\rRendering: %1.0f samples per pixel  %8.2f%%", SPP, ((double) column)/width * 100);

            for (int row = 0; row < width; row++) {
                // loop over samples per pixel
                for (int samples = 0; samples < SPP; samples++) {
                    DiffuseColor colorMaster = new DiffuseColor(0,0,0);
                    
                    // create camera plane coordinates
                    Vec3D camera = CamPlaneCoordinate(column, row);
                    // anti-aliasing of the camera coordinates
                    camera.setX(camera.getX() + uniformRand()/700);
                    camera.setY(camera.getY() + uniformRand()/700);
                    // ray with direction from the origin to the camera plane (Where the camera is placed relative to its plane or lens)
                    Ray ray = new Ray(new Vec3D(0,0,0), camera.subtract(new Vec3D(0,0,0)).norm());
                    
                    // trace the ray recursively
                    trace(ray, scene, 0,colorMaster, parameterList, halton1, halton2);
                    
//                    if (colorMaster.getR() > 0 || colorMaster.getG() > 0 || colorMaster.getB() > 0) System.out.printf("%f, %f, %f%n", colorMaster.getR(),colorMaster.getG(),colorMaster.getB());
//                    System.out.printf("%f, %f, %f%n", colorMaster.getR(),colorMaster.getG(),colorMaster.getB());
                    
                    // set the appropriate pixel
                    pixels[column][row] = pixels[column][row].add(colorMaster.multiply(1/SPP));
                }
            }
        }
        
        System.out.println("Render finished");
        
        try {
            FileWriter fileWriter = new FileWriter("ray.ppm");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            
            printWriter.printf("P3\n%d %d\n%d\n ",width,height,255);
            
            for (int row=0;row<height;row++) {
		for (int col=0;col<width;col++) {
			printWriter.printf("%d %d %d ", Math.min((int)pixels[col][row].getR(),255), Math.min((int)pixels[col][row].getG(),255), Math.min((int)pixels[col][row].getB(),255));
		}
		printWriter.printf("\n");
	}
            
        } catch (IOException ex) {
            Logger.getLogger(RenderingEquation.class.getName()).log(Level.SEVERE, null, ex);
        }
	
        
        return 0;
    }
}
