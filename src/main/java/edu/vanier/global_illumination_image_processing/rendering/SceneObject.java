package edu.vanier.global_illumination_image_processing.rendering;

/**
 * Superclass for all objects in the 3D scene.
 * Objects in the scene have properties such as color, emission, and type (diffuse, specular, refractive).
 * All objects should be intersectable and able to compute their surface normals.
 * TODO docs
 * @author William Carbonneau
 */
public abstract class SceneObject {
    
    /** Color of the object (max value should be 12 for each component) */
    public DiffuseColor color;
    
    /** Emission value of the object */
    public double emission;
    
    /** Type of the object (diffuse, specular, refractive) */
    public int type;
    
    /** The refractive index for Snell's law, for refractive objects (type 3)*/
    public double refractiveIndex; // TODO modify for refractive index per object likely this line: double rIndex = parameterList.get("refractiveIndex");

    /** Distance to origin used by Plane */
    public double distanceOrigin = 0;
    
    /** radius used by Sphere */
    public double radius = 0;
    
    /** Base coordinate used by all objects */
    public Vec3D normal;
    
    /**
     * Sets the material properties of the object.
     * 
     * sets the refractive index default to 1.5 (glass)
     * 
     * @param color The color of the object.
     * @param emission The emission value of the object.
     * @param type The type of the object.
     */
    public void setMaterial(DiffuseColor color, double emission, int type) {
        this.color = color;
        this.emission = emission;
        this.type = type;
        this.refractiveIndex = 1.5;
    }
        
    /**
     * Set the normal vector of the Plane
     * 
     * @param newNormal Vec3D
     */
    public void setNormal(Vec3D newNormal) {
        this.normal = newNormal;
    }

    /**
     * Set the radius for Spheres
     * 
     * @param radius double
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }
    

    /**
     * Set the emission value
     * 
     * @param emission double
     */
    public void setEmission(double emission) {
        this.emission = emission;
    }

    /**
     * Set the color
     * 
     * @param color DiffuseColor
     */
    public void setColor(DiffuseColor color) {
        this.color = color;
    }

    /**
     * Set the type (1 = Diffuse, 2 = Reflective, 3 = Refractive)
     * 
     * @param type int
     */
    public void setType(int type) {
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
     * Get the radius for Sphere
     * 
     * @return double radius
     */
    public double getRadius() {
        return radius;
    }
        
    /**
     * Get the normal vector of the Plane
     * 
     * @return normal Vec3D
     */
    public Vec3D getNormal() {
        return normal;
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
    
     /**
     * Get the distance from the origin (only for some objects is this relevant otherwise 0)
     * 
     * @return double
     */
    public double getDistanceOrigin() {
        return distanceOrigin;
    }

    /**
     * Sets the refractive index property of the object.
     * 
     * @param refractiveIndex double
     */
    public void setRefractiveIndex(double refractiveIndex) {
        this.refractiveIndex = refractiveIndex;
    }
    
    /**
     * Set the distance of the Plane from the origin
     * 
     * @param newDistanceOrigin 
     */
    public void setDistanceOrigin(double newDistanceOrigin) {
        this.distanceOrigin = newDistanceOrigin;
    }

    /**
     * get the refractive index value of this object
     * This is the index by which the material refracts light
     * It is only relevant for type 3 objects
     * 
     * @return The emissivity (double)
     */
    public double getRefractiveIndex() {
        return refractiveIndex;
    }
    
    
}
