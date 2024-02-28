package edu.vanier.global_illumination_image_processing.rendering;

/**
 * A simple ray class representing a ray in 3D space.
 * A ray consists of an origin point and a direction vector. 
 * The direction vector is expected to be normalized.
 * 
 * @author William Carbonneau
 */
public class Ray {
    private Vec3D origin; // The origin point of the ray
    private Vec3D direction; // The direction vector of the ray

    /**
     * Constructs a new Ray object with the given origin and direction.
     * The direction vector is normalized to ensure consistent behavior.
     * 
     * @param origin The origin point of the ray.
     * @param direction The direction vector of the ray.
     */
    public Ray(Vec3D origin, Vec3D direction) {
        this.origin = origin;
        this.direction = direction.norm(); // Normalizing direction vector
    }

    /**
     * Retrieves the origin point of the ray.
     * 
     * @return The origin point of the ray.
     */
    public Vec3D getOrigin() {
        return origin;
    }

    /**
     * Sets the origin point of the ray.
     * 
     * @param origin The new origin point of the ray.
     */
    public void setOrigin(Vec3D origin) {
        this.origin = origin;
    }

    /**
     * Retrieves the direction vector of the ray.
     * 
     * @return The direction vector of the ray.
     */
    public Vec3D getDirection() {
        return direction;
    }

    /**
     * Sets the direction vector of the ray.
     * 
     * @param direction The new direction vector of the ray.
     */
    public void setDirection(Vec3D direction) {
        this.direction = direction.norm(); // Normalizing direction vector
    }
}
