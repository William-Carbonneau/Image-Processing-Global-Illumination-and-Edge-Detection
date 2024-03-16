package edu.vanier.global_illumination_image_processing.rendering;

/**
 * Superclass for all objects in the 3D scene.
 * Objects in the scene have properties such as color, emission, and type (diffuse, specular, refractive).
 * All objects should be intersectable and able to compute their surface normals.
 * TODO docs
 * @author William Carbonneau
 */
public abstract class SceneObject {
    public DiffuseColor color; // Color of the object
    public double emission; // Emission value of the object
    public int type; // Type of the object (diffuse, specular, refractive)
    public double refractiveIndex; // TODO modify for refractive index per object likely this line: double rIndex = parameterList.get("refractiveIndex");

    /**
     * Sets the material properties of the object.
     * 
     * @param color The color of the object.
     * @param emission The emission value of the object.
     * @param type The type of the object.
     */
    public void setMaterial(DiffuseColor color, double emission, int type) {
        this.color = color;
        this.emission = emission;
        this.type = type;
    }

    /**
     * Computes the intersection of the object with a given ray.
     * 
     * @param intersectRay The ray to intersect with the object.
     * @return The distance along the ray to the intersection point
     */
    public abstract double intersect(Ray intersectRay);

    /**
     * Computes the surface normal of the object at a given point.
     * 
     * @param intersectPoint A point on the surface of the object.
     * @return The surface normal vector at the given point.
     */
    public abstract Vec3D normal(Vec3D intersectPoint);

    /**
     * get the color of this object
     * 
     * @return The color (DiffuseColor)
     */
    public DiffuseColor getColor() {
        return color;
    }

    /**
     * get the emission value of this object
     * This is the amount of light the object emits
     * 
     * @return The emissivity (double)
     */
    public double getEmission() {
        return emission;
    }

    /**
     * get the material type of this object
     * 1 Diffuse
     * 2 Specular
     * 3 Refractive
     * 
     * @return The type (int)
     */
    public int getType() {
        return type;
    }
}
