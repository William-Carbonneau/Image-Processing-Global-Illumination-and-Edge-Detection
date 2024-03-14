package edu.vanier.global_illumination_image_processing.rendering.objects;

import edu.vanier.global_illumination_image_processing.rendering.Intersection;
import edu.vanier.global_illumination_image_processing.rendering.Ray;
import edu.vanier.global_illumination_image_processing.rendering.SceneObject;
import edu.vanier.global_illumination_image_processing.rendering.Vec3D;
import edu.vanier.global_illumination_image_processing.rendering.DiffuseColor;

/**
 * Plane SceneObject represented mathematically
 * TODO docs
 * @author William Carbonneau
 */
public class Plane extends SceneObject{
    private Vec3D normal;
    private double distanceOrigin;
    
    // getters and setters
    
    public Vec3D getNormal() {
        return normal;
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

    public double getDistance() {
        return distanceOrigin;
    }

    public void setNormal(Vec3D n) {
        this.normal = n;
    }

    public void setDirection(double d) {
        this.distanceOrigin = d;
    }

    /**General Constructor
     * @param normal
     * @param distanceOrigin*/
    public Plane(Vec3D normal, double distanceOrigin) {
        this.normal = normal;
        this.distanceOrigin = distanceOrigin;
    }
    
    /**Material Constructor
     * @param normal
     * @param distanceOrigin
     * @param color
     * @param emission
     * @param type*/
    public Plane(Vec3D normal, double distanceOrigin, DiffuseColor color, double emission, int type) {
        this.normal = normal;
        this.distanceOrigin = distanceOrigin;
        this.type = type;
        setMaterial(color, emission, type);
    }
    
    // math methods
    
    @Override
    public double intersect(Ray intersectRay) {
        double d0 = getNormal().dot(intersectRay.getDirection());
        if (d0 != 0) {
            double temp = -1 * (((getNormal().dot(intersectRay.getOrigin()))+getDistance()) / d0);
            return (temp > Intersection.EPS) ? temp : 0;
        }else {
            return 0;
        }
    }

    @Override
    public Vec3D normal(Vec3D intersectPoint) {
        return normal;
    }
    
}
