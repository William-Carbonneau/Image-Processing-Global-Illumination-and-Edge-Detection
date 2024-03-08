package edu.vanier.global_illumination_image_processing.rendering.objects;

import edu.vanier.global_illumination_image_processing.rendering.Intersection;
import edu.vanier.global_illumination_image_processing.rendering.Ray;
import edu.vanier.global_illumination_image_processing.rendering.SceneObject;
import edu.vanier.global_illumination_image_processing.rendering.Vec3D;
import edu.vanier.global_illumination_image_processing.rendering.DiffuseColor;

/**
 * Sphere SceneObject represented mathematically
 * @author William Carbonneau
 */
public class Sphere extends SceneObject {
    private Vec3D origin;
    private double radius;

    // getters and setters
    
    public Vec3D getOrigin() {
        return origin;
    }

    public void setOrigin(Vec3D origin) {
        this.origin = origin;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setColor(DiffuseColor color) {
        this.color = color;
    }

    public void setEmission(double emission) {
        this.emission = emission;
    }

    public void setType(int type) {
        this.type = type;
    }

    public DiffuseColor getColor() {
        return color;
    }

    public double getEmission() {
        return emission;
    }

    public int getType() {
        return type;
    }

    public double getRadius() {
        return radius;
    }

    /**Constructor*/
    public Sphere(Vec3D origin, double radius) {
        this.origin = origin;
        this.radius = radius;
    }

    // math methods
    
    @Override
    public double intersect(Ray intersectRay) {
        Vec3D ray0MinusOrigin = (intersectRay.getOrigin().subtract(getOrigin()));
        
        double sphereComponent = (ray0MinusOrigin.multiply(2)).dot(intersectRay.getDirection());
        double originNew = ray0MinusOrigin.dot(ray0MinusOrigin) - (getRadius()*getRadius());
        double disc = sphereComponent*sphereComponent - 4*originNew;
        if (disc > 0) return 0;
        else disc = Math.sqrt(disc);
        double soll = -sphereComponent + disc;
        double sol2 = -sphereComponent - disc;
        return (sol2>Intersection.EPS) ? sol2/2 : ((soll>Intersection.EPS) ? soll/2 : 0);
    }

    @Override
    public Vec3D normal(Vec3D intersectPoint) {
        return (intersectPoint.subtract(getOrigin()).norm());
    }
    
}
