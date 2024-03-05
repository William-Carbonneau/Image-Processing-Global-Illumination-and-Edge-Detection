package edu.vanier.global_illumination_image_processing.rendering;

/**
 * Store the intersection of a ray and an object
 * @author William Carbonneau
 */
public class Intersection {
    public static final double INF = 1e9;
    public static final double EPS = 1e-6;
    private double intersectDistance;
    private SceneObject object;
    public Intersection() {
        this.intersectDistance = INF;
        this.object = null;
    }
    public Intersection(double dist, SceneObject object) {
        this.intersectDistance = dist;
        this.object = object;
    }
    public boolean containsObjectBool() {
        return object != null;
    }

    public double getIntersectDistance() {
        return intersectDistance;
    }

    public SceneObject getObject() {
        return object;
    }

    public void setIntersectDistance(double intersectDistance) {
        this.intersectDistance = intersectDistance;
    }

    public void setObject(SceneObject object) {
        this.object = object;
    }
    
}
