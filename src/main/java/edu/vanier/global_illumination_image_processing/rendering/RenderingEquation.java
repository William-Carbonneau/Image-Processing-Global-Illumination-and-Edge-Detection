package edu.vanier.global_illumination_image_processing.rendering;

/**
 * The main algorithm for 3D Global illumination rendering
 * 
 * @author William Carbonneau
 */
public class RenderingEquation {
    
    /** width and height of render image/camera in pixels, cannot be static for dynamic adjustment */
    private int width, height;
    /** The Scene to render */
    private Scene scene;
    /** random instance for general use */
    private UniformRandom rand;
    
    /** Halton samplers */
    public final Halton halton1;
    public final Halton halton2;
    
    /**
     * Constructor with width, height and Scene
     * 
     * @param width int
     * @param height int
     * @param scene Scene
     */
    public RenderingEquation(int width, int height, Scene scene) {
        this.width = width;
        this.height = height;
        this.scene = scene;
        this.rand = new UniformRandom();
        this.halton1 = new Halton();
        this.halton2 = new Halton();
    }

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
     * Get the coordinates on the perspective camera plane
     * 
     * @param x
     * @param y
     * @return Vec3D
     */
    private Vec3D CamPlaneCoordinate(double x, double y) {
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
    private Vec3D Hemisphere(double uniform1, double uniform2, boolean stratified) {
        if (stratified) {
            // stratified sampling, costly square roots, but improved sampling performance
            double radius = Math.sqrt(uniform1);
            double angle = 2*Math.PI*uniform2;
            double xPos = radius*Math.cos(angle);
            double yPos = radius*Math.sin(angle);

            return new Vec3D(xPos,yPos, Math.sqrt(Double.max(0.0, 1.0-uniform1)));
        }else {
            // non-stratified sampler
            double radius = Math.sqrt(1.0 - (uniform1 * uniform1));
            double angle = 2 * Math.PI * uniform2;
            return new Vec3D(Math.cos(angle) * radius, Math.sin(angle) * radius, uniform1);
        }
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
    private void trace(Ray ray, Scene scene, int recursionDepth, DiffuseColor color, Halton halton1, Halton halton2, boolean stratified) {
        
        // russian roulette recursion
        double rouletteFactor = 1.0;
        
        // exit condition
        if (recursionDepth >= 5) {
            double rouletteStopProbability = 0.1;
            if (rand.uniformRand2() <= rouletteStopProbability) {
                // if we meet the exit probablilistic condition return from recursion
                return;
            }
            // adjust the roulette factor for weighting results
            rouletteFactor = 1.0 / (1.0 - rouletteStopProbability);
        }
        
        // get intersection of scene and ray
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
            Vec3D sampleDirection = Hemisphere(rand.uniformRand2(),rand.uniformRand2(), stratified);
            
            // intentional bug for swirl effect
//            Vec3D sampleDirection = Hemisphere(halton1.get(),halton2.get());
            
            // get new rotated ray direction from orthonormal system
            Vec3D rotatedDirection = new Vec3D(0,0,0);
            
            rotatedDirection.setX(new Vec3D(rotationX.getX(), rotationY.getX(), normal.getX()).dot(sampleDirection));
            rotatedDirection.setY(new Vec3D(rotationX.getY(), rotationY.getY(), normal.getY()).dot(sampleDirection));
            rotatedDirection.setZ(new Vec3D(rotationX.getZ(), rotationY.getZ(), normal.getZ()).dot(sampleDirection));
            
            ray.setDirection(rotatedDirection); // already normalized, no need for costly square root operation
            
            // intentional bug for psychedelic effect
//            ray.setDirection(normal);

            double cosineDirection = ray.getDirection().dot(normal);
            
            // create temporary color for recursive call-back
            DiffuseColor tempColor = new DiffuseColor(0,0,0);

            // call recursive trace
            trace(ray,scene,recursionDepth+1,tempColor,halton1,halton2,stratified);
            
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
            trace(ray,scene,recursionDepth+1,tempColor,halton1,halton2,stratified);
            
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

            // reciprocal index
            rIndex = 1/rIndex;
            
            double cosineDirection1 = -1.0 * normal.dot(ray.getDirection());
            double cosineDirection2 = 1.0 - (rIndex*rIndex*(1.0-(cosineDirection1*cosineDirection1)));
            // Schlick approximation
            double fresnelFactorProbability = ratio + (1.0-ratio)* Math.pow(1.0-cosineDirection1, 5.0);
            // calculate refraction direction
            if (cosineDirection2 > 0 && rand.uniformRand2() > fresnelFactorProbability) {
                ray.setDirection(ray.getDirection().multiply(rIndex).add(normal.multiply(rIndex*cosineDirection1-Math.sqrt(cosineDirection2))).norm());
            }else {
                // otherwise get the reflection index
                ray.setDirection(ray.getDirection().add(normal.multiply(cosineDirection1*2)).norm());
            }
            
            // create temporary color for recursive call-back
            DiffuseColor tempColor = new DiffuseColor(0,0,0);
            
            // call recursive trace
            trace(ray,scene,recursionDepth+1,tempColor,halton1,halton2,stratified);
            
            // recursive collection/aggregation
            color.addToObject(tempColor.multiply(1.15*rouletteFactor));

        }
    }
    
    /**
     * Simulate for one pixel for all samples per pixel
     * 
     * @param column image column for ray
     * @param rowCam image row in camera for ray
     * @param rowAdjusted image row adjusted in array
     * @param SPP samples per pixel, type: int
     * @param halton1 Halton sequence 1, type: Halton
     * @param halton2 Halton sequence 2, type: Halton
     * @param scene the scene containing simulation, type Scene
     * @param pixels array of image pixels, type: DiffuseColor[][]
     */
    public void simulatePerPixel(int column, int rowCam, int rowAdjusted, double SPP, Halton halton1, Halton halton2, Scene scene, DiffuseColor[][] pixels, boolean stratified) {
        
        // loop over samples per pixel
        for (int samples = 0; samples < SPP; samples++) {
            DiffuseColor colorMaster = new DiffuseColor(0,0,0);

            // create camera plane coordinates
            Vec3D camera = CamPlaneCoordinate(column, rowCam);
            // anti-aliasing of the camera coordinates
            camera.setX(camera.getX() + rand.uniformRand()/700);
            camera.setY(camera.getY() + rand.uniformRand()/700);
            // ray with direction from the origin to the camera plane (Where the camera is placed relative to its plane or lens)
            Ray ray = new Ray(new Vec3D(0,0,0), camera.subtract(new Vec3D(0,0,0)).norm());

            // trace the ray recursively
            trace(ray, scene, 0,colorMaster, halton1, halton2, stratified);

            // set the appropriate pixel
            pixels[column][rowAdjusted] = pixels[column][rowAdjusted].add(colorMaster.multiply(1/SPP));
        }
    }
}
