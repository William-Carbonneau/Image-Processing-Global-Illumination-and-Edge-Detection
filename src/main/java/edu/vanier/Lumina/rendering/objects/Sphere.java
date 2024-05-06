package edu.vanier.Lumina.rendering.objects;

import edu.vanier.Lumina.rendering.Intersection;
import edu.vanier.Lumina.rendering.Ray;
import edu.vanier.Lumina.rendering.SceneObject;
import edu.vanier.Lumina.rendering.Vec3D;
import edu.vanier.Lumina.rendering.DiffuseColor;

/**
 * Sphere SceneObject represented mathematically 
 * 
 * @author William Carbonneau
 */
public class Sphere extends SceneObject {

    /**
     * General Constructor
     * 
     * @param origin Vec3D
     * @param radius double
     */
    public Sphere(Vec3D origin, double radius) {
        this.normal = origin;
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
        this.normal = origin;
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
        Vec3D ray0MinusOrigin = (intersectRay.getOrigin().subtract(this.getNormal()));
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
        return intersectPoint.subtract(this.getNormal()).multiply(1.0/radius); // normalize by radius to avoid square root
    }
    
}
