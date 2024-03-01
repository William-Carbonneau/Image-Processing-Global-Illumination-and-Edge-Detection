package edu.vanier.global_illumination_image_processing.rendering;

/**
 *
 * @author William Carbonneau
 */
public class Intersection {
    public static final double INF = 1e9;
    public static final double EPS = 1e-6;
    private final double intersectDistance;
    private final SceneObject object;
    public Intersection() {
        this.intersectDistance = INF;
        this.object = null;
    }
    public Intersection(double dist, SceneObject object) {
        this.intersectDistance = dist;
        this.object = object;
    }
    public boolean bool() {
        return object != null;
    }

    public double getIntersectDistance() {
        return intersectDistance;
    }

    public SceneObject getObject() {
        return object;
    }
}
