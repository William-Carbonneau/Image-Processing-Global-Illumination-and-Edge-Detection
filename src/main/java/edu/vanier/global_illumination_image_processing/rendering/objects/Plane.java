package edu.vanier.global_illumination_image_processing.rendering.objects;

import edu.vanier.global_illumination_image_processing.rendering.Intersection;
import edu.vanier.global_illumination_image_processing.rendering.Ray;
import edu.vanier.global_illumination_image_processing.rendering.SceneObject;
import edu.vanier.global_illumination_image_processing.rendering.Vec3D;
import edu.vanier.global_illumination_image_processing.rendering.DiffuseColor;

/**
 * Plane SceneObject represented mathematically
 * 
 * @author William Carbonneau
 */
public class Plane extends SceneObject{
    
    // getters and setters
    
    /**
     * Set the material color of the Plane
     * 
     * @param color DiffuseColor
     */
    public void setColor(DiffuseColor color) {
        this.color = color;
    }

    /**
     * Set the emission value of the Plane
     * 
     * @param emission double
     */
    public void setEmission(double emission) {
        this.emission = emission;
    }

    /**
     * Set the material type of the Plane
     * 1 = Diffuse, 2 = Reflective, 3 = Refractive
     * 
     * @param type int
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Get the material color of the Plane
     * 
     * @return color DiffuseColor
     */
    @Override
    public DiffuseColor getColor() {
        return color;
    }

    /**
     * Get the emission value of the Plane
     * 
     * @return emission double
     */
    @Override
    public double getEmission() {
        return emission;
    }

    /**
     * Get the material type of the Plane
     * 1 = Diffuse, 2 = Reflective, 3 = Refractive
     * 
     * @return type int
     */
    @Override
    public int getType() {
        return type;
    }

    /**
     * General Constructor
     * 
     * @param normal
     * @param distanceOrigin*/
    public Plane(Vec3D normal, double distanceOrigin) {
        this.normal = normal;
        this.distanceOrigin = distanceOrigin;
    }
    
    /**
     * Material Constructor
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
    
    /**
     * Calculate intersection of Plane with a Ray
     * 
     * @param intersectRay Ray
     * @return double, distance to origin of Ray from intersect point
     */
    @Override
    public double intersect(Ray intersectRay) {
        double d0 = getNormal().dot(intersectRay.getDirection());
        if (d0 != 0) {
            double temp = -1 * (((getNormal().dot(intersectRay.getOrigin()))+getDistanceOrigin()) / d0);
            return (temp > Intersection.EPS) ? temp : 0;
        }else {
            return 0;
        }
    }

    /**
     * Calculate the normal at a point, just gets it in the case of a Plane
     * 
     * @param intersectPoint
     * @return normal Vec3D
     */
    @Override
    public Vec3D normal(Vec3D intersectPoint) {
        return normal;
    }
    
}
