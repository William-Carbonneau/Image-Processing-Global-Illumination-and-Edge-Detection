package edu.vanier.global_illumination_image_processing.rendering;
import edu.vanier.global_illumination_image_processing.rendering.objects.Plane;
import edu.vanier.global_illumination_image_processing.rendering.objects.Sphere;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.distribution.UniformRealDistribution;

/**
 * The main algorithm for 3D Global illumination rendering
 * @author William Carbonneau
 */
public class RenderingEquation {
    // create uniform random numbers
    private static final MersenneTwister twister = new MersenneTwister();
    private static final UniformRealDistribution uniRealDist = new UniformRealDistribution(twister, 0.0, 1.0);
    // width and height of render image/camera in pixels, cannot be static for dynamic adjustment
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
    * Generates a stratified (or not) sample point on a hemisphere for diffuse objects. TODO citation
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

        // stratified sampling, costly square roots, but improved sampling performance
        double radius = Math.sqrt(uniform1);
        double angle = 2*Math.PI*uniform2;
        double xPos = radius*Math.cos(angle);
        double yPos = radius*Math.sin(angle);
        
        return new Vec3D(xPos,yPos, Math.sqrt(Double.max(0.0, 1.0-uniform1)));
        
    }
    
    /**
    * Traces a ray through the scene, calculating the color contribution of each intersected object recursively.
    * Traces rays from the camera's viewpoint and calculates how they interact with objects in the scene.
    * 
    * @param ray             The ray to be traced.
    * @param scene           The scene containing the objects to be intersected by the ray.
    * @param recursionDepth  The current recursion depth, the number of recursive calls made by the method.
    * @param color           The color of the ray, which accumulates contributions from intersected objects.
    * @param parameterList   A HashMap containing additional parameters needed for certain calculations.
    * @param halton1         The first Halton sequence generator used for random number generation.
    * @param halton2         The second Halton sequence generator used for random number generation.
    */
    private static void trace(Ray ray, Scene scene, int recursionDepth, DiffuseColor color, Halton halton1, Halton halton2) {
        
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
        // type 1 is diffuse material
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
            

            ray.setDirection(rotatedDirection); // already normalized, no need for costly operation
            
            // intentional bug for psychedelic effect
//            ray.setDirection(normal);

            double cosineDirection = ray.getDirection().dot(normal);
            
            // create temporary color for recursive call-back
            DiffuseColor tempColor = new DiffuseColor(0,0,0);

            // call recursive trace
            trace(ray,scene,recursionDepth+1,tempColor,halton1,halton2);
            
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
            trace(ray,scene,recursionDepth+1,tempColor,halton1,halton2);
            
            // recursive collection/aggregation
            color.addToObject(tempColor.multiply(rouletteFactor));

        }
        
        // handle glass and refractive materials using snell's law and Fresnel's law
        // Calculate probability weights and outgoing direction        
        // type 3 is refractive
        if (intersect.getObject().getType() == 3) {
            
            // set up physical variables
            double rIndex = intersect.getObject().getRefractiveIndex();
            double ratio = (1.0-rIndex)/(1.0+rIndex);
            ratio = ratio*ratio; // square the ratio
            
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
            DiffuseColor tempColor = new DiffuseColor(0,0,0);
            
            // call recursive trace
            trace(ray,scene,recursionDepth+1,tempColor,halton1,halton2);
            
            // recursive collection/aggregation
            color.addToObject(tempColor.multiply(1.15*rouletteFactor));

        }
    }
    
    /**
     * Simulate for one pixel for all samples per pixel
     * 
     * @param SPP samples per pixel, type: int
     * @param halton1 Halton sequence 1, type: Halton
     * @param halton2 Halton sequence 2, type: Halton
     * @param scene the scene containing simulation, type Scene
     * @param pixels array of image pixels, type: DiffuseColor[][]
     */
    private static void simulatePerPixel(int column, int rowCam, int rowAdjusted, double SPP, Halton halton1, Halton halton2, Scene scene, DiffuseColor[][] pixels) {
        
//        System.out.print("rowAdjusted: " + rowAdjusted + ", ");
        // loop over samples per pixel
        for (int samples = 0; samples < SPP; samples++) {
            DiffuseColor colorMaster = new DiffuseColor(0,0,0);

            // create camera plane coordinates
            Vec3D camera = CamPlaneCoordinate(column, rowCam);
            // anti-aliasing of the camera coordinates
            camera.setX(camera.getX() + uniformRand()/700);
            camera.setY(camera.getY() + uniformRand()/700);
            // ray with direction from the origin to the camera plane (Where the camera is placed relative to its plane or lens)
            Ray ray = new Ray(new Vec3D(0,0,0), camera.subtract(new Vec3D(0,0,0)).norm());

            // trace the ray recursively
            trace(ray, scene, 0,colorMaster, halton1, halton2);

            // set the appropriate pixel
            pixels[column][rowAdjusted] = pixels[column][rowAdjusted].add(colorMaster.multiply(1/SPP));
        }
    }
    
    /**
     * Run the path tracer with parameters
     * Uses the trace() method
     * This also outputs the file as a ppm
     * 
     * @param samples sample per pixel for simulation
     * @return int run status non-zero is error
     */
    public static int run(double samples) {
        final Scene scene1 = new Scene();
        


        // add objects
        // Sphere: origin, radius, color, emission, type 
        scene1.addObj("Metal sphere 1", new Sphere(new Vec3D(-0.75,-1.45,-4.4), 1.05, new DiffuseColor(4, 8, 4), 0,2));
        scene1.addObj("Glass sphere 1", new Sphere(new Vec3D(2.0,-2.05,-3.7), 0.5, new DiffuseColor(10, 10, 1), 0,3));
        scene1.addObj("Diffuse sphere 1", new Sphere(new Vec3D(-1.75,-1.95,-3.1), 0.6, new DiffuseColor(4, 4, 12), 0,1));
        // Plane: normal, distance from origin to normal, color, emission, type
        scene1.addObj("bottom plane", new Plane(new Vec3D(0,1,0), 2.5, new DiffuseColor(6, 6, 6), 0,1)); // bottom plane
        scene1.addObj("back plane", new Plane(new Vec3D(0,0,1), 5.5, new DiffuseColor(6, 6, 6), 0,1)); // back plane
        scene1.addObj("left plane", new Plane(new Vec3D(1,0,0), 2.75, new DiffuseColor(10, 2, 2), 0,1)); // left plane
        scene1.addObj("right plane", new Plane(new Vec3D(-1,0,0), 2.75, new DiffuseColor(2, 10, 2), 0,1)); // right plane
        scene1.addObj("ceiling plane", new Plane(new Vec3D(0,-1,0), 3.0, new DiffuseColor(6, 6, 6), 0,1)); // ceiling plane
        scene1.addObj("front plane", new Plane(new Vec3D(0,0,-1), 0.5, new DiffuseColor(6, 6, 6), 0,1)); // front plane
        // add lights - use color and emissiveness and diffuse material for diffuse radial light
        scene1.addObj("light sphere 1", new Sphere(new Vec3D(0,1.9,-3), 0.5, new DiffuseColor(0, 0, 0), 10000,1));

        // modify objects in the scene, need to deal with possibility of null
        try {
            scene1.getObjectByName("Glass sphere 1").setRefractiveIndex(1.5);
        }catch(NullPointerException e) {
            System.out.println("Object not found to modify");
        }
        final Scene scene2 = new Scene();
        


        // add objects
        // Sphere: origin, radius, color, emission, type 
        scene2.addObj("Metal sphere 1", new Sphere(new Vec3D(-0.75,-1.45,-4.4), 1.05, new DiffuseColor(4, 8, 4), 0,2));
        scene2.addObj("Glass sphere 1", new Sphere(new Vec3D(2.0,-2.05,-3.7), 0.5, new DiffuseColor(10, 10, 1), 0,3));
        scene2.addObj("Diffuse sphere 1", new Sphere(new Vec3D(-1.75,-1.95,-3.1), 0.6, new DiffuseColor(4, 4, 12), 0,1));
        // Plane: normal, distance from origin to normal, color, emission, type
        scene2.addObj("bottom plane", new Plane(new Vec3D(0,1,0), 2.5, new DiffuseColor(6, 6, 6), 0,1)); // bottom plane
        scene2.addObj("back plane", new Plane(new Vec3D(0,0,1), 5.5, new DiffuseColor(6, 6, 6), 0,1)); // back plane
        scene2.addObj("left plane", new Plane(new Vec3D(1,0,0), 2.75, new DiffuseColor(10, 2, 2), 0,1)); // left plane
        scene2.addObj("right plane", new Plane(new Vec3D(-1,0,0), 2.75, new DiffuseColor(2, 10, 2), 0,1)); // right plane
        scene2.addObj("ceiling plane", new Plane(new Vec3D(0,-1,0), 3.0, new DiffuseColor(6, 6, 6), 0,1)); // ceiling plane
        scene2.addObj("front plane", new Plane(new Vec3D(0,0,-1), 0.5, new DiffuseColor(6, 6, 6), 0,1)); // front plane
        // add lights - use color and emissiveness and diffuse material for diffuse radial light
        scene2.addObj("light sphere 1", new Sphere(new Vec3D(0,1.9,-3), 0.5, new DiffuseColor(0, 0, 0), 10000,1));

        // modify objects in the scene, need to deal with possibility of null
        try {
            scene2.getObjectByName("Glass sphere 1").setRefractiveIndex(1.5);
        }catch(NullPointerException e) {
            System.out.println("Object not found to modify");
        }
        
        // start a clock
        final long startTime = System.currentTimeMillis();
        
        // TODO parallelize the coming loop
        
        int threadCount = 2;
        
        // create arrays for each thread
        final DiffuseColor[][] rowArray1 = new DiffuseColor[width][height/threadCount];
        final DiffuseColor[][] rowArray2 = new DiffuseColor[width][height/threadCount];
        
        
        // Instantiate each DiffuseColor object
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height/threadCount; j++) {
                rowArray1[i][j] = new DiffuseColor();
            }
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height/threadCount; j++) {
                rowArray2[i][j] = new DiffuseColor();
            }
        }
        

        System.out.println("Start Multithreading");
        
        ExecutorService execServ = Executors.newFixedThreadPool(threadCount);
        ArrayList<CompletableFuture<Void>> tasks = new ArrayList<>();
        // Un-Parallelized loop broken into columns using number of chosen threads
        
                // set samples per pixels
        final double SPP1 = samples;
        final double SPP2 = samples;
        
        final Halton halton11 = new Halton();
        final Halton halton21 = new Halton();
        final Halton halton12 = new Halton();
        final Halton halton22 = new Halton();
        halton11.number(0,2);
        halton21.number(0,2);
        halton12.number(0,2);
        halton22.number(0,2);
        
        
        CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
            for (int row = 0; row < height/threadCount; row++) {
                if (row % 10 == 0) System.out.println(row);

                for (int column = 0; column < width; column++) {
                    simulatePerPixel(column, row, row, SPP1, halton11, halton21, scene1, rowArray1);
                }
            }
        },execServ);
        
        CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> {
            for (int row = height/threadCount; row < height; row++) {
                if (row % 10 == 0) System.out.println(row);

                for (int column = 0; column < width; column++) {
                    simulatePerPixel(column, row, row - height/threadCount, SPP1, halton12, halton22, scene1, rowArray2);
                }
            }
        }, execServ);
        tasks.add(task1);
        tasks.add(task2);

        
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        execServ.shutdown();
        
        
        System.out.println("Render finished");
        System.out.println("Final time (milliseconnds): " + (System.currentTimeMillis() - startTime));
        
        File saveFile = new File("ray.bmp");
        
        // create buffered image
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        
            
        for (int row = 0; row < height/threadCount; row++) {
            for (int column = 0; column < width; column++) {
                output.setRGB(column, row, new Color(Integer.min((int)rowArray1[column][row].getR(),255), Integer.min((int)rowArray1[column][row].getG(),255), Integer.min((int)rowArray1[column][row].getB(),255)).getRGB());
            }
        }
        
        for (int row = 0; row < height/threadCount; row++) {
            for (int column = 0; column < width; column++) {
                output.setRGB(column, row+height/threadCount, new Color(Integer.min((int)rowArray2[column][row].getR(),255), Integer.min((int)rowArray2[column][row].getG(),255), Integer.min((int)rowArray2[column][row].getB(),255)).getRGB());
            }
        }
        
        
        try {
            ImageIO.write(output, "bmp", saveFile);
        }catch(Exception e) {
            e.printStackTrace();
        }
  
        return 0;
    }
}
