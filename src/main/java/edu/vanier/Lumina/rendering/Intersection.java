package edu.vanier.Lumina.rendering;

/**
 * Store the intersection of a ray and an object
 * @author William Carbonneau
 */
public class Intersection {
    
    /** A constant standing in for infinity */
    public static final double INF = 1e9;
    
    /** A small constant representing an epsilon value for numerical stability. */
    public static final double EPS = 1e-6;
    
    /** The distance from the ray's origin to the point on the intersection object. */
    private double intersectDistance;
    
    /** The scene object the ray intersects first. */
    private SceneObject object;
    
    /**
     * Constructor for a new Intersection object with default values.
     * The intersect distance is initialized to infinity and the intersected object is null.
     */
    public Intersection() {
        this.intersectDistance = INF;
        this.object = null;
    }
    
    /**
     * Constructor for a new Intersection object with defined distance and intersection object.
     * 
     * @param dist   The distance from the ray's origin to the intersection point.
     * @param object The scene object intersected by the ray.
     */
    public Intersection(double dist, SceneObject object) {
        this.intersectDistance = dist;
        this.object = object;
    }
    
    /**
     * Checks if this Intersection object contains an intersected object.
     * 
     * @return true if an object is present, else false.
     */
    public boolean containsObjectBool() {
        return object != null;
    }

    /**
     * Gets the distance from the ray's origin to the intersection point.
     * 
     * @return The intersect distance.
     */
    public double getIntersectDistance() {
        return intersectDistance;
    }

    /**
     * Gets the intersected object.
     * 
     * @return The intersected object.
     */
    public SceneObject getObject() {
        return object;
    }

    /**
     * Sets the distance from the ray's origin to the intersection point.
     * 
     * @param intersectDistance The intersect distance to set.
     */
    protected void setIntersectDistance(double intersectDistance) {
        this.intersectDistance = intersectDistance;
    }

    /**
     * Sets the intersected object.
     * 
     * @param object The intersected object to set.
     */
    protected void setObject(SceneObject object) {
        this.object = object;
    }
    
}
