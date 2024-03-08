package edu.vanier.global_illumination_image_processing.rendering.objects;

import edu.vanier.global_illumination_image_processing.rendering.Intersection;
import edu.vanier.global_illumination_image_processing.rendering.Ray;
import edu.vanier.global_illumination_image_processing.rendering.SceneObject;
import edu.vanier.global_illumination_image_processing.rendering.Vec3D;
import edu.vanier.global_illumination_image_processing.rendering.DiffuseColor;

/**
 * Plane SceneObject represented mathematically
 * @author William Carbonneau
 */
public class Plane extends SceneObject{
    private Vec3D n;
    private double d;

    // getters and setters
    
    public Vec3D getN() {
        return n;
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

    public double getD() {
        return d;
    }

    public void setN(Vec3D n) {
        this.n = n;
    }

    public void setD(double d) {
        this.d = d;
    }

    /**Constructor*/
    public Plane(Vec3D n, double d) {
        this.n = n;
        this.d = d;
    }
    
    // math methods
    
    @Override
    public double intersect(Ray intersectRay) {
        double d0 = getN().dot(intersectRay.getDirection());
        if (d0 != 0) {
            double temp = -1 * (((getN().dot(intersectRay.getOrigin()))+getD()) / d0);
            return (temp > Intersection.EPS) ? temp : 0;
        }else {
            return 0;
        }
    }

    @Override
    public Vec3D normal(Vec3D intersectPoint) {
        return n;
    }
    
}
