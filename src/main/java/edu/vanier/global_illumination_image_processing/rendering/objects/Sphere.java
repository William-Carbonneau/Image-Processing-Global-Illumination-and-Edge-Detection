package edu.vanier.global_illumination_image_processing.rendering.objects;

import edu.vanier.global_illumination_image_processing.rendering.Intersection;
import edu.vanier.global_illumination_image_processing.rendering.Ray;
import edu.vanier.global_illumination_image_processing.rendering.SceneObject;
import edu.vanier.global_illumination_image_processing.rendering.Vec3D;
import edu.vanier.global_illumination_image_processing.rendering.DiffuseColor;

/**
 * Sphere SceneObject represented mathematically 
 * 
 * @author William Carbonneau
 */
public class Sphere extends SceneObject {
    private Vec3D origin;
    private double radius;

    // getters and setters

    /**
     * Set the origin of the Sphere
     * 
     * @param origin Vec3D
     */
    public void setOrigin(Vec3D origin) {
        this.origin = origin;
    }

    /**
     * Set the radius of the Sphere
     * 
     * @param radius double
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * Set the material color of the Sphere
     * 
     * @param color DiffuseColor
     */
    public void setColor(DiffuseColor color) {
        this.color = color;
    }

    /**
     * Set the emission value of the Sphere
     * 
     * @param emission double
     */
    public void setEmission(double emission) {
        this.emission = emission;
    }

    /**
     * Set the material type of the Sphere
     * 1 = Diffuse, 2 = Reflective, 3 = Refractive
     * 
     * @param type int
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Get the material color of the Sphere
     * 
     * @return color DiffuseColor
     */
    public DiffuseColor getColor() {
        return color;
    }

    /**
     * Get the emission value of the Sphere
     * 
     * @return emission double
     */
    public double getEmission() {
        return emission;
    }
    /**
     * Get the material type of the Sphere
     * 1 = Diffuse, 2 = Reflective, 3 = Refractive
     * 
     * @return type int
     */
    public int getType() {
        return type;
    }
    
    /**
     * Get the origin of the Sphere
     * 
     * @return origin Vec3D
     */
    public Vec3D getOrigin() {
        return origin;
    }
    
    /**
     * Get the radius of the Sphere
     * 
     * @return radius double
     */
    public double getRadius() {
        return radius;
    }

    /**
     * General Constructor
     * 
     * @param origin Vec3D
     * @param radius double
     */
    public Sphere(Vec3D origin, double radius) {
        this.origin = origin;
        this.radius = radius;
    }
    
    /**
     * Material Constructor
     * 
     * @param origin Vec3D
     * @param radius double
     * @param color DiffuseColor
     * @param emission double
     * @param type int
     */
    public Sphere(Vec3D origin, double radius, DiffuseColor color, double emission, int type) {
        this.origin = origin;
        this.radius = radius;
        setMaterial(color, emission,type);
    }

    // math methods
    
    /**
     * Get intersection of a Ray with the Sphere
     * @param intersectRay Ray
     * @return double, distance to origin of Ray from intersect point
     */
    @Override
    public double intersect(Ray intersectRay) {
        Vec3D ray0MinusOrigin = (intersectRay.getOrigin().subtract(this.getOrigin()));
        // solving quadratic formula
        double sphereComponent = ray0MinusOrigin.multiply(2).dot(intersectRay.getDirection());
        double originNew = ray0MinusOrigin.dot(ray0MinusOrigin) - (this.getRadius()*this.getRadius());
        double disc = sphereComponent*sphereComponent - 4*originNew;
        if (disc < 0) return 0; // check if discriminant is less then 0, return 0 is no relevant hits
        else disc = Math.sqrt(disc);
        double solutionl = -sphereComponent + disc;
        double solution2 = -sphereComponent - disc;
        return (solution2>Intersection.EPS) ? solution2/2 : ((solutionl>Intersection.EPS) ? solutionl/2 : 0);
    }
    
    /**
     * Calculate the normal of the Sphere at a point
     * 
     * @param intersectPoint Ray
     * @return normal Vec3D
     */
    @Override
    public Vec3D normal(Vec3D intersectPoint) {
        return intersectPoint.subtract(this.getOrigin()).multiply(1.0/radius); // normalize by radius to avoid square root
    }
    
}
